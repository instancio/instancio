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
package org.instancio.api;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.TypeToken;
import org.instancio.test.support.pojo.generics.basic.Pair;
import org.instancio.test.support.pojo.generics.basic.Triplet;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.AddressExtension;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Pet;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.instancio.Bindings.all;
import static org.instancio.Bindings.field;
import static org.instancio.testsupport.asserts.ReflectionAssert.assertThatObject;

/**
 * Smoke test invoking various API methods.
 */
class InstancioApiTest {

    private static final int HOMER_AGE = 40;
    private static final String HOMER = "Homer";
    private static final String SPRINGFIELD = "Springfield";

    @Test
    void createFromClass() {
        final Person homer = Instancio.of(Person.class)
                .supply(field("name"), () -> HOMER)
                .supply(field(Person.class, "age"), () -> HOMER_AGE)
                .supply(all(LocalDateTime.class), () -> LocalDateTime.now())
                .withNullable(field("date"))
                .withNullable(field(Person.class, "pets"))
                .withNullable(all(Gender.class))
                .map(all(Address.class), AddressExtension.class)
                .create();

        assertThat(homer.getName()).isEqualTo(HOMER);
        assertThat(homer.getAge()).isEqualTo(HOMER_AGE);
        assertThat(homer.getLastModified()).isCloseTo(LocalDateTime.now(), within(3, ChronoUnit.SECONDS));
        assertThat(homer.getAddress())
                .isExactlyInstanceOf(AddressExtension.class)
                .satisfies(it -> assertThat(((AddressExtension) it).getAdditionalInfo()).isNotBlank());
    }

    @Test
    void createFromClassWithTypeParameters() {
        //noinspection unchecked
        final Pair<Integer, String> pair = Instancio.of(Pair.class)
                .withTypeParameters(Integer.class, String.class)
                .create();

        assertThat(pair.getLeft()).isInstanceOf(Integer.class);
        assertThat(pair.getRight()).isInstanceOf(String.class);
    }

    @Test
    void createFromType() {
        final Pair<Integer, Triplet<String, Long, Boolean>> pair = Instancio.of(
                new TypeToken<Pair<Integer, Triplet<String, Long, Boolean>>>() {
                }).create();

        assertThat(pair.getLeft()).isInstanceOf(Integer.class);
        assertThat(pair.getRight()).isInstanceOf(Triplet.class);

        final Triplet<String, Long, Boolean> triplet = pair.getRight();
        assertThat(triplet.getLeft()).isInstanceOf(String.class);
        assertThat(triplet.getMid()).isInstanceOf(Long.class);
        assertThat(triplet.getRight()).isInstanceOf(Boolean.class);
    }

    @Test
    void createFromModel() {
        final Model<Person> homerModel = Instancio.of(Person.class)
                .supply(field("name"), () -> HOMER)
                .supply(field(Person.class, "age"), () -> HOMER_AGE)
                .supply(all(LocalDateTime.class), () -> LocalDateTime.now())
                .supply(field(Address.class, "city"), () -> SPRINGFIELD)
                .ignore(field(Person.class, "date"))
                .ignore(all(Gender.class))
                .toModel();

        final Person homer = Instancio.of(homerModel).create();

        assertThat(homer.getName()).isEqualTo(HOMER);
        assertThat(homer.getAge()).isEqualTo(HOMER_AGE);
        assertThat(homer.getLastModified()).isCloseTo(LocalDateTime.now(), within(3, ChronoUnit.SECONDS));
        assertThat(homer.getAddress().getCity()).isEqualTo(SPRINGFIELD);
        assertThat(homer.getDate()).isNull();
        assertThat(homer.getGender()).isNull();
        assertThat(homer.getPets())
                .isNotEmpty()
                .hasOnlyElementsOfType(Pet.class);
    }

    @Test
    void createStreamFromClass() {
        final List<Person> results = Instancio.of(Person.class)
                .supply(field("name"), () -> HOMER)
                .stream()
                .limit(5)
                .collect(toList());

        assertThat(results).hasSize(5)
                .extracting(Person::getName)
                .containsOnly(HOMER);

        assertThatObject(results).isFullyPopulated();
    }

    @Test
    void createStreamFromTypeToken() {
        final List<Person> results = Instancio.of(new TypeToken<Person>() {})
                .supply(field("name"), () -> HOMER)
                .stream()
                .limit(5)
                .collect(toList());

        assertThat(results).hasSize(5)
                .extracting(Person::getName)
                .containsOnly(HOMER);

        assertThatObject(results).isFullyPopulated();
    }
}
