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
package org.instancio.internal.generator;

import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.Hints;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.testsupport.asserts.HintsAssert;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({Feature.GENERATOR, Feature.GENERATE})
public abstract class AbstractGeneratorTestTemplate<T, G extends AbstractGenerator<T>> {

    private static final Settings DEFAULT_SETTINGS = Settings.defaults().lock();
    protected final int SAMPLE_SIZE = 500;
    protected final Random random = new DefaultRandom();

    protected abstract String getApiMethod();

    protected abstract G generator();

    protected Settings getSettings() {
        return DEFAULT_SETTINGS;
    }

    protected GeneratorContext getGeneratorContext() {
        return new GeneratorContext(getSettings(), random);
    }

    @Test
    protected final void apiMethod() {
        assertThat(generator().apiMethod()).isEqualTo(getApiMethod());
    }

    @Test
    protected void hints() {
        final Hints hints = generator().hints();

        HintsAssert.assertHints(hints).afterGenerate(AfterGenerate.DO_NOT_MODIFY);
    }

    @Test
    protected void tryGenerateNonNull() {
        final Object result = generator().tryGenerateNonNull(random);

        assertThat(result).isNotNull();
    }

    @Test
    protected void generateNullable() {
        final AbstractGenerator<?> generator = generator();
        generator.nullable();

        final boolean hasNull = Stream.generate(() -> generator.generate(random))
                .limit(SAMPLE_SIZE)
                .anyMatch(Objects::isNull);

        assertThat(hasNull)
                .as("Expected %s to generate a null", generator.getClass().getSimpleName())
                .isTrue();
    }
}
