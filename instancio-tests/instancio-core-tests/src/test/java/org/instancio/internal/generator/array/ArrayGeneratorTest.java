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
package org.instancio.internal.generator.array;

import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.random.DefaultRandom;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.instancio.testsupport.asserts.HintsAssert;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;

@NonDeterministicTag
@FeatureTag(Feature.SETTINGS)
class ArrayGeneratorTest {

    private static final int MIN_SIZE = 101;
    private static final int MAX_SIZE = 102;
    private static final int SAMPLE_SIZE = 10_000;
    private static final int PERCENTAGE_THRESHOLD = 10;

    private static final Settings settings = Settings.defaults()
            .set(Keys.ARRAY_MIN_LENGTH, MIN_SIZE)
            .set(Keys.ARRAY_MAX_LENGTH, MAX_SIZE)
            .set(Keys.ARRAY_NULLABLE, true)
            .set(Keys.ARRAY_ELEMENTS_NULLABLE, true)
            .lock();

    private static final Random random = new DefaultRandom();
    private static final GeneratorContext context = new GeneratorContext(settings, random);

    private final ArrayGenerator<?> generator = new ArrayGenerator<>(context, String[].class);

    @Test
    void apiMethod() {
        assertThat(generator.apiMethod()).isEqualTo("array()");
    }

    @Test
    void generate() {
        final int[] counts = new int[2];

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final String[] result = (String[]) generator.generate(random);
            counts[result == null ? 0 : 1]++;

            if (result != null) {
                assertThat(result).hasSizeBetween(MIN_SIZE, MAX_SIZE);
            }
        }

        assertThat(counts[1])
                .as("Expecting 5/6 of results to be non-null")
                .isCloseTo((5 * SAMPLE_SIZE) / 6, withPercentage(PERCENTAGE_THRESHOLD));

        HintsAssert.assertHints(generator.hints())
                .afterGenerate(AfterGenerate.POPULATE_ALL)
                .nullableArrayElements(true);
    }
}
