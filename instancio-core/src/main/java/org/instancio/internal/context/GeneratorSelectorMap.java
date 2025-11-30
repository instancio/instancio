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

import org.instancio.GeneratorSpecProvider;
import org.instancio.TargetSelector;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generators.Generators;
import org.instancio.internal.generators.BuiltInGenerators;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Sonar;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
class GeneratorSelectorMap {

    private final GeneratorInitialiser generatorInitialiser;
    private final SelectorMap<Generator<?>> selectorMap = new SelectorMapImpl<>();
    private final Generators generators;

    GeneratorSelectorMap(@NonNull final GeneratorContext generatorContext) {
        this.generators = new BuiltInGenerators(generatorContext);
        this.generatorInitialiser = new GeneratorInitialiser(generatorContext);
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
        final Generator<?> generator = generatorInitialiser.initGenerator(targetSelector, g);
        selectorMap.put(targetSelector, generator);
    }

    SelectorMap<Generator<?>> getSelectorMap() {
        return selectorMap;
    }

    Map<TargetSelector, Class<?>> getSubtypeMap() {
        return Collections.unmodifiableMap(generatorInitialiser.getSubtypeMap());
    }

    Optional<Generator<?>> getGenerator(final InternalNode node) {
        return selectorMap.getValue(node);
    }
}