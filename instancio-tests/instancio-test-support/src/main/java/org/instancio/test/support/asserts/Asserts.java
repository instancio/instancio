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
package org.instancio.test.support.asserts;

import org.assertj.core.api.AbstractCharSequenceAssert;
import org.instancio.test.support.pojo.basic.Numbers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public final class Asserts {

    private Asserts() {
        // non-instantiable
    }

    public static void assertAllNulls(final Object... objs) {
        assertThat(objs).containsOnlyNulls();
    }

    public static void assertNoNulls(final Object... objs) {
        assertThat(objs).doesNotContainNull();
    }

    public static void assertNoZeroes(final int... nums) {
        assertThat(nums).doesNotContain(0);
    }

    public static void assertAllZeroes(final int... nums) {
        assertThat(nums).containsOnly(0);
    }

    public static void assertRange(final Numbers result, final BigDecimal min, final BigDecimal max) {
        assertThat(result.getPrimitiveByte()).isBetween(min.byteValue(), max.byteValue());
        assertThat(result.getPrimitiveShort()).isBetween(min.shortValue(), max.shortValue());
        assertThat(result.getPrimitiveInt()).isBetween(min.intValue(), max.intValue());
        assertThat(result.getPrimitiveLong()).isBetween(min.longValue(), max.longValue());
        assertThat(result.getPrimitiveFloat()).isBetween(min.floatValue(), max.floatValue());
        assertThat(result.getPrimitiveDouble()).isBetween(min.doubleValue(), max.doubleValue());

        assertThat(result.getByteWrapper()).isBetween(min.byteValue(), max.byteValue());
        assertThat(result.getShortWrapper()).isBetween(min.shortValue(), max.shortValue());
        assertThat(result.getIntegerWrapper()).isBetween(min.intValue(), max.intValue());
        assertThat(result.getLongWrapper()).isBetween(min.longValue(), max.longValue());
        assertThat(result.getFloatWrapper()).isBetween(min.floatValue(), max.floatValue());
        assertThat(result.getDoubleWrapper()).isBetween(min.doubleValue(), max.doubleValue());

        assertThat(result.getBigInteger()).isBetween(min.toBigInteger(), max.toBigInteger());
        assertThat(result.getBigDecimal()).isBetween(min, max);
    }

    public static AbstractCharSequenceAssert<?, String> assertHasFieldPrefix(
            final String prefix, final String actual) {

        return assertThat(actual)
                .startsWith(prefix)
                .hasSizeGreaterThan(prefix.length());
    }

    public static AbstractCharSequenceAssert<?, String> assertDoesNotHaveFieldPrefix(
            final String prefix, final String actual) {

        return assertThat(actual).isNotBlank().doesNotContain(prefix);
    }
}
