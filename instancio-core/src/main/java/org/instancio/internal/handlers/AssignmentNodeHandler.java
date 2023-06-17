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
package org.instancio.internal.handlers;

import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.internal.GeneratedObjectStore;
import org.instancio.internal.assignment.InternalAssignment;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResolver;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.generator.misc.EmitGenerator;
import org.instancio.internal.instantiation.Instantiator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Fail;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class AssignmentNodeHandler implements NodeHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AssignmentNodeHandler.class);
    private static final Hints DO_NOT_MODIFY = Hints.afterGenerate(AfterGenerate.DO_NOT_MODIFY);

    private final ModelContext<?> context;
    private final GeneratedObjectStore generatedObjectStore;
    private final UserSuppliedGeneratorProcessor userSuppliedGeneratorProcessor;
    private final Set<InternalAssignment> unresolvedAssignments = new LinkedHashSet<>();

    public AssignmentNodeHandler(
            final ModelContext<?> context,
            final GeneratedObjectStore generatedObjectStore,
            final GeneratorResolver generatorResolver,
            final Instantiator instantiator) {

        this.context = context;
        this.generatedObjectStore = generatedObjectStore;
        this.userSuppliedGeneratorProcessor = new UserSuppliedGeneratorProcessor(
                generatorResolver, instantiator);
    }

    @NotNull
    @Override
    public GeneratorResult getResult(@NotNull final InternalNode node) {
        final List<InternalAssignment> assignments = context.getAssignments(node);

        // loop from the end so that the last matching assignment wins
        for (int i = assignments.size() - 1; i >= 0; i--) {
            final InternalAssignment assignment = assignments.get(i);
            final GeneratorResult candidateValue = generatedObjectStore.getValue(assignment.getDestination());

            if (candidateValue == null) {
                LOG.trace("Delayed result for {}", assignment.getDestination());
                unresolvedAssignments.add(assignment);
                return GeneratorResult.delayed();
            }

            unresolvedAssignments.remove(assignment);

            LOG.trace("Value for destination {}: {}", assignment.getDestination(), candidateValue.getValue());

            final Predicate<Object> predicate = assignment.getOriginPredicate();

            if (predicate == null || isSatisfied(candidateValue.getValue(), predicate)) {

                // null generator means we're copying the result of one node to another
                // i.e. assign valueOf(selectorA).to(selectorB)
                if (assignment.getGenerator() == null) {
                    Object destinationResult = candidateValue.getValue();

                    if (assignment.getValueMapper() != null) {
                        destinationResult = assignment.getValueMapper().apply(destinationResult);
                    }

                    return GeneratorResult.create(destinationResult, DO_NOT_MODIFY);
                } else {
                    final Generator<?> assignmentGenerator = assignment.getGenerator();

                    // TODO refactor to remove this check
                    if (assignmentGenerator instanceof EmitGenerator) {
                        final EmitGeneratorHelper helper = new EmitGeneratorHelper(context);
                        return helper.getResult((EmitGenerator<?>) assignmentGenerator, node);
                    }

                    final Generator<?> generator = userSuppliedGeneratorProcessor.processGenerator(
                            assignmentGenerator, node);

                    final Object destinationResult = generator.generate(context.getRandom());
                    return GeneratorResult.create(destinationResult, generator.hints());
                }
            }
        }

        // if no assignments, or origin value didn't satisfy the predicate
        // return empty result means process the node as usual using a built-in generator
        return GeneratorResult.emptyResult();
    }

    public Set<InternalAssignment> getUnresolvedAssignments() {
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
            throw Fail.withUsageError("error invoking the predicate",
                    object.getClass().getTypeName(), ex);
        }
    }
}
