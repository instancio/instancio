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
package org.instancio.internal.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringConvertersTest {

    @Test
    void verifyFunctions() {
        assertThat(StringConverters.getConverter(Boolean.class).apply("true")).isTrue();
        assertThat(StringConverters.getConverter(Byte.class).apply("5")).isEqualTo((byte) 5);
        assertThat(StringConverters.getConverter(Short.class).apply("6")).isEqualTo((short) 6);
        assertThat(StringConverters.getConverter(Integer.class).apply("7")).isEqualTo(7);
        assertThat(StringConverters.getConverter(Long.class).apply("8")).isEqualTo(8L);
        assertThat(StringConverters.getConverter(Float.class).apply("10.8")).isEqualTo(10.8f);
        assertThat(StringConverters.getConverter(Double.class).apply("10.2")).isEqualTo(10.2d);
        // primitives
        assertThat(StringConverters.getConverter(boolean.class).apply("true")).isTrue();
        assertThat(StringConverters.getConverter(byte.class).apply("5")).isEqualTo((byte) 5);
        assertThat(StringConverters.getConverter(short.class).apply("6")).isEqualTo((short) 6);
        assertThat(StringConverters.getConverter(int.class).apply("7")).isEqualTo(7);
        assertThat(StringConverters.getConverter(long.class).apply("8")).isEqualTo(8L);
        assertThat(StringConverters.getConverter(float.class).apply("10.8")).isEqualTo(10.8f);
        assertThat(StringConverters.getConverter(double.class).apply("10.2")).isEqualTo(10.2d);
    }
}
