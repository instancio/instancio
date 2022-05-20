/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.settings;

import org.instancio.Mode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValueOfFunctionsTest {

    @Test
    void verifyFunctions() {
        assertThat(ValueOfFunctions.getFunction(Boolean.class).apply("true")).isEqualTo(true);
        assertThat(ValueOfFunctions.getFunction(Byte.class).apply("5")).isEqualTo((byte) 5);
        assertThat(ValueOfFunctions.getFunction(Short.class).apply("6")).isEqualTo((short) 6);
        assertThat(ValueOfFunctions.getFunction(Integer.class).apply("7")).isEqualTo(7);
        assertThat(ValueOfFunctions.getFunction(Long.class).apply("8")).isEqualTo(8L);
        assertThat(ValueOfFunctions.getFunction(Float.class).apply("10.8")).isEqualTo(10.8f);
        assertThat(ValueOfFunctions.getFunction(Double.class).apply("10.2")).isEqualTo(10.2d);
        assertThat(ValueOfFunctions.getFunction(Mode.class).apply("LENIENT")).isEqualTo(Mode.LENIENT);
    }
}
