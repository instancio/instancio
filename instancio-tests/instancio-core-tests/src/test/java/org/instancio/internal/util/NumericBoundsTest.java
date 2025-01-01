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
package org.instancio.internal.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class NumericBoundsTest {

    @ParameterizedTest
    @CsvSource({
            "-1000, 0, 2, -99, 0, Row 1",
            "-1000, 1000, 2, -99, 99, Row 2",
            "0, 1000, 2, 0, 99, Row 3",
            "-9999999, 9999999, 4, -9999, 9999, Row 4",
            "0, 9999999, 3, 0, 999, Row 5",
            "0, 99999999, 8, 0, 99999999, Row 6",
            "-500, 500, 2, -99, 99, Row 7",
            "-1, 1, 1, -1, 1, Row 8",
            "-10, 10, 1, -9, 9, Row 9",
            "-100000, 0, 3, -999, 0, Row 10",
            "-123456789, 123456789, 6, -999999, 999999, Row 11",
            "500, 1000, 3, 500, 999, Row 12"
    })
    void updateBounds(
            final BigDecimal initialMin,
            final BigDecimal initialMax,
            final int digits,
            final BigDecimal expectedMin,
            final BigDecimal expectedMax,
            final String row) {

        final NumericBounds range = new NumericBounds(initialMin, initialMax);

        final NumericBounds result = range.updateBounds(digits);

        assertThat(result.getMin()).as(row + ": Min").isEqualTo(expectedMin);
        assertThat(result.getMax()).as(row + ": Max").isEqualTo(expectedMax);
    }
}