/*
 * Copyright 2022-2026 the original author or authors.
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
import org.instancio.internal.selectors.ElementOfDescriptor;
import org.instancio.internal.selectors.PredicateSelectorImpl;
import org.instancio.internal.util.CollectionUtils;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class AssignmentSelectorMap {

    private final GeneratorContext generatorContext;
    private final GeneratorInitialiser generatorInitialiser;
    private final SelectorMap<List<InternalAssignment>> destinationToAssignmentsMap;
    private final SelectorMap<List<TargetSelector>> originToDestinationSelectorsMap;
    private final BooleanSelectorMap originSelectors;

    AssignmentSelectorMap(final GeneratorContext generatorContext, final ElementOfState elementOfState) {
        this.generatorContext = generatorContext;
        this.generatorInitialiser = new GeneratorInitialiser(generatorContext);
        this.destinationToAssignmentsMap = SelectorMapImpl.create(elementOfState);
        this.originToDestinationSelectorsMap = SelectorMapImpl.create(elementOfState);
        this.originSelectors = new BooleanSelectorMap(elementOfState, ElementOfState.SelectorMapRole.STANDARD);
    }

    void putAll(final Map<TargetSelector, List<Assignment>> targetSelectors) {
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
                final Generator<?> generator = holder.getGenerator(generators);

                final Generator<?> updatedGenerator = generatorInitialiser.initGenerator(assignment.getDestination(), generator);

                final InternalAssignment updatedAssignment = assignment.toBuilder()
                        .generatorHolder(GeneratorHolder.of(updatedGenerator))
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
        final List<List<InternalAssignment>> values = destinationToAssignmentsMap.getValues(node);
        return CollectionUtils.flatMap(values);
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

    /**
     * Marks every elementOf assignment selector (origin and destination) whose container predicate
     * matches {@code containerNode} as "used", so a container that exists but is empty or has null
     * elements doesn't cause its selectors to be reported as unused.
     */
    void markSelectorsUsedForContainer(final InternalNode containerNode) {
        markMatchingSelectorsUsed(destinationToAssignmentsMap, containerNode);
        markMatchingSelectorsUsed(originToDestinationSelectorsMap, containerNode);
    }

    private static <V> void markMatchingSelectorsUsed(
            final SelectorMap<V> map, final InternalNode containerNode) {

        for (SelectorMap.SelectorEntry<V> entry : map) {
            final PredicateSelectorImpl ps = (PredicateSelectorImpl) entry.selector();
            final ElementOfDescriptor eod = ps.getElementOfDescriptor();
            if (eod != null && eod.matchesContainer(containerNode)) {
                map.markSelectorUsed(entry.selector());
            }
        }
    }

    /**
     * Returns the first elementOf assignment selector (origin or destination) whose container
     * predicate matches {@code containerNode}, or {@code null} if none. Used to fail fast when an
     * elementOf cross-element assignment targets a non-indexed collection (e.g. Set): such usage
     * can never work, so it is reported regardless of {@code lenient()}.
     */
    @Nullable
    TargetSelector findElementOfSelectorForContainer(final InternalNode containerNode) {
        final TargetSelector fromDestination =
                findMatchingElementOfSelector(destinationToAssignmentsMap, containerNode);

        return fromDestination != null
                ? fromDestination
                : findMatchingElementOfSelector(originToDestinationSelectorsMap, containerNode);
    }

    @Nullable
    private static <V> TargetSelector findMatchingElementOfSelector(
            final SelectorMap<V> map, final InternalNode containerNode) {

        for (SelectorMap.SelectorEntry<V> entry : map) {
            final PredicateSelectorImpl selector = (PredicateSelectorImpl) entry.selector();
            final ElementOfDescriptor eod = selector.getElementOfDescriptor();
            if (eod != null && eod.matchesContainer(containerNode)) {
                return selector;
            }
        }
        return null;
    }
}
