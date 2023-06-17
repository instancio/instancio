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
package org.instancio.internal.context;

import org.instancio.Assignment;
import org.instancio.TargetSelector;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generators.Generators;
import org.instancio.internal.Flattener;
import org.instancio.internal.assignment.GeneratorHolder;
import org.instancio.internal.assignment.InternalAssignment;
import org.instancio.internal.generator.misc.GeneratorDecorator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Sonar;
import org.instancio.settings.Keys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class AssignmentSelectorMap {

    private final Map<TargetSelector, List<Assignment>> assignmentSelectors;
    private final SelectorMap<List<InternalAssignment>> selectorMap = new SelectorMap<>();
    private final BooleanSelectorMap originSelectors;
    private final TargetSelectorSelectorMap destinationSelectors;
    private final AfterGenerate defaultAfterGenerate;
    private final GeneratorContext context;

    public AssignmentSelectorMap(
            final Map<TargetSelector, List<Assignment>> targetSelectors,
            final GeneratorContext context) {

        this.assignmentSelectors = Collections.unmodifiableMap(targetSelectors);
        this.defaultAfterGenerate = context.getSettings().get(Keys.AFTER_GENERATE_HINT);
        this.context = context;

        // Maps an origin selector to a list of destination selectors.
        // When specifying an assignment, the destination selector may be a group, e.g.
        // assign(given(origin).is("foo").set(Select.all(field("f1"), field("f2")))
        final Map<TargetSelector, List<TargetSelector>> originDestinationsMap = new HashMap<>();

        for (Map.Entry<TargetSelector, List<Assignment>> entry : targetSelectors.entrySet()) {
            final TargetSelector targetSelector = entry.getKey();
            final List<InternalAssignment> assignments = processAssignments(entry.getValue());

            assignments.forEach(c -> {
                final List<TargetSelector> destinations = originDestinationsMap.computeIfAbsent(
                        c.getOrigin(), k -> new ArrayList<>());

                destinations.add(c.getDestination());
            });

            for (TargetSelector selector : ((Flattener<TargetSelector>) targetSelector).flatten()) {
                selectorMap.put(selector, assignments);
            }
        }

        originSelectors = new BooleanSelectorMap(originDestinationsMap.keySet());
        destinationSelectors = new TargetSelectorSelectorMap(originDestinationsMap);
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

    private List<InternalAssignment> processAssignments(final List<Assignment> assignments) {
        final List<InternalAssignment> processed = new ArrayList<>(assignments.size());
        final Generators generators = new Generators(context);

        for (Assignment c : assignments) {
            final InternalAssignment assignment = (InternalAssignment) c;

            if (assignment.getGeneratorHolder() != null) {
                final Generator<?> generator = getGenerator(assignment, generators);

                generator.init(context);

                final InternalAssignment updated = assignment.toBuilder()
                        .generator(GeneratorDecorator.decorateIfNullAfterGenerate(generator, defaultAfterGenerate))
                        .build();

                processed.add(updated);
            } else {
                processed.add(assignment);
            }
        }

        return processed;
    }

    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    private Generator<?> getGenerator(final InternalAssignment assignment, final Generators generators) {
        final GeneratorHolder holder = assignment.getGeneratorHolder();

        if (holder.getGenerator() == null) {
            return (Generator<?>) holder.getSpecProvider().getSpec(generators);
        }
        return holder.getGenerator();
    }
}
