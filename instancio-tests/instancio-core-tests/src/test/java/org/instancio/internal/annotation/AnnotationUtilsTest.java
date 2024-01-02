/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.internal.annotation;

import org.instancio.internal.util.Constants;
import org.instancio.internal.util.Range;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Gen.ints;

class AnnotationUtilsTest {

    private static final int SAMPLE_SIZE = 1000;

    @Test
    void minAndMaxAreZero() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final int maxLimit = ints().range(0, 100).get();
            final Range<Integer> result = AnnotationUtils.calculateRange(0, 0, maxLimit);
            assertThat(result.min()).isEqualTo(result.max()).isZero();
        }
    }

    @Test
    void minIsZeroAndMaxIsVeryLarge() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final int veryMaxLarge = ints().range(Integer.MAX_VALUE - 10000, Integer.MAX_VALUE).get();
            final int maxLimit = ints().range(1, 1000).get();

            final Range<Integer> result = AnnotationUtils.calculateRange(0, veryMaxLarge, maxLimit);
            assertThat(result.min()).isOne();
            assertThat(result.max()).isEqualTo(maxLimit);
        }
    }

    @Test
    void minIsLessThanHalfOfMax() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final int min = ints().range(2, 100).get();
            final int max = min + min + ints().range(2, 5).get();

            // maxLimit argument is irrelevant for this test
            // (it is calculated internally)
            final int maxLimit = -1;

            final int expectedMax = (min * (100 + Constants.RANGE_ADJUSTMENT_PERCENTAGE)) / 100;

            final Range<Integer> result = AnnotationUtils.calculateRange(min, max, maxLimit);
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

            final Range<Integer> result = AnnotationUtils.calculateRange(min, max, maxLimit);
            assertThat(result.min()).isEqualTo(min);
            assertThat(result.max()).isEqualTo(max);
        }
    }

    @ValueSource(ints = {0, 1})
    @ParameterizedTest
    void minSizeIsOne(final int min) {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final int max = ints().range(2, 100).get();
            final int maxLimit = ints().range(2, 100).get();

            final int expectedMin = 1;
            final int expectedMax = Math.min(max, maxLimit);

            final Range<Integer> result = AnnotationUtils.calculateRange(min, max, maxLimit);

            assertThat(result.min()).isEqualTo(expectedMin);
            assertThat(result.max()).isEqualTo(expectedMax);
        }
    }
}
