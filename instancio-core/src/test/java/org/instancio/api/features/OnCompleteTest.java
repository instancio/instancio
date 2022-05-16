/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.api.features;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.TypeToken;
import org.instancio.test.support.pojo.arrays.ArrayPerson;
import org.instancio.test.support.pojo.collections.lists.ListPerson;
import org.instancio.test.support.pojo.collections.maps.MapStringPerson;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.PersonHolder;
import org.instancio.test.support.pojo.person.Pet;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.RichPerson;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;
import static org.junit.jupiter.api.Assertions.fail;

class OnCompleteTest {
    private static final String COUNTRY_CODE = "+1";
    private static final String HOMER = "Homer";
    private static final String SNOWBALL = "Snowball";

    @Test
    void usingCreateClass() {
        final Person result = Instancio.of(Person.class)
                .onComplete(all(Phone.class), (Phone phone) -> phone.setCountryCode(COUNTRY_CODE))
                .onComplete(all(Person.class), (Person person) -> person.setName(HOMER))
                .onComplete(all(Pet.class), (Pet pet) -> pet.setName(SNOWBALL))
                .create();

        assertPerson(result);
    }

    @Test
    void usingModel() {
        final Model<Person> model = Instancio.of(Person.class)
                .onComplete(all(Phone.class), (Phone phone) -> phone.setCountryCode(COUNTRY_CODE))
                .onComplete(all(Person.class), (Person person) -> person.setName(HOMER))
                .onComplete(all(Pet.class), (Pet pet) -> pet.setName(SNOWBALL))
                .toModel();

        assertPerson(Instancio.create(model));
    }

    @Test
    void withMapKKey() {
        final Map<Person, String> result = Instancio.of(new TypeToken<Map<Person, String>>() {})
                .onComplete(all(Phone.class), (Phone phone) -> phone.setCountryCode(COUNTRY_CODE))
                .onComplete(all(Person.class), (Person person) -> person.setName(HOMER))
                .onComplete(all(Pet.class), (Pet pet) -> pet.setName(SNOWBALL))
                .create();

        assertThat(result.keySet())
                .hasSizeGreaterThanOrEqualTo(Constants.MIN_SIZE)
                .allSatisfy(OnCompleteTest::assertPerson);
    }

    @Test
    void withMapValue() {
        final MapStringPerson result = Instancio.of(MapStringPerson.class)
                .onComplete(all(Phone.class), (Phone phone) -> phone.setCountryCode(COUNTRY_CODE))
                .onComplete(all(Person.class), (Person person) -> person.setName(HOMER))
                .onComplete(all(Pet.class), (Pet pet) -> pet.setName(SNOWBALL))
                .create();

        assertThat(result.getMap().values())
                .hasSizeGreaterThanOrEqualTo(Constants.MIN_SIZE)
                .allSatisfy(OnCompleteTest::assertPerson);
    }

    @Test
    void withListElement() {
        final ListPerson result = Instancio.of(ListPerson.class)
                .onComplete(all(Phone.class), (Phone phone) -> phone.setCountryCode(COUNTRY_CODE))
                .onComplete(all(Person.class), (Person person) -> person.setName(HOMER))
                .onComplete(all(Pet.class), (Pet pet) -> pet.setName(SNOWBALL))
                .create();

        assertThat(result.getList())
                .hasSizeGreaterThanOrEqualTo(Constants.MIN_SIZE)
                .allSatisfy(OnCompleteTest::assertPerson);
    }

    @Test
    void withArrayElement() {
        final ArrayPerson result = Instancio.of(ArrayPerson.class)
                .onComplete(all(Phone.class), (Phone phone) -> phone.setCountryCode(COUNTRY_CODE))
                .onComplete(all(Person.class), (Person person) -> person.setName(HOMER))
                .onComplete(all(Pet.class), (Pet pet) -> pet.setName(SNOWBALL))
                .create();

        assertThat(result.getArray())
                .hasSizeGreaterThanOrEqualTo(Constants.MIN_SIZE)
                .allSatisfy(OnCompleteTest::assertPerson);
    }

    @Test
    void onCompleteFieldForIgnoredClass() {
        final Person result = Instancio.of(Person.class)
                .ignore(all(Address.class))
                .onComplete(all(Phone.class), (Phone phone) -> failIfCalled())
                .create();

        assertThat(result.getAddress()).isNull();
    }

    @Test
    void onCompleteFieldForIgnoredField() {
        final Person result = Instancio.of(Person.class)
                .ignore(field("name"))
                .onComplete(field("name"), (String name) -> failIfCalled())
                .create();

        assertThat(result.getName()).isNull();
    }

    @Test
    void onCompleteForNullableClass() {
        final Set<Address> result = Instancio.of(Person.class)
                .ignore(all(Address.class))
                .onComplete(field(Address.class, "city"), (String city) -> failIfCalled())
                .stream()
                .map(Person::getAddress)
                .limit(100)
                .collect(toSet());

        assertThat(result).hasSize(1).containsOnlyNulls();
    }

    @Test
    void onCompleteWithScope() {
        final PersonHolder result = Instancio.of(PersonHolder.class)
                .onComplete(all(Phone.class).within(
                                scope(RichPerson.class, "address1")),
                        (Phone p) -> p.setNumber("foo"))
                .create();

        assertThat(result.getRichPerson().getAddress1().getPhoneNumbers()).extracting(Phone::getNumber).containsOnly("foo");
        assertThat(result.getRichPerson().getAddress2().getPhoneNumbers()).extracting(Phone::getNumber).doesNotContain("foo");
    }

    private static void assertPerson(final Person result) {
        assertThat(result.getAddress().getPhoneNumbers()).allSatisfy(
                phone -> assertThat(phone.getCountryCode()).isEqualTo(COUNTRY_CODE));

        assertThat(result.getPets()).allSatisfy(
                pet -> assertThat(pet.getName()).isEqualTo(SNOWBALL));

        assertThat(result.getName()).isEqualTo(HOMER);
    }

    private static void failIfCalled() {
        fail("Should not be called");
    }
}