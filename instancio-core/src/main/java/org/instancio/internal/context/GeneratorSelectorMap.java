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

import org.instancio.GeneratorSpecProvider;
import org.instancio.TargetSelector;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generators.Generators;
import org.instancio.internal.Flattener;
import org.instancio.internal.generator.InternalGeneratorHint;
import org.instancio.internal.generator.misc.GeneratorDecorator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Sonar;
import org.instancio.settings.Keys;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
class GeneratorSelectorMap {

    private final GeneratorContext context;
    private final AfterGenerate defaultAfterGenerate;
    private final Map<TargetSelector, Generator<?>> generatorSelectors;
    private final Map<TargetSelector, GeneratorSpecProvider<?>> generatorSpecSelectors;
    private final SelectorMap<Generator<?>> selectorMap;
    private final Map<TargetSelector, Class<?>> generatorSubtypeMap = new LinkedHashMap<>();

    GeneratorSelectorMap(
            @NotNull final GeneratorContext context,
            @NotNull final Map<TargetSelector, Generator<?>> generatorSelectors,
            @NotNull final Map<TargetSelector, GeneratorSpecProvider<?>> generatorSpecSelectors) {

        this.context = context;
        this.defaultAfterGenerate = context.getSettings().get(Keys.AFTER_GENERATE_HINT);
        this.generatorSelectors = Collections.unmodifiableMap(generatorSelectors);
        this.generatorSpecSelectors = Collections.unmodifiableMap(generatorSpecSelectors);

        this.selectorMap = generatorSelectors.isEmpty() && generatorSpecSelectors.isEmpty()
                ? SelectorMapImpl.emptyMap() : new SelectorMapImpl<>();

        putAllGeneratorSpecs(generatorSpecSelectors);
        putAllGenerators(generatorSelectors);
    }

    private void putAllGeneratorSpecs(final Map<TargetSelector, GeneratorSpecProvider<?>> specs) {
        final Generators generators = new Generators(context);

        for (Map.Entry<TargetSelector, GeneratorSpecProvider<?>> entry : specs.entrySet()) {
            final TargetSelector targetSelector = entry.getKey();
            final GeneratorSpecProvider<?> genFn = entry.getValue();
            for (TargetSelector selector : ((Flattener<TargetSelector>) targetSelector).flatten()) {
                // Do not share generator instances between different selectors.
                // For example, array generators are created for each component type.
                // Therefore, using 'gen.array().length(10)' would fail when selectors are different for array types.
                final Generator<?> generator = (Generator<?>) genFn.getSpec(generators);
                putGenerator(selector, generator);
            }
        }
    }

    private void putAllGenerators(final Map<TargetSelector, Generator<?>> generatorMap) {
        for (Map.Entry<TargetSelector, Generator<?>> entry : generatorMap.entrySet()) {
            final TargetSelector targetSelector = entry.getKey();
            final Generator<?> generator = entry.getValue();
            for (TargetSelector selector : ((Flattener<TargetSelector>) targetSelector).flatten()) {
                putGenerator(selector, generator);
            }
        }
    }

    private void putGenerator(final TargetSelector targetSelector, final Generator<?> g) {
        g.init(context);

        final Generator<?> generator = GeneratorDecorator.decorateIfNullAfterGenerate(g, defaultAfterGenerate);
        selectorMap.put(targetSelector, generator);

        final InternalGeneratorHint internalHint = generator.hints().get(InternalGeneratorHint.class);

        Optional.ofNullable(internalHint)
                .map(InternalGeneratorHint::targetClass)
                .ifPresent(klass -> generatorSubtypeMap.put(targetSelector, klass));
    }

    SelectorMap<Generator<?>> getSelectorMap() {
        return selectorMap;
    }

    Map<TargetSelector, Generator<?>> getGeneratorSelectors() {
        return generatorSelectors;
    }

    Map<TargetSelector, GeneratorSpecProvider<?>> getGeneratorSpecSelectors() {
        return generatorSpecSelectors;
    }

    Map<TargetSelector, Class<?>> getGeneratorSubtypeMap() {
        return Collections.unmodifiableMap(generatorSubtypeMap);
    }

    Optional<Generator<?>> getGenerator(final InternalNode node) {
        return selectorMap.getValue(node);
    }
}