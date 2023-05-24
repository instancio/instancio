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
package org.instancio.internal.generator.checksum;

import org.instancio.Gen;
import org.instancio.exception.InstancioApiException;
import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junitpioneer.jupiter.params.IntRangeSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Mod10GeneratorTest extends AbstractGeneratorTestTemplate<String, Mod10Generator> {
    private static final int DEFAULT_SIZE = 16;

    private final Mod10Generator generator = new Mod10Generator(getGeneratorContext());

    @Override
    protected String getApiMethod() {
        return "mod10()";
    }

    @Override
    protected Mod10Generator generator() {
        return generator;
    }

    @Test
    void defaultSize() {
        final String result = generator.generate(random);
        assertThat(result).hasSize(DEFAULT_SIZE);
    }

    @ParameterizedTest
    @IntRangeSource(from = 2, to = 100)
    void withCustomSize(final int length) {
        generator.length(length);
        assertThat(generator.generate(random)).hasSize(length);
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
                    .checkDigitIndex(checkIdx)
                    .length(size);

            final String result = generator.generate(random);
            assertThat(result).hasSize(size);
        }
    }

    @Test
    void validationLength() {
        assertThatThrownBy(() -> generator.length(1))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("number length must be greater than 1, but was: 1");
    }

    @Test
    void validationMultiplier() {
        assertThatThrownBy(() -> generator.multiplier(-1))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("multiplier must not be negative: -1");
    }

    @Test
    void validationWeight() {
        assertThatThrownBy(() -> generator.weight(-1))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("weight must not be negative: -1");
    }
}
