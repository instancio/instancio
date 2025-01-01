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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

class NumberUtilsTest {

    @Test
    void calculateNewMin() {
        assertNewMin(1, 0, 50, -2);
        assertNewMin(1, -1, 50, -2);
        assertNewMin(1, -2, 50, -3);
        assertNewMin(6, 5, 50, 3);
        assertNewMin(1000, 500, 100, 0);

        // current min unchanged
        assertNewMin(5, 6, 50, 5);

        // equal min/max
        assertNewMin(0, 0, 200, 0);
        assertNewMin(10, 10, 100, 10);
    }

    @Test
    void calculateNewMax() {
        assertNewMax(-1, 0, 50, 2);
        assertNewMax(-1, 1, 50, 2);
        assertNewMax(-1, 2, 50, 3);
        assertNewMax(5, 6, 50, 9);
        assertNewMax(-1000, -500, 100, 0);

        // current max unchanged
        assertNewMax(5, 4, 50, 5);

        // equal min/max
        assertNewMax(0, 0, 200, 0);
        assertNewMax(10, 10, 100, 10);
    }

    private static <T extends Number & Comparable<T>> void assertNewMin(
            final T curMin, final T newMax, final int percentage, final T expected) {

        final T newMin = NumberUtils.calculateNewMin(curMin, newMax, percentage);
        assertThat(newMin).isEqualTo(expected);
    }

    private static <T extends Number & Comparable<T>> void assertNewMax(
            final T curMax, final T newMin, final int percentage, final T expected) {

        final T newMax = NumberUtils.calculateNewMax(curMax, newMin, percentage);
        assertThat(newMax).isEqualTo(expected);
    }

    @CsvSource({
            "0, 0",
            "1, 1",
            "9, 9",
            "10, 1",
            "11, 2",
            "19, 10",
            "12345, 15",
    })
    @ParameterizedTest
    void sumDigits(final int digits, final int result) {
        assertThat(NumberUtils.sumDigits(digits)).isEqualTo(result);
    }

    @MethodSource("digitSource")
    @ParameterizedTest
    void toDigitInt(final int digitInt, final char digitChar) {
        assertThat(NumberUtils.toDigitInt(digitChar)).isEqualTo(digitInt);
    }

    @MethodSource("digitSource")
    @ParameterizedTest
    void toDigitChar(final int digitInt, final char digitChar) {
        assertThat(NumberUtils.toDigitChar(digitInt)).isEqualTo(digitChar);
    }

    @Test
    void isZero() {
        assertThat(NumberUtils.isZero(new BigDecimal("0"))).isTrue();
        assertThat(NumberUtils.isZero(new BigDecimal("0.0000"))).isTrue();
        assertThat(NumberUtils.isZero(new BigDecimal("0.00000001"))).isFalse();
    }

    @ValueSource(classes = {
            byte.class, short.class, int.class, long.class,
            Byte.class, Short.class, Integer.class, Long.class,
            BigInteger.class
    })
    @ParameterizedTest
    void getFromBigDecimalConverterWithRounding(final Class<Number> number) {
        assertConversion("1.5009", number, 2);
        assertConversion("1.49", number, 1);
        assertConversion("-1.5009", number, -2);
        assertConversion("-1.49", number, -1);
    }

    @ValueSource(classes = {
            float.class, double.class,
            Float.class, Double.class,
            BigDecimal.class
    })
    @ParameterizedTest
    void getFromBigDecimalConverter(final Class<Number> number) {
        assertConversion("1.5009", number, 1.5009);
        assertConversion("1.49", number, 1.49);
        assertConversion("-1.5009", number, -1.5009);
        assertConversion("-1.49", number, -1.49);
    }

    private void assertConversion(final String fromDecimal, final Class<Number> toNumber, final double expected) {
        final BigDecimal bd = new BigDecimal(fromDecimal);
        final Number result = NumberUtils.bigDecimalConverter(toNumber).apply(bd);
        assertThat(result.doubleValue())
                .as("Unexpected result converting %s to %s, expected: %s", fromDecimal, toNumber, expected)
                .isEqualTo(expected, withPrecision(0.0000001));
    }

    private static Stream<Arguments> digitSource() {
        return IntStream.range(0, 10).mapToObj(i -> Arguments.of(i, (char) (i + '0')));
    }
}
