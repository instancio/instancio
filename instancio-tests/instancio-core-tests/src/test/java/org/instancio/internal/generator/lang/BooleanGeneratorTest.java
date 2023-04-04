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
package org.instancio.internal.generator.lang;

import org.instancio.Instancio;
import org.instancio.exception.InstancioApiException;
import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.withinPercentage;
import static org.instancio.Select.allBooleans;

@NonDeterministicTag
class BooleanGeneratorTest extends AbstractGeneratorTestTemplate<Boolean, BooleanGenerator> {

    private final BooleanGenerator generator = new BooleanGenerator(getGeneratorContext());

    @Override
    protected String getApiMethod() {
        return "booleans()";
    }

    @Override
    protected BooleanGenerator generator() {
        return generator;
    }

    @Test
    void generate() {
        final Set<Object> results = new HashSet<>();
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            results.add(generator.generate(random));
        }

        assertThat(results)
                .as("Should contain true and false")
                .hasSize(2);
    }

    @Test
    @NonDeterministicTag
    void withProbability() {
        final int sampleSize = 10_000;
        final double probabilityOfTrue = 0.3;

        final int[] counts = new int[2];

        final BooleanGenerator generator = generator().probability(probabilityOfTrue);

        for (int i = 0; i < sampleSize; i++) {
            final boolean result = generator.generate(random);
            counts[result ? 1 : 0]++;
        }

        final double falsePercentage = counts[0] / (double) sampleSize;
        final double truePercentage = counts[1] / (double) sampleSize;
        assertThat(falsePercentage).isCloseTo(1 - probabilityOfTrue, withinPercentage(5));
        assertThat(truePercentage).isCloseTo(probabilityOfTrue, withinPercentage(5));
    }

    @Test
    void nullableViaGeneratorSpec() {
        final Stream<Boolean> results = Instancio.of(Boolean.class)
                .generate(allBooleans(), gen -> gen.booleans().nullable())
                .stream()
                .limit(SAMPLE_SIZE);

        assertThat(results).containsNull();
    }

    @Test
    void validation() {
        assertThatThrownBy(() -> generator.probability(-0.000123))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Probability must be between 0 and 1, inclusive: -1.23E-4");

        assertThatThrownBy(() -> generator.probability(1.000001))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Probability must be between 0 and 1, inclusive: 1.000001");
    }
}
