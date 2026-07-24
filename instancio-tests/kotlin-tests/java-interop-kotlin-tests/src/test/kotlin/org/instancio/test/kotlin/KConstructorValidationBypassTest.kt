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
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.instancio.Instancio
import org.instancio.Select.field
import org.instancio.exception.InstancioApiException
import org.instancio.junit.Given
import org.instancio.junit.InstancioExtension
import org.instancio.settings.Keys
import org.instancio.settings.OnConstructorError
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Documents how constructor-based instantiation interacts with two Kotlin
 * language guarantees that are enforced *inside* the constructor:
 *
 *  - `init { require(...) }` invariants
 *  - non-null parameter checks (`Intrinsics.checkNotNullParameter`)
 *
 * With the default `ON_CONSTRUCTOR_ERROR=FALLBACK`, a constructor that rejects
 * the generated (or user-supplied) arguments is silently bypassed: the object
 * is allocated without a constructor and populated by direct field assignment.
 * The resulting object can therefore hold state that its own constructor would
 * have rejected.
 *
 * <p>These tests pin down the current behaviour. See the assertions/comments for
 * the cases where the outcome may be surprising to Kotlin users.
 */
@ExtendWith(InstancioExtension::class)
class KConstructorValidationBypassTest {

    // No default => ALL_ARGS => the init block runs against the *generated* value
    data class Age(val value: Int) {
        init {
            require(value in 0..150) { "invalid age: $value" }
        }
    }

    // Has default => NO_ARGS => the init block runs only against the *default* value
    data class AgeWithDefault(val value: Int = 1) {
        init {
            require(value in 0..150) { "invalid age: $value" }
        }
    }

    data class NonNull(val a: String, val b: String)

    @Nested
    inner class InitBlockValidation {

        @Test
        fun fallbackSilentlyBypassesInitValidation() {
            // 'value' is a random Int, almost always outside 0..150.
            // With FALLBACK (default) the failing constructor is bypassed and the
            // object is created with an invalid value that require() should reject.
            val results = Instancio.stream(Age::class.java)
                .limit(10)

            assertThat(results)
                .hasSize(10)
                .anyMatch { it.value !in 0..150 }
        }

        @Test
        fun failPropagatesInitValidationError() {
            val api = Instancio.of(Age::class.java)
                .withSetting(Keys.ON_CONSTRUCTOR_ERROR, OnConstructorError.FAIL)
                .set(field("value"), 999)

            assertThatThrownBy { api.create() }
                .isInstanceOf(InstancioApiException::class.java)
        }

        @Test
        fun allDefaultClassBypassesValidationEvenUnderFail(@Given expected: Int) {
            val result = Instancio.of(AgeWithDefault::class.java)
                .withSetting(Keys.ON_CONSTRUCTOR_ERROR, OnConstructorError.FAIL)
                .set(field("value"), expected)
                .create()

            assertThat(result.value).isEqualTo(expected)
        }
    }

    @Nested
    inner class NullSafety {

        @Test
        fun fallbackAllowsNullInNonNullProperty() {
            val result = Instancio.of(NonNull::class.java)
                .set(field("a"), null)
                .create()

            assertThat(result.a).isNull()
        }

        @Test
        fun failPropagatesNullCheckError() {
            val api = Instancio.of(NonNull::class.java)
                .withSetting(Keys.ON_CONSTRUCTOR_ERROR, OnConstructorError.FAIL)
                .set(field("a"), null)

            assertThatThrownBy { api.create() }
                .isInstanceOf(InstancioApiException::class.java)
        }
    }
}
