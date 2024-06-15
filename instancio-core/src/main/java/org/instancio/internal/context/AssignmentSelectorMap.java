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
package org.instancio.internal.context;

import org.instancio.Assignment;
import org.instancio.TargetSelector;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generators.Generators;
import org.instancio.internal.assignment.GeneratorHolder;
import org.instancio.internal.assignment.InternalAssignment;
import org.instancio.internal.generators.BuiltInGenerators;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

final class AssignmentSelectorMap {

    private final GeneratorContext generatorContext;
    private final GeneratorInitialiser generatorInitialiser;
    private final SelectorMap<List<InternalAssignment>> destinationToAssignmentsMap = new SelectorMapImpl<>();
    private final SelectorMap<List<TargetSelector>> originToDestinationSelectorsMap = new SelectorMapImpl<>();
    private final BooleanSelectorMap originSelectors = new BooleanSelectorMap();

    AssignmentSelectorMap(@NotNull final GeneratorContext generatorContext) {
        this.generatorContext = generatorContext;
        this.generatorInitialiser = new GeneratorInitialiser(generatorContext);
    }

    void putAll(final @NotNull Map<TargetSelector, List<Assignment>> targetSelectors) {
        for (Map.Entry<TargetSelector, List<Assignment>> entry : targetSelectors.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    void put(final TargetSelector targetSelector, final List<Assignment> assignments) {
        // Maps an origin selector to a list of destination selectors.
        // When specifying an assignment, the destination selector may be a group
        // e.g. assign(given(origin).is("foo").set(Select.all(field("f1"), field("f2")))
        final Map<TargetSelector, List<TargetSelector>> originDestinationsMap = new HashMap<>();

        final List<InternalAssignment> processedAssignments = processAssignments(assignments);

        for (InternalAssignment a : processedAssignments) {
            final List<TargetSelector> destinations = originDestinationsMap.computeIfAbsent(
                    a.getOrigin(), k -> new ArrayList<>());

            destinations.add(a.getDestination());
        }

        destinationToAssignmentsMap.put(targetSelector, processedAssignments);
        originSelectors.putAll(originDestinationsMap.keySet());

        for (Map.Entry<TargetSelector, List<TargetSelector>> entry : originDestinationsMap.entrySet()) {
            final TargetSelector selector = entry.getKey();
            final List<TargetSelector> destinations = entry.getValue();

            List<List<TargetSelector>> currentDestinations = originToDestinationSelectorsMap.getValues(selector);

            if (currentDestinations.isEmpty()) {
                originToDestinationSelectorsMap.put(selector, destinations);
            } else {
                currentDestinations.forEach(currentList -> currentList.addAll(destinations));
            }
        }
    }

    private List<InternalAssignment> processAssignments(final List<Assignment> assignments) {
        final List<InternalAssignment> processed = new ArrayList<>(assignments.size());
        final Generators generators = new BuiltInGenerators(generatorContext);

        for (Assignment c : assignments) {
            final InternalAssignment assignment = (InternalAssignment) c;

            if (assignment.getGeneratorHolder() != null) {
                final GeneratorHolder holder = assignment.getGeneratorHolder();
                final Generator<?> generator = holder.getGenerator() == null
                        ? (Generator<?>) holder.getSpecProvider().getSpec(generators)
                        : holder.getGenerator();

                final Generator<?> updatedGenerator = generatorInitialiser.initGenerator(assignment.getDestination(), generator);

                final InternalAssignment updatedAssignment = assignment.toBuilder()
                        .generator(updatedGenerator)
                        .build();

                processed.add(updatedAssignment);
            } else {
                processed.add(assignment);
            }
        }

        return processed;
    }

    Map<TargetSelector, Class<?>> getSubtypeMap() {
        return Collections.unmodifiableMap(generatorInitialiser.getSubtypeMap());
    }

    List<InternalAssignment> getAssignments(final InternalNode node) {
        final Optional<List<InternalAssignment>> value = destinationToAssignmentsMap.getValue(node);
        return value.orElse(Collections.emptyList());
    }

    /**
     * Contains origin selectors.
     *
     * <pre>{@code
     * // Given these assignments
     * .assign(given(Origin::foo).satisfies(predicateA).set(field(Destination::bar), "val1"))
     * .assign(given(Origin::foo).satisfies(predicateB).set(field(Destination::bar), "val2"))
     *
     * // This map will contain
     * { key=field(Origin::foo) -> value=true }
     * }</pre>
     */
    BooleanSelectorMap getOriginSelectors() {
        return originSelectors;
    }

    /**
     * Maps a destination selector to a list of assignments with this destination.
     *
     * <pre>{@code
     * // Given these assignments
     * .assign(given(Origin::foo).satisfies(predicateA).set(field(Destination::bar), "val1"))
     * .assign(given(Origin::foo).satisfies(predicateB).set(field(Destination::bar), "val2"))
     *
     * // This map will contain
     * { key=field(Destination::bar) -> value=[
     *     Assignment[origin=field(Origin::foo), destination=field(Destination::bar), predicate=predicateA, "val1"],
     *     Assignment[origin=field(Origin::foo), destination=field(Destination::bar), predicate=predicateB, "val2"]]
     * }
     * }</pre>
     */
    SelectorMap<List<InternalAssignment>> getDestinationToAssignmentsMap() {
        return destinationToAssignmentsMap;
    }

    /**
     * Maps an origin selector to a list of destination selectors.
     *
     * <pre>{@code
     * // Given these assignments
     * .assign(given(Origin::foo).satisfies(predicateA).set(field(Destination::bar), "val1"))
     * .assign(given(Origin::foo).satisfies(predicateB).set(field(Destination::bar), "val2"))
     *
     * // This map will contain
     * { key=field(Destination::bar) -> value=[field(Destination::bar), field(Destination::bar)] }
     * }</pre>
     */
    SelectorMap<List<TargetSelector>> getOriginToDestinationSelectorsMap() {
        return originToDestinationSelectorsMap;
    }

    List<TargetSelector> getDestinationSelectors(final InternalNode node) {
        List<List<TargetSelector>> values = originToDestinationSelectorsMap.getValues(node);
        return CollectionUtils.flatMap(values);
    }
}
