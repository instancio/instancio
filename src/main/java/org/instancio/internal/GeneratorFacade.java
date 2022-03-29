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

import org.instancio.internal.handlers.ArrayNodeHandler;
import org.instancio.internal.handlers.CollectionNodeHandler;
import org.instancio.internal.handlers.InstantiatingHandler;
import org.instancio.internal.handlers.MapNodeHandler;
import org.instancio.internal.handlers.NodeHandler;
import org.instancio.internal.handlers.UserSuppliedGeneratorHandler;
import org.instancio.internal.handlers.UsingGeneratorResolverHandler;
import org.instancio.internal.nodes.ClassNode;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.random.RandomProvider;
import org.instancio.internal.reflection.ImplementationResolver;
import org.instancio.internal.reflection.InterfaceImplementationResolver;
import org.instancio.internal.reflection.instantiation.Instantiator;
import org.instancio.util.ReflectionUtils;
import org.instancio.util.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Optional;

class GeneratorFacade {
    private static final Logger LOG = LoggerFactory.getLogger(GeneratorFacade.class);

    private final AncestorTree ancestorTree = new AncestorTree();
    private final ImplementationResolver implementationResolver = new InterfaceImplementationResolver();
    private final ModelContext<?> context;
    private final RandomProvider random;
    private final NodeHandler[] nodeHandlers;

    public GeneratorFacade(final ModelContext<?> context) {
        this.context = context;
        this.random = context.getRandomProvider();

        final GeneratorResolver generatorResolver = new GeneratorResolver(context);
        final Instantiator instantiator = new Instantiator();

        this.nodeHandlers = new NodeHandler[]{
                new UserSuppliedGeneratorHandler(context, generatorResolver, instantiator),
                new ArrayNodeHandler(generatorResolver),
                new UsingGeneratorResolverHandler(context, generatorResolver),
                new CollectionNodeHandler(context, instantiator),
                new MapNodeHandler(context, instantiator),
                new InstantiatingHandler(context, instantiator)
        };
    }

    GeneratorResult generateNodeValue(final Node node, @Nullable final Object owner) {
        if (owner != null) {
            final Object ancestor = ancestorTree.getObjectAncestor(owner, node.getParent());
            if (ancestor != null) {
                LOG.debug("{} has a circular dependency to {}. Not setting field value.",
                        owner.getClass().getSimpleName(), ancestor);

                return GeneratorResult.nullResult();
            }
        }

        if (shouldReturnNullForNullable(node)) {
            return GeneratorResult.nullResult();
        }

        Optional<GeneratorResult> generatorResult = Optional.empty();
        for (NodeHandler handler : nodeHandlers) {
            generatorResult = handler.getResult(node);
            if (generatorResult.isPresent()) {
                ancestorTree.setObjectAncestor(generatorResult.get().getValue(), new AncestorTree.AncestorTreeNode(owner, node.getParent()));
                break;
            }
        }

        if (!generatorResult.isPresent()) {
            final Class<?> effectiveType = context.getSubtypeMapping(node.getKlass());
            generatorResult = resolveImplementationAndGenerate(effectiveType, node, owner);
        }

        return generatorResult.orElse(GeneratorResult.nullResult());
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

        Class<?> implementor = implementationResolver.resolve(abstractType).orElse(null);
        if (implementor == null) {
            LOG.debug("Interface '{}' has no implementation", abstractType.getName());
            return Optional.empty();
        }
        Node implementorNode = new ClassNode(parentNode.getNodeContext(), implementor, null, null, parentNode);
        return Optional.of(generateNodeValue(implementorNode, owner));
    }

    private boolean shouldReturnNullForNullable(final Node node) {
        final boolean precondition = context.isNullable(node.getField()) || context.isNullable(node.getKlass());
        return random.diceRoll(precondition);
    }
}
