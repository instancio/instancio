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
import org.instancio.kotlin.KSelect.allStrings
import org.instancio.kotlin.KSelect.field
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstancioExtension::class)
class KInstancioCartesianProductTest {

    private data class Item(var name: String = "", var count: Int = 0)

    @Nested
    inner class OfCartesianProduct {

        @Test
        fun fromType() {
            val results = KInstancio.ofCartesianProduct<Item>()
                .with(field(Item::name), "foo", "bar")
                .with(field(Item::count), 1, 2)
                .create()

            assertThat(results).hasSize(4)
            assertThat(results.map { it.name }).containsExactly("foo", "foo", "bar", "bar")
            assertThat(results.map { it.count }).containsExactly(1, 2, 1, 2)
        }

        @Test
        fun fromModel() {
            val model = KInstancio.of<Item>()
                .set(allStrings(), "base")
                .toModel()

            val results = KInstancio.ofCartesianProduct(model)
                .with(field(Item::count), 10, 20, 30)
                .create()

            assertThat(results).hasSize(3)
            assertThat(results).allMatch { it.name == "base" }
            assertThat(results.map { it.count }).containsExactly(10, 20, 30)
        }
    }
}
