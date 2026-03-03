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

import org.instancio.TargetSelector;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.Hints;
import org.instancio.internal.generator.InternalGeneratorHint;
import org.instancio.internal.generator.misc.GeneratorDecorator;
import org.instancio.settings.Keys;

import java.util.LinkedHashMap;
import java.util.Map;

final class GeneratorInitialiser {

    private final Map<TargetSelector, Class<?>> subtypeMap = new LinkedHashMap<>();
    private final GeneratorContext context;
    private final AfterGenerate defaultAfterGenerate;

    GeneratorInitialiser(final GeneratorContext generatorContext) {
        this.context = generatorContext;
        this.defaultAfterGenerate = generatorContext.getSettings().get(Keys.AFTER_GENERATE_HINT);
    }

    <T> Generator<T> initGenerator(final TargetSelector targetSelector, final Generator<T> g) {
        g.init(context);

        final Generator<T> generator = GeneratorDecorator.decorateIfNullAfterGenerate(g, defaultAfterGenerate);
        final Hints hints = generator.hints();
        if (hints != null) {
            final InternalGeneratorHint hint = hints.get(InternalGeneratorHint.class);

            if (hint != null && hint.targetClass() != null) {
                subtypeMap.put(targetSelector, hint.targetClass());
            }
        }

        return generator;
    }

    Map<TargetSelector, Class<?>> getSubtypeMap() {
        return subtypeMap;
    }
}
