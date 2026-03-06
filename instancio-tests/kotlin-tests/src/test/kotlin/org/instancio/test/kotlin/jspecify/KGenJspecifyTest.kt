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
package org.instancio.test.kotlin.jspecify

import org.assertj.core.api.Assertions.assertThat
import org.instancio.Instancio
import org.instancio.junit.InstancioExtension
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.stream.Stream

/**
 * Verifies Kotlin's compatibility with JSpecify-annotated [org.instancio.InstancioGenApi]
 * with nullable and non-null return types.
 */
@ExtendWith(InstancioExtension::class)
internal class KGenJspecifyTest {

    @Nested
    inner class Nullable {
        @Test
        fun get() {
            val result: Stream<String?> = Stream
                .generate { Instancio.gen().string().nullable().get() }
                .limit(100)

            assertThat(result).containsNull();
        }

        @Test
        fun list() {
            val result: List<String?> = Instancio.gen().string().nullable().list(100)

            assertThat(result).containsNull()
        }

        @Test
        fun stream() {
            val result: Stream<String?> = Instancio.gen().string().nullable().stream().limit(100)

            assertThat(result).containsNull()
        }
    }

    @Nested
    inner class NonNull {
        @Test
        fun get() {
            val result: Stream<String> = Stream
                .generate { Instancio.gen().string().get() }
                .limit(100)

            assertThat(result).doesNotContainNull()
        }

        @Test
        fun list() {
            val result: List<String> = Instancio.gen().string().list(100)

            assertThat(result).doesNotContainNull()
        }

        @Test
        fun stream() {
            val result: Stream<String> = Instancio.gen().string().stream().limit(100)

            assertThat(result).doesNotContainNull()
        }
    }
}