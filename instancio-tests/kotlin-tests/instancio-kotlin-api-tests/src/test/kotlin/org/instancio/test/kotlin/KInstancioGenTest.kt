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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstancioExtension::class)
class KInstancioGenTest {

    @Test
    fun string() {
        val result = KInstancio.gen().string().get()

        assertThat(result).isNotBlank()
    }

    @Test
    fun oneOf() {
        val result = KInstancio.gen().oneOf("foo", "bar", "baz").get()

        assertThat(result).isIn("foo", "bar", "baz")
    }

    @Test
    fun list() {
        val results = KInstancio.gen().ints().range(1, 100).list(5)

        assertThat(results).hasSize(5).allMatch { it in 1..100 }
    }

    @Test
    fun stream() {
        val results = KInstancio.gen().string().stream().limit(5).toList()

        assertThat(results).hasSize(5).doesNotContainNull()
    }
}
