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
import org.instancio.junit.InstancioExtension
import org.instancio.test.kotlin.pojo.person.KPerson
import org.instancio.test.kotlin.pojo.person.KPhone
import org.instancio.test.support.asserts.ReflectionAssert.assertThatObject
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
        val result = Instancio.of(KPerson::class.java)
            .set(root(), null)
            .create()

        assertThat(result).isNull()
    }

    @Test
    fun typePredicate() {
        val expected = Instancio.create(KPhone::class.java)
        val result = Instancio.of(KPerson::class.java)
            .set(types().of(KPhone::class.java), expected)
            .create()

        assertThat(result.address?.phoneNumbers)
            .isNotEmpty.containsOnly(expected)
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