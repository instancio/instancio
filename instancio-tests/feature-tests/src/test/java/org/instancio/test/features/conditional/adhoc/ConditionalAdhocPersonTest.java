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
package org.instancio.test.features.conditional.adhoc;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.valueOf;

@FeatureTag(Feature.CONDITIONAL)
@ExtendWith(InstancioExtension.class)
class ConditionalAdhocPersonTest {

    @Test
    void whenCountryThenPhone() {
        final List<Address> result = Instancio.ofList(Address.class)
                .size(100)
                .generate(field(Address::getCountry), gen -> gen.oneOf("Canada", "Other"))
                .when(valueOf(field(Address::getCountry))
                        .satisfies("Canada"::equals)
                        .set(field(Phone::getCountryCode), "+1"))
                .create();

        for (Address address : result) {
            if (address.getCountry().equals("Canada")) {
                assertAllCountryCodesAreEqualTo(address, "+1");
            } else {
                assertThat(address.getCountry()).isEqualTo("Other");
                assertThat(address.getPhoneNumbers())
                        .extracting(Phone::getCountryCode)
                        .doesNotContain("+1");
            }
        }
    }

    @RepeatedTest(100)
    void whenCountryThenPhoneMultipleConditionals() {
        final int size = 100;
        final List<Address> results = Instancio.ofList(Address.class)
                .size(100)
                .generate(field(Address::getCountry), gen -> gen.oneOf("Canada", "USA", "UK", "Germany"))
                .when(valueOf(field(Address::getCountry))
                        .isIn("Canada", "USA")
                        .set(field(Phone::getCountryCode), "+1"))
                .when(valueOf(field(Address::getCountry))
                        .satisfies("UK"::equals)
                        .set(field(Phone::getCountryCode), "+44"))
                .when(valueOf(field(Address::getCountry))
                        .satisfies("Germany"::equals)
                        .set(field(Phone::getCountryCode), "+49"))
                .create();

        assertThat(results).hasSize(size).allSatisfy(result -> {
            final String country = result.getCountry();

            if (country.equals("Canada") || country.equals("USA")) {
                assertAllCountryCodesAreEqualTo(result, "+1");
            } else if (country.equals("UK")) {
                assertAllCountryCodesAreEqualTo(result, "+44");
            } else if (country.equals("Germany")) {
                assertAllCountryCodesAreEqualTo(result, "+49");
            } else {
                fail("Unexpected country: " + country);
            }
        });
    }

    @Test
    void whenCountryCodeThenNumber() {
        final String expected = "604-123-5678";
        final Address result = Instancio.of(Address.class)
                .generate(field(Phone::getCountryCode), gen -> gen.oneOf("+1", "+2"))
                .when(valueOf(field(Phone::getCountryCode))
                        .is("+1")
                        .set(field(Phone::getNumber), expected))
                .create();

        assertThat(result.getPhoneNumbers())
                .filteredOn(p -> p.getCountryCode().equals("+1"))
                .as("Should generate expected number")
                .allMatch(p -> p.getNumber().equals(expected));

        assertThat(result.getPhoneNumbers())
                .filteredOn(p -> !p.getCountryCode().equals("+1"))
                .as("Should generate random numbers")
                .noneMatch(p -> p.getNumber().equals(expected));
    }

    @Test
    void whenGenderThenName() {
        final String maleName = "Bob";
        final String femaleName = "Alice";

        final Person result = Instancio.of(Person.class)
                .when(valueOf(all(Gender.class))
                        .is(Gender.MALE)
                        .set(field(Person::getName), maleName))
                .when(valueOf(all(Gender.class))
                        .is(Gender.FEMALE)
                        .set(field(Person::getName), femaleName))
                .create();

        if (result.getGender() == Gender.MALE) {
            assertThat(result.getName()).isEqualTo(maleName);
        } else if (result.getGender() == Gender.FEMALE) {
            assertThat(result.getName()).isEqualTo(femaleName);
        } else {
            assertThat(result.getName())
                    .as("Should generate random name for gender: %s", result.getGender())
                    .isNotIn(maleName, femaleName);
        }
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void whenAgeThenName() {
        Person person = Instancio.of(Person.class)
                .generate(field(Person::getAge), gen -> gen.ints().range(0, 20))
                .when(valueOf(Person::getAge)
                        .satisfies((Integer age) -> age < 10)
                        .set(field(Person::getName), "Bobby"))
                .when(valueOf(Person::getAge)
                        .satisfies((Integer age) -> age >= 10)
                        .set(field(Person::getName), "Bob"))
                .create();


        if (person.getAge() < 10) {
            assertThat(person.getName()).isEqualTo("Bobby");
        } else {
            assertThat(person.getName()).isEqualTo("Bob");
        }
    }

    @Test
    void whenCountryThenMultipleActions() {
        final String canada = "Canada";

        final Address result = Instancio.of(Address.class)
                .generate(field(Address::getCountry), gen -> gen.oneOf(canada, "Other"))
                .when(valueOf(field(Address::getCountry))
                        .is(canada)
                        .set(field(Address::getCity), "Vancouver")
                        .set(field(Phone::getCountryCode), "+1")
                        .generate(field(Phone::getNumber), gen -> gen.string().digits().length(7)))
                .create();

        if (result.getCountry().equals(canada)) {
            assertThat(result.getCity()).isEqualTo("Vancouver");
            assertThat(result.getPhoneNumbers()).allSatisfy(phone -> {
                assertThat(phone.getCountryCode()).isEqualTo("+1");
                assertThat(phone.getNumber()).containsOnlyDigits().hasSize(7);
            });
        } else {
            assertThat(result.getCity()).isNotEqualTo("Vancouver");
        }
    }


    private static void assertAllCountryCodesAreEqualTo(final Address address, final String countryCode) {
        assertThat(address.getPhoneNumbers())
                .extracting(Phone::getCountryCode)
                .containsOnly(countryCode);
    }
}
