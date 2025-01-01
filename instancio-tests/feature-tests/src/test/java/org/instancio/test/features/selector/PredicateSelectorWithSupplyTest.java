/*
 * Copyright 2022-2025 the original author or authors.
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
import org.instancio.test.support.pojo.person.PersonName;
import org.instancio.test.support.pojo.person.Pet;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.Pojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({Feature.PREDICATE_SELECTOR, Feature.SET, Feature.SUPPLY})
@ExtendWith(InstancioExtension.class)
class PredicateSelectorWithSupplyTest {

    private static final String EXPECTED_STRING = "foo";

    @Nested
    class FieldPredicateTest {

        @Test
        void fieldsNamed() {
            final Person result = Instancio.of(Person.class)
                    .set(fields().named("name"), EXPECTED_STRING)
                    .create();

            assertThat(result.getName()).isEqualTo(EXPECTED_STRING);
            assertThat(result.getPets()).extracting(Pet::getName).containsOnly(EXPECTED_STRING);
        }

        @Test
        void fieldsOfType() {
            final Person result = Instancio.of(Person.class)
                    .set(fields().ofType(String.class), EXPECTED_STRING)
                    .create();

            assertThatObject(result).hasAllFieldsOfTypeEqualTo(String.class, EXPECTED_STRING);
            assertThatObject(result.getAddress()).hasAllFieldsOfTypeEqualTo(String.class, EXPECTED_STRING);
            assertThat(result.getPets())
                    .allSatisfy(pet -> assertThatObject(pet).hasAllFieldsOfTypeEqualTo(String.class, EXPECTED_STRING));
            assertThat(result.getAddress().getPhoneNumbers())
                    .allSatisfy(phone -> assertThatObject(phone).hasAllFieldsOfTypeEqualTo(String.class, EXPECTED_STRING));
        }

        @Test
        void fieldsDeclaredIn() {
            final Person result = Instancio.of(Person.class)
                    .set(fields().declaredIn(Address.class).ofType(String.class), EXPECTED_STRING)
                    .create();

            assertThat(result.getName()).isNotEqualTo(EXPECTED_STRING);
            assertThat(result.getPets()).extracting(Pet::getName).doesNotContain(EXPECTED_STRING);
            assertThatObject(result.getAddress()).hasAllFieldsOfTypeEqualTo(String.class, EXPECTED_STRING);
        }

        @Test
        void fieldsAnnotated() {
            final Person result = Instancio.of(Person.class)
                    .set(fields().annotated(PersonName.class), EXPECTED_STRING)
                    .create();

            assertThat(result.getName()).isEqualTo(EXPECTED_STRING);
            assertThat(result.getPets()).extracting(Pet::getName).doesNotContain(EXPECTED_STRING);
        }

        @Test
        void setAllFieldsToNull() {
            final Person result = Instancio.of(Person.class)
                    .set(fields(), null) // matches all fields
                    .create();

            assertThat(result).hasAllNullFieldsOrPropertiesExcept("finalField", "age");
            assertThat(result.getAge()).isZero();
        }

        @Test
        void usingFieldPredicateDirectly() {
            final Predicate<Field> stringFieldsInAddressClass = field -> field.getType() == String.class
                    && field.getDeclaringClass().equals(Address.class);

            final Person result = Instancio.of(Person.class)
                    .set(fields(stringFieldsInAddressClass), EXPECTED_STRING)
                    .create();

            assertThatObject(result.getAddress()).hasAllFieldsOfTypeEqualTo(String.class, EXPECTED_STRING);
            assertThat(result.getName()).isNotEqualTo(EXPECTED_STRING);
        }
    }

    @Nested
    class TypePredicateTest {

        @Test
        void typesOf() {
            final Person result = Instancio.of(Person.class)
                    // The declared field is of type List
                    .supply(types().of(Collection.class), () -> new LinkedList<>())
                    .create();

            assertThat(result.getAddress().getPhoneNumbers()).isExactlyInstanceOf(LinkedList.class);
        }

        @Test
        void typesAnnotatedPojo() {
            final Person result = Instancio.of(Person.class)
                    .set(types().excluding(Person.class).annotated(Pojo.class), null)
                    .create();

            assertThat(result.getAddress()).isNull();
        }

        @Test
        void typesOfPhoneAnnotatedPojo() {
            final Person result = Instancio.of(Person.class)
                    .set(types().of(Phone.class).annotated(Pojo.class), null)
                    .create();

            assertThat(result.getAddress()).isNotNull();
            assertThat(result.getAddress().getPhoneNumbers()).isEmpty();
        }

        @Test
        void usingTypePredicateDirectly() {
            final Predicate<Class<?>> classNamesContainingDate = klass -> klass.getSimpleName().contains("Date");

            final Person result = Instancio.of(Person.class)
                    .set(types(classNamesContainingDate), null)
                    .create();

            assertThat(result.getLastModified()).isNull();
            assertThat(result.getDate()).isNull();
            assertThat(result).hasNoNullFieldsOrPropertiesExcept("lastModified", "date");
        }
    }

}
