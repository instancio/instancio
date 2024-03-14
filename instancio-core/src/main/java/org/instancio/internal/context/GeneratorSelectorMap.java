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

import org.instancio.GeneratorSpecProvider;
import org.instancio.TargetSelector;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generators.Generators;
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
    private final SelectorMap<Generator<?>> selectorMap = new SelectorMapImpl<>();
    private final Map<TargetSelector, Class<?>> subtypeMap = new LinkedHashMap<>();
    private final Generators generators;

    GeneratorSelectorMap(@NotNull final GeneratorContext context) {
        this.context = context;
        this.generators = new Generators(context);
        this.defaultAfterGenerate = context.getSettings().get(Keys.AFTER_GENERATE_HINT);
    }

    void putGenerator(final TargetSelector targetSelector, final Generator<?> generator) {
        addToSelectorMap(targetSelector, generator);
    }

    void putGeneratorSpec(final TargetSelector targetSelector, final GeneratorSpecProvider<?> genFn) {
        addToSelectorMap(targetSelector, (Generator<?>) genFn.getSpec(generators));
    }

    void putAllGenerators(final Map<TargetSelector, Generator<?>> generatorMap) {
        for (Map.Entry<TargetSelector, Generator<?>> entry : generatorMap.entrySet()) {
            putGenerator(entry.getKey(), entry.getValue());
        }
    }

    void putAllGeneratorSpecs(final Map<TargetSelector, GeneratorSpecProvider<?>> specs) {
        for (Map.Entry<TargetSelector, GeneratorSpecProvider<?>> entry : specs.entrySet()) {
            putGeneratorSpec(entry.getKey(), entry.getValue());
        }
    }

    private void addToSelectorMap(final TargetSelector targetSelector, final Generator<?> g) {
        g.init(context);

        final Generator<?> generator = GeneratorDecorator.decorateIfNullAfterGenerate(g, defaultAfterGenerate);
        selectorMap.put(targetSelector, generator);

        final InternalGeneratorHint hint = generator.hints().get(InternalGeneratorHint.class);

        if (hint != null && hint.targetClass() != null) {
            subtypeMap.put(targetSelector, hint.targetClass());
        }
    }

    SelectorMap<Generator<?>> getSelectorMap() {
        return selectorMap;
    }

    Map<TargetSelector, Class<?>> getSubtypeMap() {
        return Collections.unmodifiableMap(subtypeMap);
    }

    Optional<Generator<?>> getGenerator(final InternalNode node) {
        return selectorMap.getValue(node);
    }
}