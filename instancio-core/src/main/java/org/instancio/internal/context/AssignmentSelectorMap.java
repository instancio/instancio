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
    private final SelectorMap<List<InternalAssignment>> selectorMap = new SelectorMapImpl<>();
    private final Map<TargetSelector, List<Assignment>> assignmentSelectors = new LinkedHashMap<>();
    private final BooleanSelectorMap originSelectors = new BooleanSelectorMap();
    private final TargetSelectorSelectorMap destinationSelectors = new TargetSelectorSelectorMap();
    private final Map<TargetSelector, Class<?>> generatorSubtypeMap = new LinkedHashMap<>();

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
        // source map: add as is
        this.assignmentSelectors.put(targetSelector, assignments);

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

        selectorMap.put(targetSelector, processedAssignments);
        originSelectors.putAll(originDestinationsMap.keySet());

        for (Map.Entry<TargetSelector, List<TargetSelector>> entry : originDestinationsMap.entrySet()) {
            final TargetSelector selector = entry.getKey();
            final List<TargetSelector> destinations = entry.getValue();

            List<List<TargetSelector>> currentDestinations = destinationSelectors.getSelectorMap().getValues(selector);

            if (currentDestinations.isEmpty()) {
                destinationSelectors.getSelectorMap().put(selector, destinations);
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
            generatorSubtypeMap.put(assignment.getDestination(), hint.targetClass());
        }

        return generator;
    }

    public List<InternalAssignment> getAssignments(final InternalNode node) {
        final Optional<List<InternalAssignment>> value = selectorMap.getValue(node);
        return value.orElse(Collections.emptyList());
    }

    BooleanSelectorMap getOriginSelectors() {
        return originSelectors;
    }

    SelectorMap<List<InternalAssignment>> getSelectorMap() {
        return selectorMap;
    }

    TargetSelectorSelectorMap getDestinationSelectors() {
        return destinationSelectors;
    }

    Map<TargetSelector, List<Assignment>> getAssignmentSelectors() {
        return assignmentSelectors;
    }

    List<TargetSelector> getDestinationSelectors(final InternalNode node) {
        return destinationSelectors.getTargetSelector(node);
    }

    Map<TargetSelector, Class<?>> getGeneratorSubtypeMap() {
        return generatorSubtypeMap;
    }
}
