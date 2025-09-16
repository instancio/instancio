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
package org.instancio.junit.given;

import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class GivenNestedTest {

    private @Given String string;

    @Test
    void everyStringShouldBePopulated() {
        assertThat(string).isNotBlank();
    }

    @Nested
    class NestedTestClass {
        private @Given String stringNested;

        @Test
        void everyStringShouldBePopulated() {
            assertThat(string).isNotBlank();
            assertThat(stringNested).isNotBlank();
        }

        @Nested
        class DeepNestedTestClass {
            private @Given String stringDeepNested;

            @Test
            void everyStringShouldBePopulated() {
                assertThat(string).isNotBlank();
                assertThat(stringNested).isNotBlank();
                assertThat(stringDeepNested).isNotBlank();
            }
        }
    }
}
