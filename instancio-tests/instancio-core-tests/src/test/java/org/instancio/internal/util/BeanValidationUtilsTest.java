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
package org.instancio.internal.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Gen.ints;

class BeanValidationUtilsTest {

    private static final int SAMPLE_SIZE = 1000;

    @Test
    void minAndMaxAreZero() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final int maxLimit = ints().range(0, 100).get();
            final IntRange result = BeanValidationUtils.calculateRange(0, 0, maxLimit);
            assertThat(result.min()).isEqualTo(result.max()).isZero();
        }
    }

    @Test
    void minIsZeroAndMaxIsVeryLarge() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final int veryMaxLarge = ints().range(Integer.MAX_VALUE - 10000, Integer.MAX_VALUE).get();
            final int maxLimit = ints().range(1, 1000).get();

            final IntRange result = BeanValidationUtils.calculateRange(0, veryMaxLarge, maxLimit);
            assertThat(result.min()).isOne();
            assertThat(result.max()).isEqualTo(maxLimit);
        }
    }

    @Test
    void minIsLessThanHalfOfMax() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final int min = ints().range(1, 100).get();
            final int max = min + min + ints().range(1, 5).get();
            // irrelevant for this test
            final int maxLimit = ints().range(Integer.MIN_VALUE, Integer.MAX_VALUE).get();

            final int expectedMax = (min * (100 + Constants.RANGE_ADJUSTMENT_PERCENTAGE)) / 100;

            final IntRange result = BeanValidationUtils.calculateRange(min, max, maxLimit);
            assertThat(result.min()).isEqualTo(min);
            assertThat(result.max()).isEqualTo(expectedMax);
        }
    }

    @Test
    void minIsEqualToOrGreaterThanHalfOfMax() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final int minLimit = 6;
            final int min = ints().range(minLimit, 100).get();
            final int max = min * 2 - ints().range(0, minLimit).get();
            // irrelevant for this test
            final int maxLimit = ints().range(Integer.MIN_VALUE, Integer.MAX_VALUE).get();

            final IntRange result = BeanValidationUtils.calculateRange(min, max, maxLimit);
            assertThat(result.min()).isEqualTo(min);
            assertThat(result.max()).isEqualTo(max);
        }
    }
}
