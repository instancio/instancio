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
import org.instancio.Select
import org.instancio.Select.all
import org.instancio.Select.allStrings
import org.instancio.Select.root
import org.instancio.Select.types
import org.instancio.exception.InstancioApiException
import org.instancio.junit.InstancioExtension
import org.instancio.test.kotlin.pojo.person.KPerson
import org.instancio.test.kotlin.pojo.person.KPhone
import org.instancio.test.support.asserts.ReflectionAssert.assertThatObject
import org.instancio.test.support.pojo.person.Phone
import org.instancio.test.support.tags.Feature
import org.instancio.test.support.tags.FeatureTag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@FeatureTag(
    Feature.SELECTOR,
    Feature.PREDICATE_SELECTOR,
    Feature.ROOT_SELECTOR,
    Feature.SCOPE,
    Feature.TO_SCOPE
)
@ExtendWith(InstancioExtension::class)
internal class KSelectorsTest {

    @Test
    fun selectRoot() {
        val result = Instancio.of(String::class.java)
            .set(root(), "foo")
            .create()

        assertThat(result).isEqualTo("foo")
    }

    @Test
    fun typePredicate() {
        val expected = Instancio.create(KPhone::class.java)
        val result = Instancio.of(KPerson::class.java)
            .set(types().of(KPhone::class.java), expected)
            .create()

        assertThat(result.address?.phoneNumbers)
            .isNotEmpty()
            .containsOnly(expected)
    }

    /**
     * Kotlin property references are not compatible with Java's [GetMethodSelector],
     * which relies on bytecode inspection of serializable lambda to extract method names.
     */
    @Test
    fun javaGetMethodSelectorDoesNotSupportKotlinPropertyReferences() {
        assertThatThrownBy {
            Instancio.of(KPhone::class.java)
                .set(Select.field(KPhone::number), "foo")
        }.isExactlyInstanceOf(InstancioApiException::class.java)
            .hasMessageContainingAll(
                "Unable to resolve the field from method reference",
                "You are using Kotlin and passing a method reference of a Kotlin class"
            )
    }

    @Test
    fun methodReferenceFromJavaClass() {
        val result = Instancio.of(Phone::class.java)
            .set(Select.field(Phone::getNumber), "foo")
            .create();

        assertThat(result.number).isEqualTo("foo");
    }

    @Test
    fun withinScope() {
        val result = Instancio.of(KPerson::class.java)
            .set(allStrings().within(all(KPhone::class.java).toScope()), "foo")
            .create()

        assertThat(result.address?.city).isNotEqualTo("foo")
        assertThat(result.address?.phoneNumbers).isNotEmpty

        result.address?.phoneNumbers?.forEach {
            assertThatObject(it).hasAllFieldsOfTypeEqualTo(String::class.java, "foo")
        }
    }
}