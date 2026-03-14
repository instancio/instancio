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
package org.instancio.test.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.instancio.junit.InstancioExtension
import org.instancio.kotlin.KInstancio
import org.instancio.kotlin.KSelect.field
import org.instancio.test.kotlin.pojo.person.basic.KStringHolder
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstancioExtension::class)
class KInstancioFillTest {

    @Nested
    inner class Fill {

        @Test
        fun populatesNullFields() {
            val obj = KStringHolder()

            KInstancio.fill(obj)

            assertThat(obj.value).isNotBlank()
        }

        @Test
        fun preservesExistingValues() {
            val obj = KStringHolder()
            obj.value = "existing"

            KInstancio.fill(obj)

            assertThat(obj.value).isEqualTo("existing")
        }
    }

    @Nested
    inner class OfObject {

        @Test
        fun populatesNullFields() {
            val obj = KStringHolder()

            KInstancio.ofObject(obj).fill()

            assertThat(obj.value).isNotBlank()
        }

        @Test
        fun withSelector() {
            val obj = KStringHolder()

            KInstancio.ofObject(obj)
                .set(field(KStringHolder::value), "custom")
                .fill()

            assertThat(obj.value).isEqualTo("custom")
        }

        @Test
        fun preservesExistingValues() {
            val obj = KStringHolder()
            obj.value = "existing"

            // fill() without selectors preserves existing non-null values
            KInstancio.ofObject(obj).fill()

            assertThat(obj.value).isEqualTo("existing")
        }
    }
}
