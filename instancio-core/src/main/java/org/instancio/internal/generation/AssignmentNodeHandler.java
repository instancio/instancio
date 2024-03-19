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

import org.instancio.generator.Generator;
import org.instancio.internal.assignment.InternalAssignment;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Constants;
import org.instancio.internal.util.Fail;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

class AssignmentNodeHandler implements NodeHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AssignmentNodeHandler.class);

    private final ModelContext<?> context;
    private final GeneratedObjectStore generatedObjectStore;
    private final UserSuppliedGeneratorProcessor userSuppliedGeneratorProcessor;
    private final Set<InternalAssignment> unresolvedAssignments = new LinkedHashSet<>();

    private AssignmentNodeHandler(
            final ModelContext<?> context,
            final GeneratedObjectStore generatedObjectStore,
            final UserSuppliedGeneratorProcessor userSuppliedGeneratorProcessor) {

        this.context = context;
        this.generatedObjectStore = generatedObjectStore;
        this.userSuppliedGeneratorProcessor = userSuppliedGeneratorProcessor;
    }

    static AssignmentNodeHandler create(
            final ModelContext<?> context,
            final GeneratedObjectStore generatedObjectStore,
            final UserSuppliedGeneratorProcessor userSuppliedGeneratorProcessor) {

        return context.getSelectorMaps().hasAssignments()
                ? new AssignmentNodeHandler(context, generatedObjectStore, userSuppliedGeneratorProcessor)
                : new NoopAssignmentNodeHandler();
    }

    @NotNull
    @Override
    public GeneratorResult getResult(@NotNull final InternalNode node) {
        final List<InternalAssignment> assignments = context.getAssignments(node);

        // loop from the end so that the last matching assignment wins
        for (int i = assignments.size() - 1; i >= 0; i--) {
            final InternalAssignment assignment = assignments.get(i);
            final Predicate<Object> predicate = assignment.getOriginPredicate();

            // unconditional assignment
            if (predicate == null && assignment.getGenerator() != null) {
                final Generator<?> generator = assignment.getGenerator();
                return userSuppliedGeneratorProcessor.getGeneratorResult(node, generator);
            }

            final GeneratorResult candidateResult = generatedObjectStore.getValue(assignment.getDestination());

            if (candidateResult == null) {
                LOG.trace("Delayed result for {}", assignment.getDestination());
                unresolvedAssignments.add(assignment);
                return GeneratorResult.delayed();
            }

            unresolvedAssignments.remove(assignment);

            LOG.trace("Value for destination {}: {}", assignment.getDestination(), candidateResult.getValue());

            if (predicate == null || isSatisfied(candidateResult.getValue(), predicate)) {

                // null generator means we're copying the result of one node to another
                // i.e. assign valueOf(selectorA).to(selectorB)
                if (assignment.getGenerator() == null) {
                    Object destinationResult = candidateResult.getValue();

                    if (assignment.getValueMapper() != null) {
                        destinationResult = assignment.getValueMapper().apply(destinationResult);
                    }

                    // Since the same object instance is assigned to different fields,
                    // set this result to DO_NOT_MODIFY. The result will be populated
                    // based on the original hint
                    return GeneratorResult.create(destinationResult, Constants.DO_NOT_MODIFY_HINT);
                } else {
                    final Generator<?> generator = assignment.getGenerator();
                    return userSuppliedGeneratorProcessor.getGeneratorResult(node, generator);
                }
            }
        }

        // if no assignments, or origin value didn't satisfy the predicate
        // return empty result means process the node as usual using a built-in generator
        return GeneratorResult.emptyResult();
    }

    Set<InternalAssignment> getUnresolvedAssignments() {
        return unresolvedAssignments;
    }

    private static boolean isSatisfied(final Object object, final Predicate<Object> predicate) {
        try {
            return predicate.test(object);
        } catch (ClassCastException ex) {
            throw Fail.withUsageError(
                    "error invoking the predicate against generated object of type %s",
                    object.getClass().getTypeName(), ex);
        } catch (Exception ex) {
            throw Fail.withUsageError("error invoking the predicate", ex);
        }
    }

    @VisibleForTesting
    static final class NoopAssignmentNodeHandler extends AssignmentNodeHandler {

        private NoopAssignmentNodeHandler() {
            super(null, null, null);
        }

        @NotNull
        @Override
        public GeneratorResult getResult(final @NotNull InternalNode node) {
            return GeneratorResult.emptyResult();
        }

        @Override
        Set<InternalAssignment> getUnresolvedAssignments() {
            return Collections.emptySet();
        }
    }
}
