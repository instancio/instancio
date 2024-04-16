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
package org.instancio.test.features.mode;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.TypeToken;
import org.instancio.internal.util.Sonar;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.arrays.ArrayPerson;
import org.instancio.test.support.pojo.collections.lists.ListPerson;
import org.instancio.test.support.pojo.collections.maps.MapStringPerson;
import org.instancio.test.support.pojo.cyclic.IndirectCircularRef;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.instancio.test.support.util.ArrayUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;

/**
 * If null is generated for a nullable and some selector(s) are not used as a result,
 * then the unused selector(s) should <b>not</b> trigger "unused selector" error.
 * <p>
 * This test verifies the above scenario, therefore some test methods do not have
 * assertions. The fact that "unused selector" error is not thrown means the test passes.
 */
@FeatureTag({
        Feature.MODE,
        Feature.GENERATE,
        Feature.ON_COMPLETE,
        Feature.SET,
        Feature.WITH_NULLABLE
})
@NonDeterministicTag
@ExtendWith(InstancioExtension.class)
class StrictModeWithNullableValuesTest {
    private static final int SAMPLE_SIZE = 100;

    // Limit size to 1 so that there's a higher probability of unused selector error.
    // With greater size, some values will be not non-null, preventing the error.
    private static final int CONTAINER_SIZE_WITH_NULLABLE = 1;

    private final Set<Object> results = new HashSet<>();

    @Test
    void overrideModelWithNullValue() {
        final Model<Person> model = Instancio.of(Person.class)
                .set(field(Address::getCity), "foo")
                .toModel();

        final Person result = Instancio.of(model)
                .set(field(Person::getAddress), null)
                .create();

        assertThat(result.getAddress()).isNull();
    }

    @RepeatedTest(SAMPLE_SIZE)
    @DisplayName("Selectors whose field type is nullable")
    void fieldTypeIsNullable() {
        Instancio.of(Person.class)
                // nullable strings
                .withNullable(allStrings())
                // and string fields
                .supply(field(Person::getName), random -> "foo")
                .generate(field(Address::getCity), gen -> gen.text().pattern("bar"))
                .onComplete(field(Person::getName), name -> assertThat(name).isEqualTo("foo"))
                .create();
    }

    @Test
    @DisplayName("Selectors whose fields are declared by a nullable class")
    void fieldsOfNullableClass() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final Person result = Instancio.of(Person.class)
                    // nullable address
                    .withNullable(all(Address.class))
                    // and address fields
                    .supply(field(Address::getCountry), random -> "foo")
                    .generate(field(Address::getCity), gen -> gen.text().pattern("bar"))
                    .onComplete(field(Address::getCountry), name -> assertThat(name).isEqualTo("foo"))
                    .create();

            results.add(result.getAddress());
        }
        assertThat(results).containsNull();
    }

    @Test
    @DisplayName("Selectors whose fields are declared by a nullable ancestor")
    void fieldsOfNullableAncestor() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            // Person -> Address -> Phone -> number
            //              |                  |
            //      (nullable ancestor)     (target)
            final Person result = Instancio.of(Person.class)
                    .withNullable(all(Address.class))
                    .set(field(Phone::getNumber), "foo")
                    .create();

            results.add(result.getAddress());
        }
        assertThat(results).containsNull();
    }

    @Test
    @SuppressWarnings(Sonar.ADD_ASSERTION)
    void nullableWithCyclicClass() {
        Instancio.of(IndirectCircularRef.class)
                .withNullable(all(IndirectCircularRef.A.class))
                .set(all(IndirectCircularRef.B.class), null)
                .create();
    }

    @Test
    void nullableArray() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final ArrayPerson result = Instancio.of(ArrayPerson.class)
                    .withNullable(all(Person[].class))
                    .set(field(Phone::getNumber), "foo")
                    .create();

            results.add(result.getArray());
        }
        assertThat(results).containsNull();
    }

    @Test
    void nullableArrayElement() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final ArrayPerson result = Instancio.of(ArrayPerson.class)
                    .generate(all(Person[].class), gen -> gen.array().nullableElements())
                    .set(field(Phone::getNumber), "foo")
                    .create();

            results.addAll(ArrayUtils.toList(result.getArray()));
        }
        assertThat(results).containsNull();
    }

    @Test
    @DisplayName("Selectors whose fields are declared by a nullable class")
    void nullableCollection() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final Person result = Instancio.of(Person.class)
                    .withNullable(all(List.class))
                    .set(field(Phone::getNumber), "foo")
                    .create();

            results.add(result.getAddress().getPhoneNumbers());
        }
        assertThat(results).containsNull();
    }

    @Test
    @DisplayName("Selectors targeting nullable collection elements")
    void nullableCollectionElement() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final ListPerson result = Instancio.of(ListPerson.class)
                    .generate(all(List.class), gen -> gen.collection()
                            .size(CONTAINER_SIZE_WITH_NULLABLE)
                            .nullableElements())
                    .set(field(Phone::getNumber), "foo")
                    .create();

            results.addAll(result.getList());
        }
        assertThat(results).containsNull();
    }

    @Test
    void nullableMap() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final MapStringPerson result = Instancio.of(MapStringPerson.class)
                    .withNullable(all(Map.class))
                    .set(field(Phone::getNumber), "foo")
                    .create();

            results.add(result.getMap());
        }
        assertThat(results).containsNull();
    }

    @Test
    void nullableMapKey() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final Map<Person, String> result = Instancio.of(new TypeToken<Map<Person, String>>() {})
                    .generate(all(Map.class), gen -> gen.map().size(CONTAINER_SIZE_WITH_NULLABLE).nullableKeys())
                    .set(field(Phone::getNumber), "foo")
                    .create();
            results.addAll(result.keySet());
        }
        assertThat(results).containsNull();
    }

    @Test
    void nullableMapValue() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final MapStringPerson result = Instancio.of(MapStringPerson.class)
                    .generate(all(Map.class), gen -> gen.map().size(CONTAINER_SIZE_WITH_NULLABLE).nullableValues())
                    .set(field(Phone::getNumber), "foo")
                    .create();
            results.addAll(result.getMap().values());
        }
        assertThat(results).containsNull();
    }
}
