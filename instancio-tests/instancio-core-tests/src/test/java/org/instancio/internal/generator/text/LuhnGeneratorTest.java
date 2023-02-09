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
package org.instancio.internal.generator.text;

import org.instancio.Gen;
import org.instancio.Random;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.random.DefaultRandom;
import org.instancio.internal.util.LuhnUtils;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LuhnGeneratorTest {
    private static final int DEFAULT_SIZE = 16;

    private final Random random = new DefaultRandom();
    private final LuhnGenerator generator = new LuhnGenerator(
            new GeneratorContext(Settings.create(), random));

    @Test
    void apiMethod() {
        assertThat(generator.apiMethod()).isNull();
    }

    @Test
    void defaultSize() {
        final String result = generator.generate(random);
        assertLuhn(result, DEFAULT_SIZE);
    }

    @Test
    void withCustomSize() {
        for (int i = 2; i < 100; i++) {
            generator.length(i);
            assertLuhn(generator.generate(random), i);
        }
    }

    @Test
    void withExplicitIndices() {
        final int sampleSize = 100_000;
        for (int i = 0; i < sampleSize; i++) {
            final int startIdx = Gen.ints().range(0, 10).get();
            final int endIdx = startIdx + Gen.ints().range(2, 10).get();

            final int checkIdx = startIdx == 0 || Gen.booleans().get()
                    ? endIdx
                    : Gen.ints().range(0, startIdx - 1).get();

            final int size = endIdx + Gen.ints().range(1, 10).get();

            generator
                    .startIndex(startIdx)
                    .endIndex(endIdx)
                    .checkIndex(checkIdx)
                    .length(size);

            final String result = generator.generate(random);
            assertLuhn(result, size, startIdx, endIdx, checkIdx);
        }
    }

    @Test
    void nullable() {
        generator.nullable();
        final Stream<String> result = Stream.generate(() -> generator.generate(random))
                .filter(Objects::isNull)
                .limit(1);

        assertThat(result).containsNull();
    }

    @Test
    void validationLength() {
        assertThatThrownBy(() -> generator.length(1))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Luhn-valid number length must be greater than 1, but was: 1");
    }

    @Test
    void validationLengthRange() {
        assertThatThrownBy(() -> generator.length(0, 1))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Luhn-valid number length must be greater than 1, but was: length(0, 1)");

        assertThatThrownBy(() -> generator.length(5, 4))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Min must be less than or equal to max");
    }

    private static void assertLuhn(final String result, final int expectedSize) {
        assertThat(result).hasSize(expectedSize);
        assertThat(LuhnUtils.isLuhnValid(result))
                .as("Expected valid Luhn number: '%s'", result)
                .isTrue();
    }

    private static void assertLuhn(
            final String result, final int expectedSize,
            final int startIdx, final int endIdx, int checkIdx) {

        assertThat(result).hasSize(expectedSize);
        assertThat(LuhnUtils.isLuhnValid(startIdx, endIdx, checkIdx, result))
                .as("Expected valid Luhn number: '%s'; startIdx=%s, endIdx=%s, checkIdx=%s",
                        result, startIdx, endIdx, checkIdx)
                .isTrue();
    }
}
