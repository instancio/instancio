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
package org.instancio.test.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.instancio.Instancio
import org.instancio.Select.*
import org.instancio.TypeToken
import org.instancio.generators.Generators
import org.instancio.junit.InstancioExtension
import org.instancio.test.support.tags.GenericsTag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@GenericsTag
@ExtendWith(InstancioExtension::class)
internal class KGenericsTest {

    private inline fun <reified T> genericType() = object : TypeToken<T> {}

    private class KPair<L, R>(val left: L, val right: R) {}

    @Test
    fun genericPair() {
        val stringLength = 7
        val result: KPair<String, Int> = Instancio.of(genericType<KPair<String, Int>>())
            .generate(allStrings()) { gen: Generators -> gen.string().length(stringLength) }
            .generate(allInts()) { gen: Generators -> gen.ints().range(10, 20) }
            .create();

        assertThat(result.left).hasSize(stringLength)
        assertThat(result.right).isBetween(10, 20)
    }

    @Test
    fun listOfPairs() {
        val expectedLong = -1L
        val result: List<KPair<String, Long>> = Instancio.of(genericType<List<KPair<String, Long>>>())
            .generate(allStrings()) { gen: Generators -> gen.oneOf("foo", "bar") }
            .set(allLongs(), expectedLong)
            .create();

        assertThat(result).isNotEmpty
        result.forEach {
            assertThat(it.left).isIn("foo", "bar")
            assertThat(it.right).isEqualTo(expectedLong)

        }
    }
}