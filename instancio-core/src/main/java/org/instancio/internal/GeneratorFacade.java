/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.internal;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorResolver;
import org.instancio.generator.GeneratorResult;
import org.instancio.internal.AncestorTree.AncestorTreeNode;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.handlers.ArrayNodeHandler;
import org.instancio.internal.handlers.CollectionNodeHandler;
import org.instancio.internal.handlers.InstantiatingHandler;
import org.instancio.internal.handlers.MapNodeHandler;
import org.instancio.internal.handlers.NodeHandler;
import org.instancio.internal.handlers.UserSuppliedGeneratorHandler;
import org.instancio.internal.handlers.UsingGeneratorResolverHandler;
import org.instancio.internal.nodes.ClassNode;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.reflection.ImplementationResolver;
import org.instancio.internal.reflection.NoopImplementationResolver;
import org.instancio.internal.reflection.instantiation.Instantiator;
import org.instancio.util.ReflectionUtils;
import org.instancio.util.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class GeneratorFacade {
    private static final Logger LOG = LoggerFactory.getLogger(GeneratorFacade.class);

    private final AncestorTree ancestorTree = new AncestorTree();
    private final ImplementationResolver implementationResolver = new NoopImplementationResolver();
    private final ModelContext<?> context;
    private final Random random;
    private final NodeHandler[] nodeHandlers;
    private final List<GenerationListener> listeners = new ArrayList<>();

    public GeneratorFacade(final ModelContext<?> context) {
        this.context = context;
        this.random = context.getRandom();

        final GeneratorContext generatorContext = new GeneratorContext(context.getSettings(), random);
        final GeneratorResolver generatorResolver = new GeneratorResolver(generatorContext);
        final Instantiator instantiator = new Instantiator();

        this.nodeHandlers = new NodeHandler[]{
                new UserSuppliedGeneratorHandler(context, generatorContext, generatorResolver, instantiator),
                new ArrayNodeHandler(context, generatorResolver),
                new UsingGeneratorResolverHandler(context, generatorResolver),
                new CollectionNodeHandler(context, instantiator),
                new MapNodeHandler(context, instantiator),
                new InstantiatingHandler(context, instantiator)
        };
    }

    public void addGenerationListener(final GenerationListener listener) {
        listeners.add(listener);
    }

    private boolean isIgnored(final Node node) {
        return context.isIgnored(node) || (node.getField() != null && Modifier.isStatic(node.getField().getModifiers()));
    }

    Optional<GeneratorResult> generateNodeValue(final Node node, @Nullable final Object owner) {
        final Optional<GeneratorResult> generatorResult = generateInternal(node, owner);
        final Object generatedObject = generatorResult.map(GeneratorResult::getValue).orElse(null);
        notifyListeners(node, generatedObject);
        return generatorResult;
    }

    private void notifyListeners(final Node node, @Nullable final Object generatedObject) {
        listeners.forEach(it -> it.objectCreated(node, generatedObject));
    }

    private Optional<GeneratorResult> generateInternal(final Node node, @Nullable final Object owner) {
        if (isIgnored(node)) {
            return Optional.empty();
        }

        if (owner != null) {
            final Object ancestor = ancestorTree.getObjectAncestor(owner, node.getParent());
            if (ancestor != null) {
                LOG.debug("{} has a circular dependency to {}. Ignoring {}",
                        owner.getClass().getSimpleName(), ancestor, node);

                return Optional.of(GeneratorResult.nullResult());
            }
        }

        if (shouldReturnNullForNullable(node)) {
            return Optional.of(GeneratorResult.nullResult());
        }

        Optional<GeneratorResult> generatorResult = Optional.empty();
        for (NodeHandler handler : nodeHandlers) {
            generatorResult = handler.getResult(node);
            if (generatorResult.isPresent()) {
                ancestorTree.setObjectAncestor(generatorResult.get().getValue(), new AncestorTreeNode(owner, node.getParent()));
                break;
            }
        }

        if (!generatorResult.isPresent()) {
            final Class<?> effectiveType = context.getSubtypeMapping(node.getTargetClass());
            generatorResult = resolveImplementationAndGenerate(effectiveType, node, owner);
        }

        return generatorResult;
    }

    /**
     * Resolve an implementation class for the given interface and attempt to generate it.
     * This method should not be called for JDK classes, such as Collection interfaces.
     */
    private Optional<GeneratorResult> resolveImplementationAndGenerate(final Class<?> abstractType,
                                                                       final Node parentNode,
                                                                       @Nullable final Object owner) {
        Verify.isFalse(ReflectionUtils.isConcrete(abstractType),
                "Expecting an interface or abstract class: %s", abstractType.getName());
        Verify.isNotArrayCollectionOrMap(abstractType);

        LOG.debug("No generator for interface '{}'", abstractType.getName());

        final Optional<Class<?>> targetClass = implementationResolver.resolve(abstractType);
        if (targetClass.isPresent()) {
            final Node implementorNode = new ClassNode(parentNode.getNodeContext(),
                    targetClass.get(), parentNode.getField(), null, parentNode);

            return generateNodeValue(implementorNode, owner);
        }

        return Optional.empty();
    }

    private boolean shouldReturnNullForNullable(final Node node) {
        final boolean precondition = context.isNullable(node);
        return random.diceRoll(precondition);
    }
}
