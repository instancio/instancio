/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.test.features.assign.adhoc;

import org.instancio.Assignment;
import org.instancio.Instancio;
import org.instancio.When;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Pet;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.instancio.Assign.given;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.When.is;
import static org.instancio.When.isIn;

@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignAdhocPersonTest {

    private static final List<Assignment> addressCountryToPhoneCountryCode = Arrays.asList(
            given(field(Address::getCountry))
                    .satisfies("Canada"::equals)
                    .set(field(Phone::getCountryCode), "+1"),

            valueOf(Address::getCountry)
                    .to(Phone::getCountryCode)
                    .as((Function<String, String>) s -> s.equals("Canada") ? "+1" : s));

    @FieldSource("addressCountryToPhoneCountryCode")
    @ParameterizedTest
    void givenCountryThenPhone(final Assignment assignment) {
        final List<Address> result = Instancio.ofList(Address.class)
                .size(100)
                .generate(field(Address::getCountry), gen -> gen.oneOf("Canada", "Other"))
                .assign(assignment)
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

    @Test
    void arrayElementWithAssignment() {
        final Function<Gender, String> fn = g -> {
            if (g == Gender.MALE) return "Paul";
            if (g == Gender.FEMALE) return "Cathy";
            return "Doug";
        };

        final Person result = Instancio.of(Person.class)
                .assign(valueOf(Person::getGender).to(Person::getName).as(fn))
                .assign(given(field(Person::getName), field(Pet::getName))
                        .set(When.is("Cathy"), "Cat")
                        .set(When.is("Doug"), "Dog")
                        .set(When.is("Paul"), "Pig"))
                .create();

        final String name, petName;

        if (result.getGender() == Gender.MALE) {
            name = "Paul";
            petName = "Pig";
        } else if (result.getGender() == Gender.FEMALE) {
            name = "Cathy";
            petName = "Cat";
        } else {
            name = "Doug";
            petName = "Dog";
        }
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getPets()).extracting(Pet::getName).containsOnly(petName);
    }

    @RepeatedTest(100)
    void givenCountryThenPhoneMultipleConditionals() {
        final int size = 100;
        final List<Address> results = Instancio.ofList(Address.class)
                .size(size)
                .generate(field(Address::getCountry), gen -> gen.oneOf("Canada", "USA", "UK", "Poland", "Germany"))
                .assign(given(field(Address::getCountry), field(Phone::getCountryCode))
                        .set(isIn("Canada", "USA"), "+1")
                        .set(is("UK"), "+44")
                        .set(is("Poland"), "+48")
                        .set(is("Germany"), "+49")
                        .elseSupply(() -> fail("Should not be reachable!")))
                .create();

        assertThat(results).hasSize(size).allSatisfy(result -> {
            final String country = result.getCountry();

            if (country.equals("Canada") || country.equals("USA")) {
                assertAllCountryCodesAreEqualTo(result, "+1");
            } else if (country.equals("UK")) {
                assertAllCountryCodesAreEqualTo(result, "+44");
            } else if (country.equals("Poland")) {
                assertAllCountryCodesAreEqualTo(result, "+48");
            } else if (country.equals("Germany")) {
                assertAllCountryCodesAreEqualTo(result, "+49");
            } else {
                fail("Unexpected country: " + country);
            }
        });
    }

    @Test
    void givenCountryCodeThenNumber() {
        final String expected = "604-123-5678";
        final Address result = Instancio.of(Address.class)
                .generate(field(Phone::getCountryCode), gen -> gen.oneOf("+1", "+2"))
                .assign(given(field(Phone::getCountryCode))
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
    void givenGenderThenName() {
        final String maleName = "Bob";
        final String femaleName = "Alice";
        final String randomNamePrefix = "random-";

        final Person result = Instancio.of(Person.class)
                .assign(given(all(Gender.class), field(Person::getName))
                        .set(is(Gender.MALE), maleName)
                        .set(is(Gender.FEMALE), femaleName)
                        .elseGenerate(gen -> gen.string().prefix(randomNamePrefix)))
                .create();

        if (result.getGender() == Gender.MALE) {
            assertThat(result.getName()).isEqualTo(maleName);
        } else if (result.getGender() == Gender.FEMALE) {
            assertThat(result.getName()).isEqualTo(femaleName);
        } else {
            assertThat(result.getName()).startsWith(randomNamePrefix);
        }
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void givenAgeThenName() {
        final Person result = Instancio.of(Person.class)
                .generate(field(Person::getAge), gen -> gen.ints().range(0, 20))
                .assign(given(field(Person::getAge), field(Person::getName))
                        .set((Integer age) -> age < 10, "Bobby")
                        .set((Integer age) -> age >= 10, "Bob"))
                .create();

        if (result.getAge() < 10) {
            assertThat(result.getName()).isEqualTo("Bobby");
        } else {
            assertThat(result.getName()).isEqualTo("Bob");
        }
    }

    @Test
    void givenCountryThenMultipleActions() {
        final String canada = "Canada";

        final Address result = Instancio.of(Address.class)
                .generate(field(Address::getCountry), gen -> gen.oneOf(canada, "Other"))
                .assign(given(field(Address::getCountry))
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

    /**
     * Set value of a field based on multiple other fields.
     */
    @Nested
    class MultipleDependentFieldsTest {

        @Test
        void stringFields_assignFieldOne() {
            final Function<StringFields, String> valueFn = o ->
                    String.format("%s %s %s", o.getTwo(), o.getThree(), o.getFour());

            final StringFields result = Instancio.of(StringFields.class)
                    .assign(valueOf(StringFields.class).to(StringFields::getOne).as(valueFn))
                    .create();

            assertThat(result.getOne()).isEqualTo(valueFn.apply(result));
        }

        @Test
        void stringsAbc_assignFieldC() {
            final Function<StringsAbc, String> valueFn = o ->
                    String.format("%s %s %s", o.getA(), o.getDef().getD(), o.getDef().getGhi().getI());

            final StringsAbc result = Instancio.of(StringsAbc.class)
                    .assign(valueOf(StringsAbc.class).to(StringsAbc::getC).as(valueFn))
                    .create();

            assertThat(result.getC()).isEqualTo(valueFn.apply(result));
        }

        @Test
        void stringsAbc_assignFieldE() {
            final Function<StringsAbc, String> valueFn = o ->
                    String.format("%s %s %s", o.getC(), o.getDef().getF(), o.getDef().getGhi().getH());

            final StringsAbc result = Instancio.of(StringsAbc.class)
                    .assign(valueOf(StringsAbc.class).to(StringsDef::getE).as(valueFn))
                    .create();

            assertThat(result.getDef().getE()).isEqualTo(valueFn.apply(result));
        }

        @Test
        void stringsAbc_assignFieldI() {
            final Function<StringsAbc, String> valueFn = o ->
                    String.format("%s %s %s", o.getA(), o.getDef().getD(), o.getDef().getGhi().getH());

            final StringsAbc result = Instancio.of(StringsAbc.class)
                    .assign(valueOf(StringsAbc.class).to(StringsGhi::getI).as(valueFn))
                    .create();

            assertThat(result.getDef().getGhi().getI()).isEqualTo(valueFn.apply(result));
        }
    }
}
