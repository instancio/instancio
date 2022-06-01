/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.api.features;

import org.instancio.Instancio;
import org.instancio.Mode;
import org.instancio.TypeToken;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.arrays.TwoArraysOfItemString;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.generics.container.ItemContainer;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.PersonHolder;
import org.instancio.test.support.pojo.person.Pet;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.RichPerson;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;

@FeatureTag({Feature.SELECTOR, Feature.SET})
class SetWithScopeTest {

    private static final String FOO = "foo";

    @Nested
    @DisplayName("In case of overlaps, last selector takes precedence")
    class LastSelectorWinsTest {
        @Test
        void selectAllIntsAndAge1() {
            final PersonHolder result = Instancio.of(PersonHolder.class)
                    .set(allInts(), -1)
                    .set(allInts().within(scope(Person.class)), 100)
                    .create();

            assertThat(result.getPerson().getAge()).isEqualTo(100);
            assertThat(result.getRichPerson().getAge()).isEqualTo(-1);
        }

        @Test
        void selectAllIntsAndAge2() {
            final PersonHolder result = Instancio.of(PersonHolder.class)
                    .lenient()
                    .set(allInts().within(scope(Person.class)), 100)
                    .set(allInts(), -1)
                    .set(allInts().within(scope(RichPerson.class)), 100)
                    .create();

            assertThat(result.getPerson().getAge()).isEqualTo(-1);
            assertThat(result.getRichPerson().getAge()).isEqualTo(100);
        }
    }

    @Test
    void selectExactField() {
        final PersonHolder result = Instancio.of(PersonHolder.class)
                .set(allInts().within(scope(Person.class, "age")), 100)
                .create();

        assertThat(result.getPerson().getAge()).isEqualTo(100);
        assertThat(result.getRichPerson().getAge()).isNotEqualTo(100);
    }

    @Test
    void selectPhonesContainedByList() {
        final PersonHolder result = Instancio.of(PersonHolder.class)
                .set(field(Phone.class, "number").within(scope(List.class)), FOO)
                .create();

        assertThat(result.getRichPerson().getPhone().getNumber()).isNotEqualTo(FOO);
        assertThat(result.getRichPerson().getAddress1().getPhoneNumbers()).extracting(Phone::getNumber).containsOnly(FOO);
        assertThat(result.getRichPerson().getAddress2().getPhoneNumbers()).extracting(Phone::getNumber).containsOnly(FOO);
        assertThat(result.getPerson().getAddress().getPhoneNumbers()).extracting(Phone::getNumber).containsOnly(FOO);
    }

    @Test
    void selectInArray() {
        final TwoArraysOfItemString result = Instancio.of(TwoArraysOfItemString.class)
                .set(allStrings().within(scope(TwoArraysOfItemString.class, "array2")), FOO)
                .create();

        assertThat(result.getArray1()).extracting(Item::getValue).doesNotContain(FOO);
        assertThat(result.getArray2()).extracting(Item::getValue).containsOnly(FOO);
    }

    @Test
    void selectPhonesContainedByAddress1WithinMapValue() {
        final int minSize = 10;
        final Map<Person, RichPerson> result = Instancio.of(new TypeToken<Map<Person, RichPerson>>() {})
                .withSettings(Settings.create().set(Keys.MAP_MIN_SIZE, minSize))
                .set(field(Phone.class, "number").within(scope(RichPerson.class, "address1")), FOO)
                .create();

        assertThat(result.entrySet()).hasSizeGreaterThanOrEqualTo(minSize).allSatisfy(entry -> {
            assertThat(entry.getKey().getAddress().getPhoneNumbers()).extracting(Phone::getNumber).doesNotContain(FOO);
            assertThat(entry.getValue().getAddress1().getPhoneNumbers()).extracting(Phone::getNumber).containsOnly(FOO);
            assertThat(entry.getValue().getAddress2().getPhoneNumbers()).extracting(Phone::getNumber).doesNotContain(FOO);
            assertThat(entry.getValue().getPhone().getNumber()).doesNotContain(FOO);
        });
    }

    @Test
    void selectPhonesContainedByAddress1WithinMapKey() {
        final int minSize = 10;
        final Map<RichPerson, Person> result = Instancio.of(new TypeToken<Map<RichPerson, Person>>() {})
                .withSettings(Settings.create().set(Keys.MAP_MIN_SIZE, minSize))
                .set(field(Phone.class, "number").within(scope(RichPerson.class, "address1")), FOO)
                .create();

        assertThat(result.keySet()).hasSizeGreaterThanOrEqualTo(minSize).allSatisfy(key -> {
            assertThat(key.getAddress2().getPhoneNumbers()).extracting(Phone::getNumber).doesNotContain(FOO);
            assertThat(key.getAddress1().getPhoneNumbers()).extracting(Phone::getNumber).containsOnly(FOO);
            assertThat(key.getAddress2().getPhoneNumbers()).extracting(Phone::getNumber).doesNotContain(FOO);
            assertThat(key.getPhone().getNumber()).doesNotContain(FOO);
        });
    }

    @Test
    void selectPhoneDeclaredByRichPerson() {
        final PersonHolder result = Instancio.of(PersonHolder.class)
                .set(field(Phone.class, "number").within(scope(RichPerson.class, "phone")), FOO)
                .create();

        assertThat(result.getRichPerson().getPhone().getNumber()).isEqualTo(FOO);
        assertThat(result.getRichPerson().getAddress1().getPhoneNumbers()).extracting(Phone::getNumber).doesNotContain(FOO);
        assertThat(result.getRichPerson().getAddress2().getPhoneNumbers()).extracting(Phone::getNumber).doesNotContain(FOO);
        assertThat(result.getPerson().getAddress().getPhoneNumbers()).extracting(Phone::getNumber).doesNotContain(FOO);
    }

    @Test
    void selectPhoneWithinRichPerson() {
        final PersonHolder result = Instancio.of(PersonHolder.class)
                .set(field(Phone.class, "number").within(scope(RichPerson.class)), FOO)
                .create();

        assertThat(result.getRichPerson().getAddress1().getPhoneNumbers()).extracting(Phone::getNumber).containsOnly(FOO);
        assertThat(result.getRichPerson().getAddress2().getPhoneNumbers()).extracting(Phone::getNumber).containsOnly(FOO);
        assertThat(result.getRichPerson().getPhone().getNumber()).isEqualTo(FOO);
        assertThat(result.getPerson().getAddress().getPhoneNumbers()).extracting(Phone::getNumber).doesNotContain(FOO);
    }

    @Test
    void setCityWithinAddress1() {
        final RichPerson result = Instancio.of(RichPerson.class)
                .set(field(Address.class, "city").within(scope(RichPerson.class, "address1")), FOO)
                .create();

        assertThat(result.getAddress1().getCity()).isEqualTo(FOO);
        assertThat(result.getAddress2().getCity()).isNotEqualTo(FOO);
    }

    @Test
    void setPhoneWithinAddress1() {
        final PersonHolder result = Instancio.of(PersonHolder.class)
                .set(field(Phone.class, "number").within(scope(RichPerson.class, "address1")), FOO)
                .create();

        assertThat(result.getRichPerson().getAddress1().getPhoneNumbers()).extracting(Phone::getNumber).containsOnly(FOO);
        assertThat(result.getRichPerson().getAddress2().getPhoneNumbers()).extracting(Phone::getNumber).doesNotContain(FOO);
        assertThat(result.getPerson().getAddress().getPhoneNumbers()).extracting(Phone::getNumber).doesNotContain(FOO);
    }

    @Test
    void generateListWithinAddress1() {
        final PersonHolder result = Instancio.of(PersonHolder.class)
                .set(all(List.class).within(scope(RichPerson.class, "address1")), Collections.emptyList())
                .create();

        assertThat(result.getRichPerson().getAddress1().getPhoneNumbers()).isEmpty();
        assertThat(result.getRichPerson().getAddress2().getPhoneNumbers()).isNotEmpty();
        assertThat(result.getPerson().getAddress().getPhoneNumbers()).isNotEmpty();
    }

    @Test
    void selectAllPhonesOfRichPersonAsTypeArgument() {
        final ItemContainer<Person, RichPerson> result = Instancio.of(new TypeToken<ItemContainer<Person, RichPerson>>() {})
                .set(field(Phone.class, "number").within(scope(RichPerson.class)), FOO)
                .create();

        final RichPerson richPerson = result.getItemValueY().getValue();
        assertThat(richPerson.getPhone().getNumber()).isEqualTo(FOO);
        assertThat(richPerson.getAddress1().getPhoneNumbers()).extracting(Phone::getNumber).containsOnly(FOO);
        assertThat(richPerson.getAddress2().getPhoneNumbers()).extracting(Phone::getNumber).containsOnly(FOO);
        assertThat(result.getItemValueX().getValue().getAddress().getPhoneNumbers()).extracting(Phone::getNumber).doesNotContain(FOO);
    }

    @Test
    void selectAddress1PhoneOfRichPersonAndPersonAsTypeArgument() {
        final ItemContainer<Person, RichPerson> result = Instancio.of(new TypeToken<ItemContainer<Person, RichPerson>>() {})
                .set(field(Phone.class, "number").within(scope(RichPerson.class, "address1")), FOO)
                .set(field(Phone.class, "number").within(scope(Person.class)), FOO)
                .create();

        final RichPerson richPerson = result.getItemValueY().getValue();
        assertThat(richPerson.getPhone().getNumber()).isNotEqualTo(FOO);
        assertThat(richPerson.getAddress1().getPhoneNumbers()).extracting(Phone::getNumber).containsOnly(FOO);
        assertThat(richPerson.getAddress2().getPhoneNumbers()).extracting(Phone::getNumber).doesNotContain(FOO);
        assertThat(result.getItemValueX().getValue().getAddress().getPhoneNumbers()).extracting(Phone::getNumber).containsOnly(FOO);
    }

    @Test
    void selectorGroupMultipleTargets() {
        final PersonHolder result = Instancio.of(PersonHolder.class)
                .set(all(
                        field(Address.class, "city").within(scope(RichPerson.class)),
                        field(Address.class, "country").within(scope(RichPerson.class))
                ), FOO)
                .set(all(
                        field(Address.class, "city").within(scope(Person.class)),
                        field(Address.class, "country").within(scope(Person.class))
                ), "bar")

                .create();

        assertThat(result.getRichPerson().getAddress1().getCity()).isEqualTo(FOO);
        assertThat(result.getRichPerson().getAddress2().getCity()).isEqualTo(FOO);
        assertThat(result.getRichPerson().getAddress1().getCountry()).isEqualTo(FOO);
        assertThat(result.getRichPerson().getAddress2().getCountry()).isEqualTo(FOO);
        assertThat(result.getRichPerson().getName()).isNotEqualTo(FOO);

        assertThat(result.getPerson().getAddress().getCity()).isEqualTo("bar");
        assertThat(result.getPerson().getAddress().getCountry()).isEqualTo("bar");
    }

    @Test
    void selectorGroupMultipleTargetsWithDifferentTypes() {
        final PersonHolder result = Instancio.of(PersonHolder.class)
                .set(all(
                                field(Address.class, "city").within(scope(PersonHolder.class), scope(Person.class)),
                                all(LocalDateTime.class).within(scope(PersonHolder.class), scope(Person.class)),
                                allInts().within(scope(PersonHolder.class), scope(Person.class)))
                        , null)
                .create();

        assertThat(result.getRichPerson().getAddress1().getCity()).isNotNull();
        assertThat(result.getRichPerson().getAddress2().getCity()).isNotNull();
        assertThat(result.getRichPerson().getAge()).isNotNull();
        assertThat(result.getPerson().getAge()).isZero();
        assertThat(result.getPerson().getLastModified()).isNull();
    }

    @Test
    void multiLevelScope() {
        final PersonHolder result = Instancio.of(PersonHolder.class)
                .set(field(Phone.class, "number")
                        .within(scope(PersonHolder.class, "richPerson"),
                                scope(RichPerson.class, "address1")
                        ), FOO)
                .create();

        assertThat(result.getRichPerson().getPhone().getNumber()).isNotEqualTo(FOO);
        assertThat(result.getRichPerson().getAddress1().getPhoneNumbers()).extracting(Phone::getNumber).containsOnly(FOO);
        assertThat(result.getRichPerson().getAddress2().getPhoneNumbers()).extracting(Phone::getNumber).doesNotContain(FOO);
        assertThat(result.getPerson().getAddress().getPhoneNumbers()).extracting(Phone::getNumber).doesNotContain(FOO);
    }

    @Test
    void multiLevelScopeAllStrings() {
        final PersonHolder result = Instancio.of(PersonHolder.class)
                .set(allStrings().within(
                        scope(RichPerson.class),
                        scope(RichPerson.class, "address1"),
                        scope(Phone.class)
                ), FOO)
                .create();

        assertThat(result.getRichPerson().getPhone().getNumber()).isNotEqualTo(FOO);
        assertThat(result.getRichPerson().getAddress1().getPhoneNumbers()).extracting(Phone::getNumber).containsOnly(FOO);
        assertThat(result.getRichPerson().getAddress1().getPhoneNumbers()).extracting(Phone::getCountryCode).containsOnly(FOO);
        assertThat(result.getRichPerson().getAddress2().getPhoneNumbers()).extracting(Phone::getNumber).doesNotContain(FOO);
        assertThat(result.getPerson().getAddress().getPhoneNumbers()).extracting(Phone::getNumber).doesNotContain(FOO);
    }

    @Test
    void selectAllStringsWithinArray() {
        final PersonHolder result = Instancio.of(PersonHolder.class)
                .set(allStrings().within(scope(Pet[].class)), FOO)
                .create();

        assertThat(result.getPerson().getPets()).extracting(Pet::getName).containsOnly(FOO);
    }

    @Test
    void selectFieldWithinArray() {
        final PersonHolder result = Instancio.of(PersonHolder.class)
                .set(field(Pet.class, "name").within(scope(Pet[].class)), FOO)
                .create();

        assertThat(result.getPerson().getPets()).extracting(Pet::getName).containsOnly(FOO);
    }
}
