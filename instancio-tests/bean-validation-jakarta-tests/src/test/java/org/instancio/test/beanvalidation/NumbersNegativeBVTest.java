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
import org.instancio.test.pojo.beanvalidation.NumbersNegativeBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DD;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class NumbersNegativeBVTest {

    @RepeatedTest(SAMPLE_SIZE_DD)
    void negative() {
        final NumbersNegativeBV.NegativeNumbers result = Instancio.create(NumbersNegativeBV.NegativeNumbers.class);

        assertThat(result.getPrimitiveByte()).isNegative();
        assertThat(result.getPrimitiveShort()).isNegative();
        assertThat(result.getPrimitiveInt()).isNegative();
        assertThat(result.getPrimitiveLong()).isNegative();
        assertThat(result.getPrimitiveFloat()).isNegative();
        assertThat(result.getPrimitiveDouble()).isNegative();

        assertThat(result.getByteWrapper()).isNegative();
        assertThat(result.getShortWrapper()).isNegative();
        assertThat(result.getIntegerWrapper()).isNegative();
        assertThat(result.getLongWrapper()).isNegative();
        assertThat(result.getFloatWrapper()).isNegative();
        assertThat(result.getDoubleWrapper()).isNegative();

        assertThat(result.getBigInteger()).isNegative();
        assertThat(result.getBigDecimal()).isNegative();
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void negativeOrZero() {
        final NumbersNegativeBV.NegativeOrZeroNumbers result = Instancio.create(NumbersNegativeBV.NegativeOrZeroNumbers.class);

        assertThat(result.getPrimitiveByte()).isNotPositive();
        assertThat(result.getPrimitiveShort()).isNotPositive();
        assertThat(result.getPrimitiveInt()).isNotPositive();
        assertThat(result.getPrimitiveLong()).isNotPositive();
        assertThat(result.getPrimitiveFloat()).isNotPositive();
        assertThat(result.getPrimitiveDouble()).isNotPositive();

        assertThat(result.getByteWrapper()).isNotPositive();
        assertThat(result.getShortWrapper()).isNotPositive();
        assertThat(result.getIntegerWrapper()).isNotPositive();
        assertThat(result.getLongWrapper()).isNotPositive();
        assertThat(result.getFloatWrapper()).isNotPositive();
        assertThat(result.getDoubleWrapper()).isNotPositive();

        assertThat(result.getBigInteger()).isNotPositive();
        assertThat(result.getBigDecimal()).isNotPositive();
    }
}
