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
package org.instancio.test.features.generator.custom;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Person_;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.Phone_;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({
        Feature.GENERATE,
        Feature.GENERATOR,
        Feature.MODEL,
        Feature.AFTER_GENERATE,
        Feature.SET,
})
@ExtendWith(InstancioExtension.class)
class GeneratorCompositionTest {

    private static final String PERSON_NAME = "John Doe";
    private static final String CITY = "Vancouver";
    private static final String COUNTRY = "Canada";
    private static final String COUNTRY_CODE = "+1";

    private abstract static class BaseGenerator<T> implements Generator<T> {
        private AfterGenerate afterGenerate;

        BaseGenerator<T> afterGenerate(final AfterGenerate afterGenerate) {
            this.afterGenerate = afterGenerate;
            return this;
        }

        @Override
        public Hints hints() {
            assertThat(afterGenerate).as("AfterGenerate was not set!").isNotNull();
            return Hints.afterGenerate(afterGenerate);
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

    private static Model<Person> createModel(final AfterGenerate afterGenerate) {
        return Instancio.of(Person.class)
                .supply(all(Person.class), new PersonGenerator().afterGenerate(afterGenerate))
                .supply(all(Address.class), new AddressGenerator().afterGenerate(afterGenerate))
                .supply(all(Phone.class), new PhoneGenerator().afterGenerate(afterGenerate))
                .generate(Phone_.number, gen -> gen.string().digits())
                .toModel();
    }

    @ParameterizedTest
    @EnumSource(value = AfterGenerate.class, mode = EnumSource.Mode.INCLUDE,
            names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    @DisplayName("Should be able to compose multiple generators and override values as needed")
    void composeGenerators(final AfterGenerate afterGenerate) {
        final int age = 25;
        final int listSize = 3;

        final Person person = Instancio.of(createModel(afterGenerate))
                .generate(all(List.class), gen -> gen.collection().size(listSize))
                .set(Person_.age, age)
                .create();

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
