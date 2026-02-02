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
import org.instancio.Random
import org.instancio.Select.*
import org.instancio.generators.Generators
import org.instancio.junit.InstancioExtension
import org.instancio.test.kotlin.pojo.person.KAddress
import org.instancio.test.kotlin.pojo.person.KPerson
import org.instancio.test.kotlin.pojo.person.KPhone
import org.instancio.test.support.asserts.Asserts.assertAllNulls
import org.instancio.test.support.asserts.ReflectionAssert.assertThatObject
import org.instancio.test.support.tags.Feature
import org.instancio.test.support.tags.FeatureTag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@FeatureTag(
    Feature.IGNORE,
    Feature.GENERATE,
    Feature.ON_COMPLETE,
    Feature.SET,
    Feature.SUPPLY
)
@ExtendWith(InstancioExtension::class)
internal class KPersonTest {

    @Test
    fun create() {
        val result = Instancio.create(KPerson::class.java)
        assertThatObject(result).isFullyPopulated
    }

    @Test
    fun createArray() {
        val results: Array<KPerson> = Instancio.create(Array<KPerson>::class.java)
        assertThat(results).isNotEmpty
            .allSatisfy { result: KPerson? -> assertThatObject(result).isFullyPopulated }
    }

    @Test
    fun customiseFields() {
        val result = Instancio.of(KPerson::class.java)
            .generate(field(KPhone::class.java, "number")) { gen: Generators ->
                gen.string().digits().length(7)
            }
            .create();

        assertThat(result.address?.phoneNumbers).isNotEmpty

        result.address?.phoneNumbers?.forEach {
            assertThat(it.number).hasSize(7).containsOnlyDigits()
        }
    }

    @Test
    fun ignore() {
        val result = Instancio.of(KPerson::class.java)
            .ignore(all(allStrings(), field("age")))
            .ignore(fields().named("phoneNumbers").declaredIn(KAddress::class.java))
            .create()

        assertThat(result.age).isZero
        assertThat(result.address).isNotNull

        val address = result.address!!
        assertAllNulls(
            result.name, address.city, address.address, address.phoneNumbers
        )
    }

    @Test
    fun supplyWholeClass() {
        val city = "some-city"
        val streetPrefix = "street-"
        val callbackCount = intArrayOf(0)

        val result = Instancio.of(KPerson::class.java)
            .supply(field("address")) { rnd: Random ->
                KAddress(address = streetPrefix + rnd.digits(3), city, phoneNumbers = listOf())
            }
            .onComplete(all(KAddress::class.java)) { _: KAddress -> callbackCount[0]++ }
            .create()

        assertThat(result.address?.city).isEqualTo(city)
        assertThat(result.address?.address).startsWith(streetPrefix).hasSizeGreaterThan(streetPrefix.length)
        assertThat(callbackCount[0]).isOne
    }

    @Test
    fun verifyCallbackIsCalled() {
        val callbackCount = intArrayOf(0)

        Instancio.of(KPerson::class.java)
            .set(field(KAddress::class.java, "city"), "foo")
            .onComplete(all(KAddress::class.java)) { address: KAddress ->
                assertThatObject(address).isFullyPopulated
                assertThat(address.city).isEqualTo("foo")
                callbackCount[0]++
            }
            .create()

        assertThat(callbackCount[0]).isOne
    }

    @Test
    fun setNonNullableToNull() {
        // Note: setting null would not be possible creating an object manually
        // since UUID is not declared as `UUID?`
        val result: KPerson = Instancio.of(KPerson::class.java)
            .set(field("uuid"), null)
            .create()

        assertThat(result.uuid).isNull()
    }
}
