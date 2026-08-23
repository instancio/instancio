/*
 * Copyright 2022-2026 the original author or authors.
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

/**
 * Verifies that fields annotated with {@code @Given} declared by a superclass
 * are populated in the same way as fields declared by the test class itself.
 */
class GivenInheritedFieldTest {

    @ExtendWith(InstancioExtension.class)
    abstract static class BaseTest {

        protected @Given String inheritedString;
    }

    @Nested
    class InheritedFieldTest extends BaseTest {

        private @Given String declaredString;

        @Test
        void shouldPopulateInheritedAndDeclaredFields() {
            assertThat(inheritedString).isNotBlank();
            assertThat(declaredString).isNotBlank();
            assertThat(inheritedString).isNotEqualTo(declaredString);
        }
    }
}
