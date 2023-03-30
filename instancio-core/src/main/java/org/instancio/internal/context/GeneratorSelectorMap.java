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
import org.instancio.internal.generator.InternalGeneratorHint;
import org.instancio.internal.generator.array.ArrayGenerator;
import org.instancio.internal.generator.misc.GeneratorDecorator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.selectors.Flattener;
import org.instancio.internal.selectors.SelectorImpl;
import org.instancio.internal.util.Sonar;
import org.instancio.settings.Keys;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.instancio.internal.util.ReflectionUtils.getField;

@SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
class GeneratorSelectorMap {

    private final Map<TargetSelector, Generator<?>> generatorSelectors;
    private final Map<TargetSelector, GeneratorSpecProvider<?>> generatorSpecSelectors;
    private final SelectorMap<Generator<?>> selectorMap = new SelectorMap<>();
    private final Map<TargetSelector, Class<?>> generatorSubtypeMap = new LinkedHashMap<>();
    private final AfterGenerate defaultAfterGenerate;
    private final GeneratorContext context;

    GeneratorSelectorMap(
            final GeneratorContext context,
            final Map<TargetSelector, Generator<?>> generatorSelectors,
            final Map<TargetSelector, GeneratorSpecProvider<?>> generatorSpecSelectors) {

        this.context = context;
        this.generatorSelectors = Collections.unmodifiableMap(generatorSelectors);
        this.generatorSpecSelectors = Collections.unmodifiableMap(generatorSpecSelectors);
        this.defaultAfterGenerate = context.getSettings().get(Keys.AFTER_GENERATE_HINT);
        putAllGeneratorSpecs(generatorSpecSelectors);
        putAllGenerators(generatorSelectors);
    }

    public SelectorMap<Generator<?>> getSelectorMap() {
        return selectorMap;
    }

    Map<TargetSelector, Generator<?>> getGeneratorSelectors() {
        return generatorSelectors;
    }

    Map<TargetSelector, GeneratorSpecProvider<?>> getGeneratorSpecSelectors() {
        return generatorSpecSelectors;
    }

    public Map<TargetSelector, Class<?>> getGeneratorSubtypeMap() {
        return Collections.unmodifiableMap(generatorSubtypeMap);
    }

    Optional<Generator<?>> getGenerator(final InternalNode node) {
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

    private void putAllGeneratorSpecs(final Map<TargetSelector, GeneratorSpecProvider<?>> specs) {
        final Generators generators = new Generators(context);

        for (Map.Entry<TargetSelector, GeneratorSpecProvider<?>> entry : specs.entrySet()) {
            final TargetSelector targetSelector = entry.getKey();
            final GeneratorSpecProvider<?> genFn = entry.getValue();
            for (TargetSelector selector : ((Flattener) targetSelector).flatten()) {
                // Do not share generator instances between different selectors.
                // For example, array generators are created for each component type.
                // Therefore, using 'gen.array().length(10)' would fail when selectors are different for array types.
                final Generator<?> generator = (Generator<?>) genFn.getSpec(generators);
                putGenerator(selector, generator);
            }
        }
    }

    private void putGenerator(final TargetSelector targetSelector, final Generator<?> g) {
        g.init(context);

        final Generator<?> generator = GeneratorDecorator.decorate(g, defaultAfterGenerate);
        selectorMap.put(targetSelector, generator);

        final InternalGeneratorHint internalHint = generator.hints().get(InternalGeneratorHint.class);
        final Optional<Class<?>> generatorTargetClass = Optional.ofNullable(internalHint)
                .map(InternalGeneratorHint::targetClass);

        generatorTargetClass.ifPresent(aClass -> generatorSubtypeMap.put(targetSelector, aClass));

        if (targetSelector instanceof SelectorImpl) {
            final SelectorImpl selector = (SelectorImpl) targetSelector;

            if (selector.isFieldSelector()) {
                final Field field = getField(selector.getTargetClass(), selector.getFieldName());
                final Class<?> userSpecifiedClass = generatorTargetClass.orElse(field.getType());

                if (generator instanceof ArrayGenerator) {
                    ((ArrayGenerator<?>) generator).subtype(userSpecifiedClass);
                }
            } else {
                final Class<?> userSpecifiedClass = generatorTargetClass.orElse(selector.getTargetClass());

                if (generator instanceof ArrayGenerator) {
                    ((ArrayGenerator<?>) generator).subtype(userSpecifiedClass);
                }
            }
        }
    }
}
