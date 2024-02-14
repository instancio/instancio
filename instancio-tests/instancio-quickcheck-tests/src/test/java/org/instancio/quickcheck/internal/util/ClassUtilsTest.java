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
package org.instancio.quickcheck.internal.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.quickcheck.internal.util.ClassUtils.nullSafeToString;

class ClassUtilsTest {
    @Test
    void nullSafeToStringWithDefaultMapper() {
        assertThat(nullSafeToString((Class<?>[]) null)).isEmpty();
        assertThat(nullSafeToString()).isEmpty();
        assertThat(nullSafeToString(String.class)).isEqualTo("java.lang.String");
        assertThat(nullSafeToString(String.class, Integer.class)).isEqualTo("java.lang.String, java.lang.Integer");
        assertThat(nullSafeToString(String.class, null, Integer.class)).isEqualTo("java.lang.String, null, java.lang.Integer");
    }

    @Test
    void nullSafeToStringWithCustomMapper() {
        assertThat(nullSafeToString(Class::getSimpleName, (Class<?>[]) null)).isEmpty();
        assertThat(nullSafeToString(Class::getSimpleName)).isEmpty();
        assertThat(nullSafeToString(Class::getSimpleName, String.class)).isEqualTo("String");
        assertThat(nullSafeToString(Class::getSimpleName, String.class, Integer.class)).isEqualTo("String, Integer");
        assertThat(nullSafeToString(Class::getSimpleName, String.class, null, Integer.class)).isEqualTo("String, null, Integer");
    }
}
