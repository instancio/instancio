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
package org.instancio.internal.generator.checksum;

import org.instancio.Instancio;
import org.instancio.exception.InstancioApiException;
import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junitpioneer.jupiter.params.IntRangeSource;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

abstract class AbstractVariableLengthModCheckGeneratorTest<G extends VariableLengthModCheckGenerator>
        extends AbstractGeneratorTestTemplate<String, G> {

    private static final int DEFAULT_SIZE = 16;

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    final void defaultSize() {
        final String result = generator().generate(random);
        assertThat(result)
                .hasSize(DEFAULT_SIZE)
                .doesNotStartWith("0");
    }

    @ParameterizedTest
    @IntRangeSource(from = 2, to = 100)
    final void withCustomSize(final int length) {
        final G generator = generator();
        generator.length(length);
        assertThat(generator.generate(random)).hasSize(length);
    }

    @Test
    final void withExplicitIndices() {
        final G generator = generator();
        final int sampleSize = 100_000;

        for (int i = 0; i < sampleSize; i++) {
            final int startIdx = Instancio.gen().ints().range(0, 10).get();
            final int endIdx = startIdx + Instancio.gen().ints().range(2, 10).get();

            final int checkIdx = startIdx == 0 || Instancio.gen().booleans().get()
                    ? endIdx
                    : Instancio.gen().ints().range(0, startIdx - 1).get();

            final int size = endIdx + Instancio.gen().ints().range(1, 10).get();

            generator
                    .startIndex(startIdx)
                    .endIndex(endIdx)
                    .checkDigitIndex(checkIdx)
                    .length(size);

            final String result = generator.generate(random);
            assertThat(result).hasSize(size);
        }
    }

    /**
     * Generating a value must not modify the generator's configuration,
     * otherwise values produced by a reused instance would be constrained
     * by whatever the first invocation happened to generate.
     */
    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    final void reusedGeneratorProducesIndependentValues() {
        final int minLength = 10;
        final int maxLength = 20;
        final int sampleSize = 500;

        final G reused = generator();
        reused.length(minLength, maxLength);

        final Set<Integer> lengths = new HashSet<>();
        for (int i = 0; i < sampleSize; i++) {
            lengths.add(reused.generate(random).length());
        }

        assertThat(lengths)
                .as("a reused generator should produce the full range of lengths")
                .containsExactlyInAnyOrderElementsOf(
                        IntStream.rangeClosed(minLength, maxLength).boxed().collect(Collectors.toList()));
    }

    @Test
    final void validationLength() {
        final G generator = generator();

        assertThatThrownBy(() -> generator.length(1))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("number length must be greater than 1, but was: 1");

        assertThatThrownBy(() -> generator.length(3, 2))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("min must be less than or equal to max");
    }

    /**
     * The payload the check digit is calculated from must contain at least
     * one digit, which is not the case if {@code startIndex} lies at or beyond
     * the end of the generated number.
     */
    @Test
    final void validationStartIndexLeavesNoPayload() {
        final G generator = generator();
        generator.startIndex(5).length(3);

        assertThatThrownBy(() -> generator.generate(random))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContainingAll(
                        "startIndex and endIndex must satisfy condition:",
                        "-> startIndex .......: 5",
                        "-> endIndex .........: 2",
                        "-> checkDigitIndex ..: 2");
    }

    @Test
    final void validationStartIndexEqualToEndIndex() {
        final G generator = generator();
        generator.startIndex(3).endIndex(3).checkDigitIndex(3).length(10);

        assertThatThrownBy(() -> generator.generate(random))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContainingAll(
                        "startIndex and endIndex must satisfy condition:",
                        "-> startIndex .......: 3",
                        "-> endIndex .........: 3",
                        "-> checkDigitIndex ..: 3");
    }
}
