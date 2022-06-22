/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.junit;

import org.junit.jupiter.params.ParameterizedTest;

import static org.assertj.core.api.Assertions.assertThat;

class InstancioSourceTest {

    @InstancioSource(String.class)
    @ParameterizedTest
    void oneArg(final String arg) {
        assertThat(arg).isNotBlank();
    }

    @InstancioSource({String.class, String.class})
    @ParameterizedTest
    void twoArgsSameType(final String first, final String second) {
        assertThat(first).isNotBlank().isNotEqualTo(second);
    }

    @InstancioSource({First.class, Second.class})
    @ParameterizedTest
    void twoArgs(final First first, final Second second) {
        assertThat(first).isNotNull();
        assertThat(second).isNotNull();
        assertThat(first.foo).isNotBlank();
        assertThat(second.bar).isNotBlank();
    }

    static class First {
        String foo;
    }

    static class Second {
        String bar;
    }
}
