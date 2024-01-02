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

class ObjectUtilsTest {

    @Test
    void defaultValue() {
        assertThat(ObjectUtils.defaultValue(boolean.class)).isFalse();
        assertThat(ObjectUtils.defaultValue(byte.class)).isZero();
        assertThat(ObjectUtils.defaultValue(short.class)).isZero();
        assertThat(ObjectUtils.defaultValue(char.class)).isEqualTo('\u0000');
        assertThat(ObjectUtils.defaultValue(int.class)).isZero();
        assertThat(ObjectUtils.defaultValue(long.class)).isZero();
        assertThat(ObjectUtils.defaultValue(float.class)).isZero();
        assertThat(ObjectUtils.defaultValue(double.class)).isZero();
        assertThat(ObjectUtils.defaultValue(Object.class)).isNull();
    }
}
