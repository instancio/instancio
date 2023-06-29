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

import org.instancio.generator.GeneratorContext;
import org.instancio.internal.assignment.InternalAssignment;
import org.instancio.internal.beanvalidation.BeanValidationProcessor;
import org.instancio.internal.beanvalidation.NoopBeanValidationProvider;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResolver;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.handlers.AssignmentNodeHandler;
import org.instancio.internal.handlers.CollectionNodeHandler;
import org.instancio.internal.handlers.InstantiatingHandler;
import org.instancio.internal.handlers.MapNodeHandler;
import org.instancio.internal.handlers.NodeHandler;
import org.instancio.internal.handlers.UserSuppliedGeneratorHandler;
import org.instancio.internal.handlers.UsingGeneratorResolverHandler;
import org.instancio.internal.instantiation.Instantiator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.settings.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

class GeneratorFacade {
    private static final Logger LOG = LoggerFactory.getLogger(GeneratorFacade.class);

    private final ModelContext<?> context;
    private final NodeHandler[] nodeHandlers;
    private final AssignmentNodeHandler assignmentNodeHandler;

    GeneratorFacade(final ModelContext<?> context, final GeneratedObjectStore generatedObjectStore) {
        this.context = context;

        final GeneratorContext generatorContext = new GeneratorContext(
                context.getSettings(), context.getRandom());

        final GeneratorSpecProcessor beanValidationProcessor = getGeneratorSpecProcessor();

        final GeneratorResolver generatorResolver = new GeneratorResolver(
                generatorContext, context.getServiceProviders().getGeneratorProviders());

        final Instantiator instantiator = new Instantiator(
                context.getServiceProviders().getTypeInstantiators());

        assignmentNodeHandler = new AssignmentNodeHandler(
                context, generatedObjectStore, generatorResolver, instantiator);

        this.nodeHandlers = new NodeHandler[]{
                assignmentNodeHandler,
                new UserSuppliedGeneratorHandler(context, generatorResolver, instantiator),
                new UsingGeneratorResolverHandler(context, generatorResolver, beanValidationProcessor),
                new CollectionNodeHandler(context, beanValidationProcessor),
                new MapNodeHandler(context, beanValidationProcessor),
                new InstantiatingHandler(instantiator)
        };
    }

    Set<InternalAssignment> getUnresolvedAssignments() {
        return assignmentNodeHandler.getUnresolvedAssignments();
    }

    private GeneratorSpecProcessor getGeneratorSpecProcessor() {
        final boolean isEnabled = context.getSettings().get(Keys.BEAN_VALIDATION_ENABLED);
        LOG.trace("Keys.BEAN_VALIDATION_ENABLED={}", isEnabled);
        return isEnabled ? new BeanValidationProcessor() : new NoopBeanValidationProvider();
    }

    private boolean shouldReturnNullForNullable(final InternalNode node) {
        final boolean precondition = context.isNullable(node);
        return context.getRandom().diceRoll(precondition);
    }

    GeneratorResult generateNodeValue(final InternalNode node) {
        GeneratorResult result = GeneratorResult.emptyResult();

        if (node.isIgnored()) {
            result = GeneratorResult.ignoredResult();
        } else if (node.isCyclic()) {
            // Cyclic nodes can only be set via an assignment
            result = assignmentNodeHandler.getResult(node);
        } else if (shouldReturnNullForNullable(node)) {
            result = GeneratorResult.nullResult();
        } else {
            for (NodeHandler handler : nodeHandlers) {
                result = handler.getResult(node);

                if (!result.isEmpty() || result.isDelayed()) {
                    break;
                }
            }
        }

        LOG.trace("{} - {}", node, result);
        return result;
    }
}
