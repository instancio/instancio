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
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.pojo.beanvalidation.NumbersPositiveBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DD;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class NumbersPositiveBVTest {

    // Validation annotations should take precedence over Settings
    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.BYTE_MIN, Byte.MIN_VALUE)
            .set(Keys.SHORT_MIN, Short.MIN_VALUE)
            .set(Keys.INTEGER_MIN, Integer.MIN_VALUE)
            .set(Keys.LONG_MIN, Long.MIN_VALUE)
            .set(Keys.FLOAT_MIN, Float.MIN_VALUE)
            .set(Keys.DOUBLE_MIN, Double.MIN_VALUE)
            .set(Keys.BYTE_MAX, (byte) -1)
            .set(Keys.SHORT_MAX, (short) -1)
            .set(Keys.INTEGER_MAX, -1)
            .set(Keys.LONG_MAX, -1L)
            .set(Keys.FLOAT_MAX, -1f)
            .set(Keys.DOUBLE_MAX, -1d);

    @RepeatedTest(SAMPLE_SIZE_DD)
    void positive() {
        final NumbersPositiveBV.PositiveNumbers result = Instancio.create(NumbersPositiveBV.PositiveNumbers.class);

        assertThat(result.getPrimitiveByte()).isPositive();
        assertThat(result.getPrimitiveShort()).isPositive();
        assertThat(result.getPrimitiveInt()).isPositive();
        assertThat(result.getPrimitiveLong()).isPositive();
        assertThat(result.getPrimitiveFloat()).isPositive();
        assertThat(result.getPrimitiveDouble()).isPositive();

        assertThat(result.getByteWrapper()).isPositive();
        assertThat(result.getShortWrapper()).isPositive();
        assertThat(result.getIntegerWrapper()).isPositive();
        assertThat(result.getLongWrapper()).isPositive();
        assertThat(result.getFloatWrapper()).isPositive();
        assertThat(result.getDoubleWrapper()).isPositive();

        assertThat(result.getBigInteger()).isPositive();
        assertThat(result.getBigDecimal()).isPositive();
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void positiveOrZero() {
        final NumbersPositiveBV.PositiveOrZeroNumbers result = Instancio.create(NumbersPositiveBV.PositiveOrZeroNumbers.class);

        assertThat(result.getPrimitiveByte()).isNotNegative();
        assertThat(result.getPrimitiveShort()).isNotNegative();
        assertThat(result.getPrimitiveInt()).isNotNegative();
        assertThat(result.getPrimitiveLong()).isNotNegative();
        assertThat(result.getPrimitiveFloat()).isNotNegative();
        assertThat(result.getPrimitiveDouble()).isNotNegative();

        assertThat(result.getByteWrapper()).isNotNegative();
        assertThat(result.getShortWrapper()).isNotNegative();
        assertThat(result.getIntegerWrapper()).isNotNegative();
        assertThat(result.getLongWrapper()).isNotNegative();
        assertThat(result.getFloatWrapper()).isNotNegative();
        assertThat(result.getDoubleWrapper()).isNotNegative();

        assertThat(result.getBigInteger()).isNotNegative();
        assertThat(result.getBigDecimal()).isNotNegative();
    }
}
