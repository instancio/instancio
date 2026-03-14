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

import java.lang.reflect.Field
import java.util.function.Predicate
import kotlin.reflect.KProperty1
import org.assertj.core.api.Assertions.assertThat
import org.instancio.junit.Given
import org.instancio.junit.InstancioExtension
import org.instancio.kotlin.KInstancio
import org.instancio.kotlin.KSelect.all
import org.instancio.kotlin.KSelect.allBooleans
import org.instancio.kotlin.KSelect.allBytes
import org.instancio.kotlin.KSelect.allChars
import org.instancio.kotlin.KSelect.allDoubles
import org.instancio.kotlin.KSelect.allFloats
import org.instancio.kotlin.KSelect.allInts
import org.instancio.kotlin.KSelect.allLongs
import org.instancio.kotlin.KSelect.allShorts
import org.instancio.kotlin.KSelect.allStrings
import org.instancio.kotlin.KSelect.field
import org.instancio.kotlin.KSelect.fields
import org.instancio.kotlin.KSelect.root
import org.instancio.kotlin.KSelect.scope
import org.instancio.kotlin.KSelect.setter
import org.instancio.kotlin.KSelect.types
import org.instancio.settings.AssignmentType
import org.instancio.settings.Keys
import org.instancio.settings.Settings
import org.instancio.test.kotlin.pojo.person.KPerson
import org.instancio.test.kotlin.pojo.person.KPhone
import org.instancio.test.kotlin.pojo.person.basic.KIntegerHolder
import org.instancio.test.kotlin.pojo.person.basic.KStringHolder
import org.instancio.test.kotlin.pojo.person.basic.KSupportedNumericTypes
import org.instancio.test.support.pojo.basic.StringHolder
import org.instancio.test.support.pojo.person.Address
import org.instancio.test.support.pojo.person.Person
import org.instancio.test.support.pojo.person.Phone
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstancioExtension::class)
class KSelectTest {

    @Test
    fun allSelector(@Given value: String) {
        val result = KInstancio.of<Phone>()
            .set(all<String>(), value)
            .create()

        assertThat(result.number)
            .isEqualTo(result.countryCode)
            .isEqualTo(value)
    }

    @Test
    fun allNumericSelectors(
        @Given byteValue: Byte,
        @Given shortValue: Short,
        @Given intValue: Int,
        @Given longValue: Long,
        @Given floatValue: Float,
        @Given doubleValue: Double,
    ) {
        val result = KInstancio.of<KSupportedNumericTypes>()
            .set(allBytes(), byteValue)
            .set(allShorts(), shortValue)
            .set(allInts(), intValue)
            .set(allLongs(), longValue)
            .set(allFloats(), floatValue)
            .set(allDoubles(), doubleValue)
            .create()

        assertThat(result.byteValue).isEqualTo(byteValue)
        assertThat(result.shortValue).isEqualTo(shortValue)
        assertThat(result.intValue).isEqualTo(intValue)
        assertThat(result.longValue).isEqualTo(longValue)
        assertThat(result.floatValue).isEqualTo(floatValue)
        assertThat(result.doubleValue).isEqualTo(doubleValue)
    }

    @Test
    fun allBooleansSelector(@Given value: Boolean) {
        val result = KInstancio.of<Boolean>()
            .set(allBooleans(), value)
            .create()

        assertThat(result).isEqualTo(value)
    }

    @Test
    fun allCharsSelector(@Given value: Char) {
        val result = KInstancio.of<Char>()
            .set(allChars(), value)
            .create()

        assertThat(result).isEqualTo(value)
    }

    @Test
    fun allStringsSelector(@Given value: String) {
        val result = KInstancio.of<StringHolder>()
            .set(allStrings(), value)
            .create()

        assertThat(result.value).isEqualTo(value)
    }

    @Test
    fun allIntsSelector(@Given value: Int) {
        val result = KInstancio.of<KIntegerHolder>()
            .set(allInts(), value)
            .create()

        assertThat(result.nullable)
            .isEqualTo(result.nonNull)
            .isEqualTo(value)
    }

    @Test
    fun allGroupSelector(@Given value: String) {
        val selector = all(
            field(KPhone::countryCode),
            field(KPhone::number)
        )

        val result = KInstancio.of<KPhone>()
            .set(selector, value)
            .create()

        assertThat(result.countryCode).isEqualTo(value)
        assertThat(result.number).isEqualTo(value)
    }

    @Test
    fun fieldsBuilderSelector(@Given value: String) {
        val result = KInstancio.of<Phone>()
            .set(fields().named("number"), value)
            .create()

        assertThat(result.number).isEqualTo(value)
    }

    @Test
    fun fieldsWithJavaPredicate(@Given value: String) {
        val predicate: Predicate<Field> = Predicate { it.name == "number" }

        val result = KInstancio.of<Phone>()
            .set(fields(predicate), value)
            .create()

        assertThat(result.number).isEqualTo(value)
    }

    @Test
    fun fieldsWithKotlinPredicate(@Given value: String) {
        val predicate: (Field) -> Boolean = { it.name == "number" }

        val result = KInstancio.of<Phone>()
            .set(fields(predicate), value)
            .create()

        assertThat(result.number).isEqualTo(value)
    }

    @Test
    fun typesBuilderSelector(@Given value: String) {
        val result = KInstancio.of<Phone>()
            .set(types().of(String::class.java), value)
            .create()

        assertThat(result.number).isEqualTo(result.countryCode).isEqualTo(value)
    }

    @Test
    fun typesWithJavaPredicate(@Given value: String) {
        val predicate: Predicate<Class<*>> = Predicate { it == String::class.java }

        val result = KInstancio.of<Phone>()
            .set(types(predicate), value)
            .create()

        assertThat(result.number).isEqualTo(result.countryCode).isEqualTo(value)
    }

    @Test
    fun typesWithKotlinPredicate(@Given value: String) {
        val predicate: (Class<*>) -> Boolean = { it == String::class.java }

        val result = KInstancio.of<Phone>()
            .set(types(predicate), value)
            .create()

        assertThat(result.number).isEqualTo(result.countryCode).isEqualTo(value)
    }

    @Test
    fun rootSelector(@Given value: String) {
        val result = KInstancio.of<String>()
            .set(root(), value)
            .create()

        assertThat(result).isEqualTo(value)
    }

    @Test
    fun fieldByName(@Given value: String) {
        val result = KInstancio.of<Phone>()
            .set(field("number"), value)
            .create()

        assertThat(result.number).isEqualTo(value)
    }

    @Test
    fun fieldByNameWithReifiedType(@Given value: String) {
        val result = KInstancio.of<Person>()
            .set(field<Phone>("number"), value)
            .create()

        assertThat(result.address.phoneNumbers).allMatch { it.number == value }
    }

    @Test
    fun fieldByGetMethodSelector(@Given value: String) {
        val result = KInstancio.of<StringHolder>()
            // NOTE: `StringHolder::getValue` only works inline
            .set(field(StringHolder::getValue), value)
            .create()

        assertThat(result.value).isEqualTo(value)
    }

    @Test
    fun fieldByKProperty(@Given value: String) {
        val property: KProperty1<KStringHolder, String?> = KStringHolder::value

        val result = KInstancio.of<KStringHolder>()
            .set(field(property), value)
            .create()

        assertThat(result.value).isEqualTo(value)
    }

    @Test
    fun setterByName(@Given value: String) {
        val result = KInstancio.of<Phone>()
            .withSettings(Settings.create().set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD))
            .set(setter("setNumber"), value)
            .create()

        assertThat(result.number).isEqualTo(value)
    }

    @Test
    fun setterByNameWithReifiedType(@Given value: String) {
        val result = KInstancio.of<Person>()
            .withSettings(Settings.create().set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD))
            .set(setter<Phone>("setNumber"), value)
            .create()

        assertThat(result.address.phoneNumbers).allMatch { it.number == value }
    }

    @Test
    fun setterByNameWithReifiedTypeAndParamType(@Given value: String) {
        val result = KInstancio.of<Phone>()
            .withSettings(Settings.create().set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD))
            .set(setter<Phone, String>("setNumber"), value)
            .create()

        assertThat(result.number).isEqualTo(value)
    }

    @Test
    fun setterByMethodReferenceSelector(@Given value: String) {
        val result = KInstancio.of<Phone>()
            .withSettings(Settings.create().set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD))
            .set(setter(Phone::setNumber), value)
            .create()

        assertThat(result.number).isEqualTo(value)
    }

    @Test
    fun scopeByClass(@Given value: String) {
        val result = KInstancio.of<Person>()
            .set(all<String>().within(scope<Address>()), value)
            .create()

        assertThat(result.address.city).isEqualTo(value)
        assertThat(result.address.street).isEqualTo(value)
        assertThat(result.address.country).isEqualTo(value)
    }

    @Test
    fun scopeByClassAndFieldName(@Given value: String) {
        val result = KInstancio.of<Person>()
            .set(all<String>().within(scope<Person>("address")), value)
            .create()

        assertThat(result.address.city).isEqualTo(value)
        assertThat(result.address.street).isEqualTo(value)
    }

    @Test
    fun scopeByGetMethodSelector(@Given value: String) {
        val result = KInstancio.of<Person>()
            .set(all<String>().within(scope(Person::getAddress)), value)
            .create()

        assertThat(result.address.city).isEqualTo(value)
        assertThat(result.address.street).isEqualTo(value)
    }

    @Test
    fun scopeByPredicateSelector(@Given value: String) {
        val predicate: Predicate<Class<*>> = Predicate { it == Address::class.java }
        val scope = scope(types(predicate))

        val result = KInstancio.of<Person>()
            .set(all<String>().within(scope), value)
            .create()

        assertThat(result.address.city).isEqualTo(value)
        assertThat(result.address.street).isEqualTo(value)
    }

    @Test
    fun scopeByKProperty(@Given value: String) {
        val result = KInstancio.of<KPerson>()
            .set(all<String>().within(scope(KPerson::address)), value)
            .create()

        assertThat(result.address?.city).isEqualTo(value)
        assertThat(result.address?.country).isEqualTo(value)
    }
}
