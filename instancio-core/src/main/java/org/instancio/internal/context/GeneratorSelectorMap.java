/*
 * Copyright 2022 the original author or authors.
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

import org.instancio.Generator;
import org.instancio.TargetSelector;
import org.instancio.exception.InstancioException;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generator.array.ArrayGenerator;
import org.instancio.generators.Generators;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.selectors.Flattener;
import org.instancio.internal.selectors.SelectorImpl;
import org.instancio.internal.selectors.SelectorTargetKind;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.instancio.util.ReflectionUtils.getField;

class GeneratorSelectorMap {

    private final Generators generators;
    private final Map<TargetSelector, Generator<?>> generatorSelectors;
    private final Map<TargetSelector, Function<Generators, ? extends GeneratorSpec<?>>> generatorSpecSelectors;
    private final SelectorMap<Generator<?>> selectorMap = new SelectorMap<>();
    private final Map<TargetSelector, Class<?>> generatorSubtypeMap = new LinkedHashMap<>();

    GeneratorSelectorMap(
            final Generators generators,
            final Map<TargetSelector, Generator<?>> generatorSelectors,
            final Map<TargetSelector, Function<Generators, ? extends GeneratorSpec<?>>> generatorSpecSelectors) {

        this.generators = generators;
        this.generatorSelectors = Collections.unmodifiableMap(generatorSelectors);
        this.generatorSpecSelectors = Collections.unmodifiableMap(generatorSpecSelectors);
        putAllGeneratorSpecs(generatorSpecSelectors);
        putAllGenerators(generatorSelectors);
    }

    public SelectorMap<Generator<?>> getSelectorMap() {
        return selectorMap;
    }

    Map<TargetSelector, Generator<?>> getGeneratorSelectors() {
        return generatorSelectors;
    }

    Map<TargetSelector, Function<Generators, ? extends GeneratorSpec<?>>> getGeneratorSpecSelectors() {
        return generatorSpecSelectors;
    }

    public Map<TargetSelector, Class<?>> getGeneratorSubtypeMap() {
        return Collections.unmodifiableMap(generatorSubtypeMap);
    }

    Optional<Generator<?>> getGenerator(final Node node) {
        return selectorMap.getValue(node);
    }

    private void putAllGenerators(final Map<TargetSelector, Generator<?>> generatorSelectors) {
        for (Map.Entry<TargetSelector, Generator<?>> entry : generatorSelectors.entrySet()) {
            final TargetSelector targetSelector = entry.getKey();
            final Generator<?> generator = entry.getValue();
            for (TargetSelector selector : ((Flattener) targetSelector).flatten()) {
                putGenerator(selector, generator);
            }
        }
    }

    private void putAllGeneratorSpecs(final Map<TargetSelector, Function<Generators, ? extends GeneratorSpec<?>>> specs) {
        for (Map.Entry<TargetSelector, Function<Generators, ? extends GeneratorSpec<?>>> entry : specs.entrySet()) {
            final TargetSelector targetSelector = entry.getKey();
            final Function<Generators, ?> genFn = entry.getValue();
            for (TargetSelector selector : ((Flattener) targetSelector).flatten()) {
                // Do not share generator instances between different selectors.
                // For example, array generators are created for each component type.
                // Therefore, using 'gen.array().length(10)' would fail when selectors are different for array types.
                final Generator<?> generator = (Generator<?>) genFn.apply(generators);
                putGenerator(selector, generator);
            }
        }
    }

    private void putGenerator(final TargetSelector targetSelector, final Generator<?> generator) {
        selectorMap.put(targetSelector, generator);

        final Optional<Class<?>> generatorTargetClass = generator.targetClass();
        generatorTargetClass.ifPresent(aClass -> generatorSubtypeMap.put(targetSelector, aClass));

        if (targetSelector instanceof SelectorImpl) {
            final SelectorImpl selector = (SelectorImpl) targetSelector;

            if (selector.getSelectorTargetKind() == SelectorTargetKind.FIELD) {
                final Field field = getField(selector.getTargetClass(), selector.getFieldName());
                final Class<?> userSpecifiedClass = generatorTargetClass.orElse(field.getType());

                if (generator instanceof ArrayGenerator) {
                    ((ArrayGenerator<?>) generator).subtype(userSpecifiedClass);
                }
            } else if (selector.getSelectorTargetKind() == SelectorTargetKind.CLASS) {
                final Class<?> userSpecifiedClass = generatorTargetClass.orElse(selector.getTargetClass());

                if (generator instanceof ArrayGenerator) {
                    ((ArrayGenerator<?>) generator).subtype(userSpecifiedClass);
                }
            } else {
                // should not be reachable
                throw new InstancioException("Unknown selector kind: " + selector.getSelectorTargetKind());
            }
        }
    }
}
