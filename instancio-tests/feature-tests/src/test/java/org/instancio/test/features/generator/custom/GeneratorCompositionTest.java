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
package org.instancio.test.features.generator.custom;

import org.instancio.Generator;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Random;
import org.instancio.generator.Hints;
import org.instancio.generator.PopulateAction;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Person_;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.Phone_;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({
        Feature.GENERATE,
        Feature.GENERATOR,
        Feature.MODEL,
        Feature.POPULATE_ACTION,
        Feature.SET,
})
class GeneratorCompositionTest {

    private static final String PERSON_NAME = "John Doe";
    private static final String CITY = "Vancouver";
    private static final String COUNTRY = "Canada";
    private static final String COUNTRY_CODE = "+1";

    private abstract static class BaseGenerator<T> implements Generator<T> {
        private PopulateAction populateAction;

        BaseGenerator<T> withAction(final PopulateAction populateAction) {
            this.populateAction = populateAction;
            return this;
        }

        @Override
        public Hints hints() {
            assertThat(populateAction).as("action was not set!").isNotNull();
            return Hints.withPopulateAction(populateAction);
        }
    }

    private static class PersonGenerator extends BaseGenerator<Person> {
        @Override
        public Person generate(final Random random) {
            return Person.builder().name(PERSON_NAME).build();
        }
    }

    private static class AddressGenerator extends BaseGenerator<Address> {
        @Override
        public Address generate(final Random random) {
            return Address.builder().city(CITY).country(COUNTRY).build();
        }
    }

    private static class PhoneGenerator extends BaseGenerator<Phone> {
        @Override
        public Phone generate(final Random random) {
            return Phone.builder().countryCode(COUNTRY_CODE).build();
        }
    }

    @ParameterizedTest
    @EnumSource(value = PopulateAction.class, mode = EnumSource.Mode.INCLUDE,
            names = {"NULLS", "NULLS_AND_DEFAULT_PRIMITIVES"})
    @DisplayName("Should be able to compose multiple generators and override values as needed")
    void composeGenerators(final PopulateAction action) {
        final int age = 25;
        final int listSize = 3;
        final Model<Person> personModel = Instancio.of(Person.class)
                .supply(all(Person.class), new PersonGenerator().withAction(action))
                .supply(all(Address.class), new AddressGenerator().withAction(action))
                .supply(all(Phone.class), new PhoneGenerator().withAction(action))
                .generate(all(List.class), gen -> gen.collection().size(listSize))
                .generate(Phone_.number, gen -> gen.string().digits())
                .set(Person_.age, age)
                .toModel();

        final Person person = Instancio.create(personModel);

        assertThat(person.getName()).isEqualTo(PERSON_NAME);
        assertThat(person.getAge()).isEqualTo(age);
        assertThat(person.getAddress().getCity()).isEqualTo(CITY);
        assertThat(person.getAddress().getCountry()).isEqualTo(COUNTRY);

        assertThat(person.getAddress().getPhoneNumbers())
                .hasSize(listSize)
                .allSatisfy(phone -> {
                    assertThat(phone.getCountryCode()).isEqualTo(COUNTRY_CODE);
                    assertThat(phone.getNumber()).containsOnlyDigits();
                });

        assertThat(person).hasNoNullFieldsOrProperties();
        assertThat(person.getAddress()).hasNoNullFieldsOrProperties();
    }
}
