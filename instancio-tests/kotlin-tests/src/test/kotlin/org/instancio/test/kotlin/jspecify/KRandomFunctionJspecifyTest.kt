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
import org.instancio.Assign
import org.instancio.Instancio
import org.instancio.RandomFunction
import org.instancio.junit.InstancioExtension
import org.instancio.test.kotlin.KSelectorsTest.KSelect.Companion.field
import org.instancio.test.support.pojo.misc.StringsAbc
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Verifies Kotlin's compatibility with JSpecify-annotated [RandomFunction]
 * with both nullable and non-nullable type arguments in lambdas.
 */
@ExtendWith(InstancioExtension::class)
internal class KRandomFunctionJspecifyTest {

    @Nested
    inner class RandomFunctionTest {

        fun <T, R> create(valueMapper: RandomFunction<T, R>): StringsAbc {
            val assignment = Assign
                .valueOf(field(StringsAbc::a))
                .to(field(StringsAbc::b))
                .`as`(valueMapper)

            return Instancio.of(StringsAbc::class.java)
                .assign(assignment)
                .create()
        }

        @Test
        fun randomFunction_nonNullInputAndOutput() {
            val valueMapper = RandomFunction<String, String> { input, _ -> input }
            val result = create(valueMapper)
            assertThat(result.b).isNotNull().isEqualTo(result.a)
        }

        @Test
        fun randomFunction_nullableInputAndOutput() {
            val valueMapper = RandomFunction<String?, String?> { input: String?, random -> null }
            val result = create(valueMapper)
            assertThat(result.b).isNull();
        }
    }
}