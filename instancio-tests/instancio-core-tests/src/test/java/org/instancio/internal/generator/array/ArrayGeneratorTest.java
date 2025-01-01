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
package org.instancio.internal.generator.array;

import org.instancio.exception.InstancioApiException;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Hints;
import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.instancio.testsupport.asserts.HintsAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Percentage.withPercentage;

@NonDeterministicTag
@FeatureTag(Feature.SETTINGS)
class ArrayGeneratorTest extends AbstractGeneratorTestTemplate<String[], ArrayGenerator<String[]>> {

    private static final int MIN_SIZE = 101;
    private static final int MAX_SIZE = 102;
    private static final int SAMPLE_SIZE = 10_000;
    private static final int PERCENTAGE_THRESHOLD = 10;

    private static final Settings SETTINGS = Settings.defaults()
            .set(Keys.ARRAY_MIN_LENGTH, MIN_SIZE)
            .set(Keys.ARRAY_MAX_LENGTH, MAX_SIZE)
            .set(Keys.ARRAY_NULLABLE, true)
            .set(Keys.ARRAY_ELEMENTS_NULLABLE, true)
            .lock();

    private final ArrayGenerator<String[]> generator = new ArrayGenerator<>(
            getGeneratorContext(), String[].class);

    @Override
    protected String getApiMethod() {
        return "array()";
    }

    @Override
    public Settings getSettings() {
        return SETTINGS;
    }

    @Override
    protected ArrayGenerator<String[]> generator() {
        return generator;
    }

    @Test
    void generate() {
        final int[] counts = new int[2];

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final String[] result = generator.generate(random);
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

    @Test
    @Override
    protected void hints() {
        final Hints hints = generator.hints();

        HintsAssert.assertHints(hints).afterGenerate(AfterGenerate.POPULATE_ALL);
    }

    @NullSource
    @ValueSource(classes = String.class)
    @ParameterizedTest
    void subtypeValidation(final Class<?> klass) {
        assertThatThrownBy(() -> generator.subtype(klass))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("type must be an array: %s", klass);
    }
}
