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
package org.instancio.internal.util;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.support.DefaultRandom;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CollectionUtilsTest {
    private static final int SAMPLE_SIZE = 100;
    private static final int EXPECTED_NUM_COMBINATIONS = 6;
    private final Random random = new DefaultRandom();

    @Test
    void isNullOrEmptyCollection() {
        //noinspection ConstantConditions
        assertThat(CollectionUtils.isNullOrEmpty((Collection<?>) null)).isTrue();
        assertThat(CollectionUtils.isNullOrEmpty(Collections.emptyList())).isTrue();
        assertThat(CollectionUtils.isNullOrEmpty(Collections.singleton("foo"))).isFalse();
    }

    @Test
    void isNullOrEmptyMap() {
        //noinspection ConstantConditions
        assertThat(CollectionUtils.isNullOrEmpty((Map<?, ?>) null)).isTrue();
        assertThat(CollectionUtils.isNullOrEmpty(Collections.emptyMap())).isTrue();
        assertThat(CollectionUtils.isNullOrEmpty(new HashMap<String, String>() {{
            put("foo", "bar");
        }})).isFalse();
    }

    @Test
    void asList() {
        assertThat(CollectionUtils.asList(null)).isEmpty();
        assertThat(CollectionUtils.asList("foo", "bar")).containsExactly("foo", "bar");
    }

    @Test
    void asSet() {
        assertThat(CollectionUtils.asSet(null)).isEmpty();
        assertThat(CollectionUtils.asSet("foo", "bar")).containsExactlyInAnyOrder("foo", "bar");
    }

    @Test
    void asLinkedHashMap() {
        final Person[] persons = Instancio.create(Person[].class);
        final Map<UUID, Person> result = CollectionUtils.asLinkedHashMap(Person::getUuid, persons);
        assertThat(result).hasSize(persons.length);
        for (Person person : persons) {
            assertThat(result).containsEntry(person.getUuid(), person);
        }
    }

    @Test
    void asLinkedHashMapShouldReturnEmptyMapIfGivenEmptyOrNullArray() {
        assertThat(CollectionUtils.asLinkedHashMap(Person::getUuid, (Person[]) null)).isEmpty();
        assertThat(CollectionUtils.asLinkedHashMap(Person::getUuid)).isEmpty();
    }

    @Test
    void combine() {
        assertThat(CollectionUtils.combine(Arrays.asList(1, 2), 3)).containsExactly(1, 2, 3);
        assertThat(CollectionUtils.combine(Arrays.asList(1, 2))).containsExactly(1, 2);
        assertThat(CollectionUtils.combine(Collections.emptyList())).isEmpty();
    }

    @Test
    void shuffleEmpty() {
        final Collection<Object> collection = new ArrayList<>();
        CollectionUtils.shuffle(collection, random);
        assertThat(collection).isEmpty();
    }

    @Test
    void shuffleSingleElementCollection() {
        final Collection<Object> collection = new HashSet<>(Collections.singleton("foo"));
        CollectionUtils.shuffle(collection, random);
        assertThat(collection).containsOnly("foo");
    }

    @Test
    @NonDeterministicTag
    void shuffleList() {
        final Set<Collection<Object>> shuffledResults = new HashSet<>();
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final Collection<Object> collection = new ArrayList<>(Arrays.asList("foo", "bar", "baz"));
            CollectionUtils.shuffle(collection, random);
            shuffledResults.add(collection);
        }
        assertThat(shuffledResults).hasSize(EXPECTED_NUM_COMBINATIONS);
    }

    @Test
    @NonDeterministicTag
    void shuffleSet() {
        final Set<Collection<Object>> shuffledResults = new HashSet<>();
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final Collection<Object> collection = new ArrayList<>(Arrays.asList("foo", "bar", "baz"));
            CollectionUtils.shuffle(collection, random);
            shuffledResults.add(collection);
        }
        assertThat(shuffledResults).hasSize(EXPECTED_NUM_COMBINATIONS);
    }
}
