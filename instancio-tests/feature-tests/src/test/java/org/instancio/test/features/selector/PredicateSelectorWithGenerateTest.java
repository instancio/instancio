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
package org.instancio.test.features.selector;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Pet;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({Feature.PREDICATE_SELECTOR, Feature.GENERATE})
@ExtendWith(InstancioExtension.class)
class PredicateSelectorWithGenerateTest {

    private static final String EXPECTED_STRING = "foo";

    @Nested
    class FieldPredicateTest {

        @Test
        void fieldsNamed() {
            final Person result = Instancio.of(Person.class)
                    .generate(fields().named("name"), gen -> gen.text().pattern(EXPECTED_STRING))
                    .create();

            assertThat(result.getName()).isEqualTo(EXPECTED_STRING);
            assertThat(result.getPets()).extracting(Pet::getName).containsOnly(EXPECTED_STRING);
        }

        @Test
        void fieldsOfType() {
            final Person result = Instancio.of(Person.class)
                    .generate(fields().ofType(Collection.class), gen -> gen.collection().size(1).subtype(LinkedList.class))
                    .create();

            assertThat(result.getAddress().getPhoneNumbers())
                    .hasSize(1)
                    .isExactlyInstanceOf(LinkedList.class);
        }

        @Test
        void usingFieldPredicateDirectly() {
            final Predicate<Field> stringFieldsInAddressClass = field -> field.getType() == String.class
                    && field.getDeclaringClass().equals(Address.class);

            final Person result = Instancio.of(Person.class)
                    .generate(fields(stringFieldsInAddressClass), gen -> gen.text().pattern(EXPECTED_STRING))
                    .create();

            assertThatObject(result.getAddress()).hasAllFieldsOfTypeEqualTo(String.class, EXPECTED_STRING);
            assertThat(result.getName()).isNotEqualTo(EXPECTED_STRING);
        }
    }

    @Nested
    class TypePredicateTest {

        @Test
        void typesOfWithLocalDateTime() {
            final Person result = Instancio.of(Person.class)
                    .generate(types().of(LocalDateTime.class), gen -> gen.temporal().localDateTime().past())
                    .create();

            assertThat(result.getLastModified()).isInThePast();
        }

        @Test
        void typesOfWithCollection() {
            final Person result = Instancio.of(Person.class)
                    // The declared field is of type List
                    .generate(types().of(Collection.class), gen -> gen.collection().subtype(LinkedList.class))
                    .create();

            assertThat(result.getAddress().getPhoneNumbers()).isExactlyInstanceOf(LinkedList.class);
            assertThatObject(result).isFullyPopulated();
        }
    }

}
