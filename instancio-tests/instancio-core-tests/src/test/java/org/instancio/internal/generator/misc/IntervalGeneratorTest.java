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
package org.instancio.internal.generator.misc;

import org.instancio.IntervalSupplier;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IntervalGeneratorTest extends AbstractGeneratorTestTemplate<IntervalSupplier<Integer>, IntervalGenerator<Integer>> {

    private static final int STARTING_VALUE = 1;

    private final IntervalGenerator<Integer> generator = new IntervalGenerator<>(getGeneratorContext(), STARTING_VALUE)
            .nextStart((i, random) -> i)
            .nextEnd((i, random) -> i);

    @Override
    protected String getApiMethod() {
        return null;
    }

    @Override
    protected IntervalGenerator<Integer> generator() {
        return generator;
    }

    @Test
    void generate() {
        assertThat(generator.generate(random)).isInstanceOf(IntervalSupplier.class);
    }

    @Nested
    class ValidationTest {

        @Test
        void nullStartingValue() {
            final GeneratorContext generatorContext = getGeneratorContext();

            assertThatThrownBy(() -> new IntervalGenerator<>(generatorContext, null))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("starting value must not be null");
        }

        @Test
        void nullNextStartFunction() {
            final GeneratorContext generatorContext = getGeneratorContext();
            final IntervalGenerator<Integer> invalidGenerator = new IntervalGenerator<>(generatorContext, STARTING_VALUE)
                    .nextEnd((i, random) -> i);

            assertThatThrownBy(() -> invalidGenerator.generate(random))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("'nextStart' function must not be null");
        }

        @Test
        void nullNextEndFunction() {
            final GeneratorContext generatorContext = getGeneratorContext();
            final IntervalGenerator<Integer> invalidGenerator = new IntervalGenerator<>(generatorContext, STARTING_VALUE)
                    .nextStart((i, random) -> i);

            assertThatThrownBy(() -> invalidGenerator.generate(random))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("'nextEnd' function must not be null");
        }
    }
}
