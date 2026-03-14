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
import org.instancio.Random
import org.instancio.Select.allStrings
import org.instancio.generator.Generator
import org.instancio.junit.InstancioExtension
import org.instancio.test.kotlin.jspecify.support.assertKotlinCompilationError
import org.instancio.test.support.tags.Feature
import org.instancio.test.support.tags.FeatureTag
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Verifies that
 *
 * <ul>
 *   <li>nullable and non-nullable type declarations are valid</li>
 *   <li>no NPE is thrown when returning null values via various APIs</li>
 * </ul>
 */
@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension::class)
internal class KCustomGeneratorJspecifyTest {

    class NullableStringGenerator : Generator<String?> { // String?
        override fun generate(random: Random): String? { // String?
            return null
        }
    }

    class NonNullStringGenerator : Generator<String> {  // String
        override fun generate(random: Random): String { // String
            return "foo"
        }
    }

    @Test
    fun `when implementing Generator of String, returning nullable String should trigger compile error`() {
        val source = """
        import org.instancio.Random
        import org.instancio.generator.Generator
        
        class NullableStringGenerator : Generator<String> {  // String
            override fun generate(random: Random): String? { // String?
                return null
            }
        }
        """
        assertKotlinCompilationError(
            source,
            "Return type of 'fun generate(random: Random): String?' is not a subtype of the return type of the overridden member"
        )
    }

    @Nested
    inner class CreateTest {

        @Test
        fun nullableGeneratorType() {
            val result: String? = Instancio.of(String::class.java)
                .supply(allStrings(), NullableStringGenerator())
                .create()

            assertThat(result).isNull()
        }

        @Test
        fun nonNullGeneratorType() {
            val result: String = Instancio.of(String::class.java)
                .supply(allStrings(), NonNullStringGenerator())
                .create()

            assertThat(result).isEqualTo("foo")
        }
    }

    @Nested
    inner class CreateStreamTest {

        @Test
        fun nullableGeneratorType() {
            val result = Instancio.of(String::class.java)
                .supply(allStrings(), NullableStringGenerator())
                .stream()
                .limit(5)

            assertThat(result).containsOnlyNulls()
        }

        @Test
        fun nonNullGeneratorType() {
            val result = Instancio.of(String::class.java)
                .supply(allStrings(), NonNullStringGenerator())
                .stream()
                .limit(5)

            assertThat(result).doesNotContainNull()
        }
    }
}