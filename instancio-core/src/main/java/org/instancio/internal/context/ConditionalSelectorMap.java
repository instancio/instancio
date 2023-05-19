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

import org.instancio.Conditional;
import org.instancio.TargetSelector;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generators.Generators;
import org.instancio.internal.conditional.GeneratorHolder;
import org.instancio.internal.conditional.InternalConditional;
import org.instancio.internal.generator.misc.GeneratorDecorator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.selectors.Flattener;
import org.instancio.internal.util.Sonar;
import org.instancio.settings.Keys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A selector map for conditionals.
 */
public final class ConditionalSelectorMap {

    private final Map<TargetSelector, List<Conditional>> conditionalSelectors;
    private final SelectorMap<List<InternalConditional>> selectorMap = new SelectorMap<>();
    private final TargetSelectorSelectorMap destinationSelectors;
    private final AfterGenerate defaultAfterGenerate;
    private final GeneratorContext context;

    public ConditionalSelectorMap(
            final Map<TargetSelector, List<Conditional>> targetSelectors,
            final GeneratorContext context) {

        this.conditionalSelectors = Collections.unmodifiableMap(targetSelectors);
        this.defaultAfterGenerate = context.getSettings().get(Keys.AFTER_GENERATE_HINT);
        this.context = context;

        // Maps an origin selector to a list of destination selectors.
        // When specifying a conditional, the destination selector may be a group, e.g.
        // when(valueOf(origin).is("foo").set(all(field("f1"), field("f2")))
        final Map<TargetSelector, List<TargetSelector>> originToDestinationsMap = new HashMap<>();

        for (Map.Entry<TargetSelector, List<Conditional>> entry : targetSelectors.entrySet()) {
            final TargetSelector targetSelector = entry.getKey();
            final List<InternalConditional> conditionals = processConditionals(entry.getValue());

            conditionals.forEach(c -> {
                final List<TargetSelector> destinations = originToDestinationsMap.computeIfAbsent(
                        c.getOrigin(), k -> new ArrayList<>());

                destinations.add(c.getDestination());
            });

            for (TargetSelector selector : ((Flattener) targetSelector).flatten()) {
                selectorMap.put(selector, conditionals);
            }
        }

        destinationSelectors = new TargetSelectorSelectorMap(originToDestinationsMap);
    }

    Map<TargetSelector, List<Conditional>> getConditionalSelectors() {
        return conditionalSelectors;
    }

    List<TargetSelector> getDestinationSelectors(final InternalNode node) {
        return destinationSelectors.getTargetSelector(node);
    }

    public List<InternalConditional> getConditionals(final InternalNode node) {
        final Optional<List<InternalConditional>> value = selectorMap.getValue(node);
        return value.orElse(Collections.emptyList());
    }

    private List<InternalConditional> processConditionals(final List<Conditional> conditionals) {
        final List<InternalConditional> processedConditionals = new ArrayList<>(conditionals.size());
        final Generators generators = new Generators(context);

        for (Conditional c : conditionals) {
            final InternalConditional conditional = (InternalConditional) c;
            final Generator<?> generator = getGenerator(conditional, generators);

            generator.init(context);

            processedConditionals.add(conditional.toBuilder()
                    .generator(GeneratorDecorator.decorateIfNullAfterGenerate(generator, defaultAfterGenerate))
                    .build());
        }

        return processedConditionals;
    }

    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    private Generator<?> getGenerator(final InternalConditional conditional, final Generators generators) {
        final GeneratorHolder holder = conditional.getGeneratorHolder();

        if (holder.getGenerator() == null) {
            return (Generator<?>) holder.getSpecProvider().getSpec(generators);
        }
        return holder.getGenerator();
    }
}
