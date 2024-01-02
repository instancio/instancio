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
package org.instancio.test.beanvalidation;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.pojo.beanvalidation.NumbersDecimalMinMaxPositiveBV;
import org.instancio.test.support.pojo.basic.Numbers;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.pojo.beanvalidation.NumbersDecimalMinMaxPositiveBV.MAX;
import static org.instancio.test.pojo.beanvalidation.NumbersDecimalMinMaxPositiveBV.MAX_ROUNDED;
import static org.instancio.test.pojo.beanvalidation.NumbersDecimalMinMaxPositiveBV.MIN;
import static org.instancio.test.pojo.beanvalidation.NumbersDecimalMinMaxPositiveBV.MIN_ROUNDED;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class NumbersDecimalMinMaxPositiveBVTest {

    @Test
    void decimalMinMax() {
        final Numbers result = Instancio.create(NumbersDecimalMinMaxPositiveBV.class);

        assertThat(result.getPrimitiveByte()).isBetween(MIN_ROUNDED.byteValue(), MAX_ROUNDED.byteValue());
        assertThat(result.getPrimitiveShort()).isBetween(MIN_ROUNDED.shortValue(), MAX_ROUNDED.shortValue());
        assertThat(result.getPrimitiveInt()).isBetween(MIN_ROUNDED.intValue(), MAX_ROUNDED.intValue());
        assertThat(result.getPrimitiveLong()).isBetween(MIN_ROUNDED.longValue(), MAX_ROUNDED.longValue());
        assertThat(result.getPrimitiveFloat()).isBetween(MIN.floatValue(), MAX_ROUNDED.floatValue());
        assertThat(result.getPrimitiveDouble()).isBetween(MIN.doubleValue(), MAX_ROUNDED.doubleValue());

        assertThat(result.getByteWrapper()).isBetween(MIN_ROUNDED.byteValue(), MAX_ROUNDED.byteValue());
        assertThat(result.getShortWrapper()).isBetween(MIN_ROUNDED.shortValue(), MAX_ROUNDED.shortValue());
        assertThat(result.getIntegerWrapper()).isBetween(MIN_ROUNDED.intValue(), MAX_ROUNDED.intValue());
        assertThat(result.getLongWrapper()).isBetween(MIN_ROUNDED.longValue(), MAX_ROUNDED.longValue());
        assertThat(result.getFloatWrapper()).isBetween(MIN.floatValue(), MAX_ROUNDED.floatValue());
        assertThat(result.getDoubleWrapper()).isBetween(MIN.doubleValue(), MAX_ROUNDED.doubleValue());

        assertThat(result.getBigInteger()).isBetween(MIN_ROUNDED.toBigInteger(), MAX_ROUNDED.toBigInteger());
        assertThat(result.getBigDecimal()).isBetween(MIN, MAX);
    }
}
