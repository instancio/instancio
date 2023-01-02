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
import org.instancio.internal.nodes.Node;
import org.instancio.internal.reflection.instantiation.Instantiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.Optional;

class GeneratorFacade {
    private static final Logger LOG = LoggerFactory.getLogger(GeneratorFacade.class);

    private final ModelContext<?> context;
    private final Random random;
    private final NodeHandler[] nodeHandlers;

    GeneratorFacade(final ModelContext<?> context) {
        this.context = context;
        this.random = context.getRandom();

        final GeneratorResolver generatorResolver = new GeneratorResolver(
                new GeneratorContext(context.getSettings(), random));

        final Instantiator instantiator = new Instantiator();

        this.nodeHandlers = new NodeHandler[]{
                new UserSuppliedGeneratorHandler(context, generatorResolver, instantiator),
                new ArrayNodeHandler(context, generatorResolver),
                new UsingGeneratorResolverHandler(context, generatorResolver),
                new CollectionNodeHandler(context, instantiator),
                new MapNodeHandler(context, instantiator),
                new InstantiatingHandler(instantiator)
        };
    }

    private boolean isIgnored(final Node node) {
        return context.isIgnored(node) || (node.getField() != null && Modifier.isStatic(node.getField().getModifiers()));
    }

    Optional<GeneratorResult> generateNodeValue(final Node node) {
        if (isIgnored(node)) {
            return Optional.empty();
        }

        if (shouldReturnNullForNullable(node)) {
            return Optional.of(GeneratorResult.nullResult());
        }

        Optional<GeneratorResult> generatorResult = Optional.empty();
        for (NodeHandler handler : nodeHandlers) {
            generatorResult = handler.getResult(node);
            if (generatorResult.isPresent()) {
                LOG.trace("{} generated using '{}'", node, handler.getClass().getName());
                break;
            }
        }

        return generatorResult;
    }

    private boolean shouldReturnNullForNullable(final Node node) {
        final boolean precondition = context.isNullable(node);
        return random.diceRoll(precondition);
    }
}
