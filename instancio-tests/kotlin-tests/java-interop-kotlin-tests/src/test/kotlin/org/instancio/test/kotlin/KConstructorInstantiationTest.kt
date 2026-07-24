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
import org.instancio.Instancio
import org.instancio.Select.field
import org.instancio.TypeToken
import org.instancio.junit.InstancioExtension
import org.instancio.settings.InstantiationStrategies
import org.instancio.settings.InstantiationStrategy
import org.instancio.settings.Keys
import org.instancio.test.support.tags.Feature
import org.instancio.test.support.tags.FeatureTag
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Verifies constructor-based instantiation (`INSTANTIATION_STRATEGIES`) against
 * common Kotlin class shapes.
 *
 * <p>Kotlin classes do not carry the `MethodParameters` attribute unless compiled
 * with `-java-parameters`, so constructor parameter matching relies on the
 * `LocalVariableTable` (emitted by default). These tests exercise the value-passing
 * (`ALL_ARGS`) path for Kotlin classes.
 */
@FeatureTag(Feature.INSTANTIATION_STRATEGIES)
@ExtendWith(InstancioExtension::class)
class KConstructorInstantiationTest {

    // No default values => no synthetic no-arg constructor => created via ALL_ARGS
    data class NoDefaults(val id: String, val count: Int)

    // All parameters have defaults => Kotlin emits a synthetic no-arg constructor
    // => created via NO_ARGS, then populated by (final) field assignment
    data class AllDefaults(val id: String = "default", val count: Int = -1)

    // Mixed: some defaults => no no-arg constructor => ALL_ARGS
    data class MixedDefaults(val id: String, val count: Int = -1)

    data class MutableData(var id: String, var count: Int)

    class ValHolder(val id: String, val count: Int)

    data class Inner(val value: String)
    data class Outer(val inner: Inner, val name: String)

    // nullable primitive (Int?) maps to java.lang.Integer
    data class NullablePrimitive(val nonNull: Int, val nullable: Int?)

    data class WithCollections(val names: List<String>, val ages: Set<Int>)

    // property declared in the body is not a constructor parameter
    data class BodyProperty(val id: String) {
        var extra: Int = 0
    }

    data class Box<T>(val item: T, val label: String)

    @JvmInline
    value class Money(val amount: Long)
    data class HasValueClass(val money: Money, val label: String)

    @Nested
    inner class WorkingUseCases {

        @Test
        fun dataClassNoDefaults() {
            val result = Instancio.create(NoDefaults::class.java)
            assertThat(result.id).isNotBlank()
            assertThat(result.count).isNotZero()
        }

        @Test
        fun dataClassAllDefaults() {
            // Values must be generated, not left as the Kotlin defaults
            val result = Instancio.create(AllDefaults::class.java)
            assertThat(result.id).isNotBlank().isNotEqualTo("default")
            assertThat(result.count).isNotEqualTo(-1)
        }

        @Test
        fun dataClassMixedDefaults() {
            val result = Instancio.create(MixedDefaults::class.java)
            assertThat(result.id).isNotBlank()
            assertThat(result.count).isNotEqualTo(-1)
        }

        @Test
        fun mutableDataClass() {
            val result = Instancio.create(MutableData::class.java)
            assertThat(result.id).isNotBlank()
            assertThat(result.count).isNotZero()
        }

        @Test
        fun nonDataClassWithVal() {
            val result = Instancio.create(ValHolder::class.java)
            assertThat(result.id).isNotBlank()
            assertThat(result.count).isNotZero()
        }

        @Test
        fun nestedDataClasses() {
            val result = Instancio.create(Outer::class.java)
            assertThat(result.name).isNotBlank()
            assertThat(result.inner.value).isNotBlank()
        }

        @Test
        fun nullablePrimitive() {
            val result = Instancio.create(NullablePrimitive::class.java)
            assertThat(result.nonNull).isNotZero()
            assertThat(result.nullable).isNotNull()
        }

        @Test
        fun collections() {
            val result = Instancio.create(WithCollections::class.java)
            assertThat(result.names).isNotEmpty().allSatisfy { assertThat(it).isNotBlank() }
            assertThat(result.ages).isNotEmpty()
        }

        @Test
        fun bodyProperty() {
            // 'extra' is not a constructor parameter; it is populated after construction
            val result = Instancio.create(BodyProperty::class.java)
            assertThat(result.id).isNotBlank()
            assertThat(result.extra).isNotZero()
        }

        @Test
        fun genericDataClass() {
            val result = Instancio.create(object : TypeToken<Box<String>> {})
            assertThat(result.item).isNotBlank()
            assertThat(result.label).isNotBlank()
        }

        @Test
        fun valueClassParameter() {
            val result = Instancio.create(HasValueClass::class.java)
            assertThat(result.label).isNotBlank()
            assertThat(result.money.amount).isNotZero()
        }
    }

    @Nested
    inner class Selectors {

        @Test
        fun selectorOnAllArgsConstructorParameter() {
            // NoDefaults is created via ALL_ARGS: the value must be routed through the constructor
            val result = Instancio.of(NoDefaults::class.java)
                .set(field("id"), "custom-id")
                .create()

            assertThat(result.id).isEqualTo("custom-id")
        }

        @Test
        fun selectorOnNoArgsConstructorField() {
            // AllDefaults is created via NO_ARGS: the value is assigned to the (final) field
            val result = Instancio.of(AllDefaults::class.java)
                .set(field("id"), "custom-id")
                .create()

            assertThat(result.id).isEqualTo("custom-id")
        }

        @Test
        fun selectorOnNestedConstructorParameter() {
            val result = Instancio.of(Outer::class.java)
                .set(field(Inner::class.java, "value"), "nested")
                .create()

            assertThat(result.inner.value).isEqualTo("nested")
        }
    }

    @Nested
    inner class StrategyConfiguration {

        @Test
        fun allArgsCanInstantiateAllDefaultsClass() {
            // Even though AllDefaults has a no-arg constructor, ALL_ARGS can use the primary one
            val result = Instancio.of(AllDefaults::class.java)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, InstantiationStrategies.of(InstantiationStrategy.ALL_ARGS))
                .create()

            assertThat(result.id).isNotBlank().isNotEqualTo("default")
        }

        @Test
        fun noArgsOnlyCannotInstantiateNoDefaultsClass() {
            // NoDefaults has no no-arg constructor; with NO_ARGS only and no fallback
            // strategy (ALL_ARGS/BYPASS_CONSTRUCTOR), the class cannot be instantiated
            // and create() returns null.
            //
            // NOTE: the explicit nullable type is required: create() returns a platform type,
            // and without it Kotlin infers the non-null type and inserts a runtime
            // null check that fails with "create(...) must not be null".
            val result: NoDefaults? = Instancio.of(NoDefaults::class.java)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, InstantiationStrategies.of(InstantiationStrategy.NO_ARGS))
                .create()

            assertThat(result).isNull()
        }
    }
}
