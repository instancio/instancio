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
package org.instancio.internal.conditional;

import org.instancio.Select;
import org.instancio.exception.InstancioApiException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InternalConditionalTest {

    @Nested
    class ToStringTest {

        @Test
        void verifyEmptyToString() {
            assertThat(InternalConditional.builder().build())
                    .hasToString("InternalConditional[origin=null, destination=null]");
        }

        @Test
        void verifyToString() {
            assertThat(InternalConditional.builder()
                    .origin(Select.field("foo"))
                    .destination(Select.field("bar"))
                    .build())
                    .hasToString("InternalConditional[origin=field(\"foo\"), destination=field(\"bar\")]");
        }
    }

    @Nested
    class ValidationTest {
        private final InternalConditional.Builder builder = InternalConditional.builder();

        @Test
        void origin() {
            assertThatThrownBy(() -> builder.origin(null))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("origin selector must not be null");
        }

        @Test
        void destination() {
            assertThatThrownBy(() -> builder.destination(null))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("destination selector must not be null");
        }

        @Test
        void predicate() {
            assertThatThrownBy(() -> builder.originPredicate(null))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("predicate must not be null");
        }
    }
}
