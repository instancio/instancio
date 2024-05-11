/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.internal.generation;

import org.instancio.exception.InstancioTerminatingException;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.assignment.InternalAssignment;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResolver;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.generator.SpiGeneratorResolver;
import org.instancio.internal.instantiation.Instantiator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.Format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GeneratorFacade {
    private static final Logger LOG = LoggerFactory.getLogger(GeneratorFacade.class);

    private final ModelContext<?> context;
    private final AssignmentNodeHandler assignmentNodeHandler;
    private final NodeHandler userSuppliedGeneratorHandler;
    private final GeneratedPojoStore generatedPojoStore;
    private final List<NodeHandler> nodeHandlers = new ArrayList<>();

    public GeneratorFacade(final ModelContext<?> context, final AssigmentObjectStore assigmentObjectStore) {
        this.context = context;
        this.generatedPojoStore = GeneratedPojoStore.createStore(context);

        final GeneratorContext generatorContext = new GeneratorContext(
                context.getSettings(), context.getRandom());

        final GeneratorResolver generatorResolver = new GeneratorResolver(generatorContext);
        final SpiGeneratorResolver spiGeneratorResolver = new SpiGeneratorResolver(
                context, generatorContext, generatorResolver);

        final Instantiator instantiator = new Instantiator(
                context.getServiceProviders().getTypeInstantiators());

        final UserSuppliedGeneratorProcessor userSuppliedGeneratorProcessor = new UserSuppliedGeneratorProcessor(
                context, generatorResolver, spiGeneratorResolver, instantiator);

        assignmentNodeHandler = AssignmentNodeHandler.create(context, assigmentObjectStore, userSuppliedGeneratorProcessor);
        userSuppliedGeneratorHandler = UserSuppliedGeneratorHandler.create(context, userSuppliedGeneratorProcessor);

        // handlers in order of precedence, starting from highest
        addHandler(assignmentNodeHandler);
        addHandler(userSuppliedGeneratorHandler);
        addHandler(new SpiGeneratorNodeHandler(context, spiGeneratorResolver));
        addHandler(AnnotationNodeHandler.create(context, generatorResolver));
        addHandler(new UsingGeneratorResolverHandler(context, generatorResolver));
        addHandler(new InstantiatingHandler(instantiator));
    }

    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    private void addHandler(final NodeHandler handler) {
        if (handler != NodeHandler.NOOP_HANDLER) {
            nodeHandlers.add(handler);
        }
    }

    public GeneratorResult generateNodeValue(final InternalNode node) {
        try {
            GeneratorResult result = getGeneratorResult(node);
            generatedPojoStore.putValue(node, result);
            LOG.trace("{} - {}", node, result);
            return result;
        } catch (InstancioTerminatingException ex) {
            throw ex;
        } catch (Exception ex) {
            final String msg = String.format("exception thrown by a custom Generator or Supplier" +
                            "%n%n" +
                            " -> Could not generate value for: %s (depth=%s)" +
                            "%n%n" +
                            "%s",

                    node.toDisplayString(), node.getDepth(), Format.nodePathToRoot(node, "    "));

            throw Fail.withUsageError(msg, ex);
        }
    }

    private boolean shouldReturnNullForNullable(final InternalNode node) {
        final boolean precondition = context.isNullable(node);
        return context.getRandom().diceRoll(precondition);
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    private GeneratorResult getGeneratorResult(final InternalNode node) {
        GeneratorResult result = GeneratorResult.emptyResult();

        if (node.isIgnored()) {
            result = GeneratorResult.ignoredResult();
        } else if (shouldReturnNullForNullable(node)) {
            result = GeneratorResult.nullResult();
        } else if (node.isCyclic()) {
            // Cyclic nodes can only be generated by a subset of handlers
            result = assignmentNodeHandler.getResult(node);

            if (result.isEmpty()) {
                result = userSuppliedGeneratorHandler.getResult(node);
            }
            if (result.isEmpty()) {
                // if Keys.SET_BACK_REFERENCES is enabled, attempt to
                // set value to parent object
                result = generatedPojoStore.getParentObject(node);
            }
        } else {
            for (NodeHandler handler : nodeHandlers) {
                result = handler.getResult(node);

                if (!result.isEmpty() || result.isDelayed()) {
                    break;
                }
            }
        }
        return result;
    }

    public Set<InternalAssignment> getUnresolvedAssignments() {
        return assignmentNodeHandler.getUnresolvedAssignments();
    }
}
