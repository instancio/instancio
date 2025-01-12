/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.internal.generator.misc;

import org.instancio.Random;
import org.instancio.documentation.InternalApi;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.Hints;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.util.ObjectUtils;
import org.instancio.settings.Keys;
import org.instancio.settings.PopulationStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@InternalApi
public final class ObjectPopulatingGenerator extends AbstractGenerator<Object> {

    private final Object object;
    private final AfterGenerate afterGenerate;

    public ObjectPopulatingGenerator(
            @NotNull final GeneratorContext generatorContext,
            @NotNull final Object object,
            @Nullable final PopulationStrategy populationStrategy) {

        super(generatorContext);
        this.object = object;
        this.afterGenerate = resolveAfterGenerate(populationStrategy);
    }

    @Override
    public String apiMethod() {
        return null;
    }

    @Override
    protected Object tryGenerateNonNull(final Random random) {
        return object;
    }

    @Override
    public Hints hints() {
        return Hints.afterGenerate(afterGenerate);
    }

    private AfterGenerate resolveAfterGenerate(
            @Nullable final PopulationStrategy userSuppliedPopulationStrategy) {

        final PopulationStrategy populationStrategy = ObjectUtils.defaultIfNull(
                userSuppliedPopulationStrategy, () -> getContext().getSettings().get(Keys.POPULATION_STRATEGY));

        if (populationStrategy == PopulationStrategy.POPULATE_NULLS) {
            return AfterGenerate.POPULATE_NULLS;
        }
        if (populationStrategy == PopulationStrategy.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES) {
            return AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES;
        }
        return AfterGenerate.APPLY_SELECTORS;
    }
}
