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
package org.instancio.test.beanvalidation;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.pojo.beanvalidation.NumbersDigitsBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DD;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class NumbersDigitsBVTest {

    @RepeatedTest(SAMPLE_SIZE_DD)
    void noFraction() {
        final NumbersDigitsBV.NoFraction result = Instancio.create(NumbersDigitsBV.NoFraction.class);

        assertThat(result.getPrimitiveByte()).isBetween((byte) -9, (byte) 9);
        assertThat(result.getPrimitiveShort()).isBetween((short) -99, (short) 99);
        assertThat(result.getPrimitiveInt()).isBetween(-999, 999);
        assertThat(result.getPrimitiveLong()).isBetween(-9999L, 9999L);
        assertThat(result.getPrimitiveFloat())
                .isBetween(-99999f, 99999f)
                .asString()
                .matches("^-?\\d+\\.\\d$");

        assertThat(result.getPrimitiveDouble())
                .isBetween(-999999d, 999999d)
                .asString()
                .matches("^-?\\d+\\.\\d$");

        assertThat(result.getByteWrapper()).isBetween((byte) -9, (byte) 9);
        assertThat(result.getShortWrapper()).isBetween((short) -99, (short) 99);
        assertThat(result.getIntegerWrapper()).isBetween(-999, 999);
        assertThat(result.getLongWrapper()).isBetween(-9999L, 9999L);
        assertThat(result.getFloatWrapper())
                .isBetween(-99999f, 99999f)
                .asString()
                .matches("^-?\\d+\\.\\d$");

        assertThat(result.getDoubleWrapper())
                .isBetween(-999999d, 999999d)
                .asString()
                .matches("^-?\\d+\\.\\d$");

        assertThat(result.getBigInteger()).isBetween(
                new BigInteger("-99999999999"),
                new BigInteger("99999999999"));

        assertThat(result.getBigDecimal()).isBetween(
                new BigDecimal("-999999999999"),
                new BigDecimal("999999999999"));
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withFraction() {
        final NumbersDigitsBV.WithFraction result = Instancio.create(NumbersDigitsBV.WithFraction.class);

        assertThat(result.getPrimitiveFloat())
                .isBetween(-999f, 999.9f)
                .asString()
                .matches("^-?\\d+\\.\\d$");

        assertThat(result.getPrimitiveDouble())
                .isBetween(-9999.99d, 9999.99d)
                .asString()
                .matches("^-?\\d+\\.\\d{1,2}$");

        assertThat(result.getFloatWrapper())
                .isBetween(-99999.999f, 99999.999f)
                .asString()
                .matches("^-?\\d+\\.\\d{1,3}$");

        assertThat(result.getDoubleWrapper())
                .isBetween(-999999.9999d, 999999.9999d)
                .asString()
                .matches("^-?\\d+\\.\\d{1,4}$");

        assertThat(result.getBigDecimal())
                .isBetween(
                        new BigDecimal("-999999999.99999999999999999999"),
                        new BigDecimal("999999999.99999999999999999999"))
                .hasScaleOf(20);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withZeroInteger() {
        final NumbersDigitsBV.WithZeroInteger result = Instancio.create(NumbersDigitsBV.WithZeroInteger.class);

        assertThat(result.getPrimitiveFloat())
                .isBetween(-0.9f, 0.9f)
                .asString()
                .matches("^-?0.\\d$");

        assertThat(result.getPrimitiveDouble())
                .isBetween(-0.99d, 0.99d)
                .asString()
                .matches("^-?0.\\d{1,2}$");

        assertThat(result.getBigDecimal())
                .isBetween(
                        new BigDecimal("-0.9999"),
                        new BigDecimal("0.9999"))
                .hasScaleOf(4);
    }
}
