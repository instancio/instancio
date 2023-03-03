/*
 * Copyright 2022-2023 the original author or authors.
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
import org.instancio.internal.beanvalidation.BeanValidationProcessor;
import org.instancio.internal.beanvalidation.NoopBeanValidationProvider;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResolver;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.handlers.ArrayNodeHandler;
import org.instancio.internal.handlers.CollectionNodeHandler;
import org.instancio.internal.handlers.InstantiatingHandler;
import org.instancio.internal.handlers.MapNodeHandler;
import org.instancio.internal.handlers.NodeHandler;
import org.instancio.internal.handlers.UserSuppliedGeneratorHandler;
import org.instancio.internal.handlers.UsingGeneratorResolverHandler;
import org.instancio.internal.instantiation.Instantiator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeKind;
import org.instancio.settings.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;

class GeneratorFacade {
    private static final Logger LOG = LoggerFactory.getLogger(GeneratorFacade.class);

    private final ModelContext<?> context;
    private final Random random;
    private final NodeHandler[] nodeHandlers;

    GeneratorFacade(final ModelContext<?> context) {
        this.context = context;
        this.random = context.getRandom();

        final GeneratorContext generatorContext = new GeneratorContext(
                context.getSettings(), random);

        final GeneratorResolver generatorResolver = new GeneratorResolver(
                generatorContext, context.getServiceProviders().getGeneratorProviders());

        final Instantiator instantiator = new Instantiator(
                context.getServiceProviders().getTypeInstantiators());

        final GeneratorSpecProcessor beanValidationProcessor = getGeneratorSpecProcessor();

        this.nodeHandlers = new NodeHandler[]{
                new UserSuppliedGeneratorHandler(context, generatorResolver, instantiator),
                new ArrayNodeHandler(context, generatorResolver, beanValidationProcessor),
                new UsingGeneratorResolverHandler(context, generatorResolver, beanValidationProcessor),
                new CollectionNodeHandler(context, beanValidationProcessor),
                new MapNodeHandler(context, beanValidationProcessor),
                new InstantiatingHandler(instantiator)
        };
    }

    private GeneratorSpecProcessor getGeneratorSpecProcessor() {
        final boolean isEnabled = context.getSettings().get(Keys.BEAN_VALIDATION_ENABLED);
        LOG.trace("Keys.BEAN_VALIDATION_ENABLED={}", isEnabled);
        return isEnabled ? new BeanValidationProcessor() : new NoopBeanValidationProvider();
    }

    private boolean hasStaticField(final InternalNode node) {
        return node.getField() != null && Modifier.isStatic(node.getField().getModifiers());
    }

    GeneratorResult generateNodeValue(final InternalNode node) {
        if (node.is(NodeKind.IGNORED) || hasStaticField(node)) {
            return GeneratorResult.ignoredResult();
        }

        if (shouldReturnNullForNullable(node)) {
            return GeneratorResult.nullResult();
        }

        GeneratorResult generatorResult = GeneratorResult.emptyResult();
        for (NodeHandler handler : nodeHandlers) {
            generatorResult = handler.getResult(node);
            if (!generatorResult.isEmpty()) {
                LOG.trace("{} generated using '{}'", node, handler.getClass().getName());
                break;
            }
        }

        return generatorResult;
    }

    private boolean shouldReturnNullForNullable(final InternalNode node) {
        final boolean precondition = context.isNullable(node);
        return random.diceRoll(precondition);
    }
}
