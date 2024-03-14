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
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generators.Generators;
import org.instancio.internal.assignment.GeneratorHolder;
import org.instancio.internal.assignment.InternalAssignment;
import org.instancio.internal.generator.InternalGeneratorHint;
import org.instancio.internal.generator.misc.GeneratorDecorator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.settings.Keys;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

final class AssignmentSelectorMap {

    private final GeneratorContext context;
    private final AfterGenerate defaultAfterGenerate;
    private final SelectorMap<List<InternalAssignment>> destinationToAssignmentsMap = new SelectorMapImpl<>();
    private final SelectorMap<List<TargetSelector>> originToDestinationSelectorsMap = new SelectorMapImpl<>();
    private final BooleanSelectorMap originSelectors = new BooleanSelectorMap();
    private final Map<TargetSelector, Class<?>> subtypeMap = new LinkedHashMap<>();

    AssignmentSelectorMap(@NotNull final GeneratorContext context) {
        this.context = context;
        this.defaultAfterGenerate = context.getSettings().get(Keys.AFTER_GENERATE_HINT);
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
        final Generators generators = new Generators(context);

        for (Assignment c : assignments) {
            final InternalAssignment assignment = (InternalAssignment) c;

            if (assignment.getGeneratorHolder() != null) {
                final Generator<?> generator = getGenerator(assignment, generators);

                final InternalAssignment updated = assignment.toBuilder()
                        .generator(generator)
                        .build();

                processed.add(updated);
            } else {
                processed.add(assignment);
            }
        }

        return processed;
    }

    private <T> Generator<T> getGenerator(final InternalAssignment assignment, final Generators generators) {
        final GeneratorHolder holder = assignment.getGeneratorHolder();

        final Generator<T> g = holder.getGenerator() == null
                ? (Generator<T>) holder.getSpecProvider().getSpec(generators)
                : holder.getGenerator();

        g.init(context);

        final Generator<T> generator = GeneratorDecorator.decorateIfNullAfterGenerate(g, defaultAfterGenerate);
        final InternalGeneratorHint hint = generator.hints().get(InternalGeneratorHint.class);

        if (hint != null && hint.targetClass() != null) {
            subtypeMap.put(assignment.getDestination(), hint.targetClass());
        }

        return generator;
    }

    public List<InternalAssignment> getAssignments(final InternalNode node) {
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

    Map<TargetSelector, Class<?>> getSubtypeMap() {
        return Collections.unmodifiableMap(subtypeMap);
    }
}
