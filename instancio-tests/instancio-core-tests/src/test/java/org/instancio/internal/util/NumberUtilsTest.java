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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

class NumberUtilsTest {

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
}
