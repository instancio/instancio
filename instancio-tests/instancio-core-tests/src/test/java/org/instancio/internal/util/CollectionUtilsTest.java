/*
 * Copyright 2022-2026 the original author or authors.
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
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        assertThat(CollectionUtils.isNullOrEmpty(Map.of("foo", "bar"))).isFalse();
    }

    @Test
    void asUnmodifiableList() {
        assertThat(CollectionUtils.asUnmodifiableList((String[]) null)).isEmpty();
        assertThat(CollectionUtils.asUnmodifiableList("foo", "bar")).containsExactly("foo", "bar");
    }

    @Test
    void asSet() {
        assertThat(CollectionUtils.asSet()).isEmpty();
        assertThat(CollectionUtils.asSet((Object[]) null)).isEmpty();
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
    void copyAsLinkedHashMap() {
        final Map<String, List<Integer>> map = new HashMap<>();
        map.put("foo", List.of(42, 43));

        final Map<String, List<Integer>> mutableMapCopy = CollectionUtils.copyAsLinkedHashMap(
                Collections.unmodifiableMap(map));

        mutableMapCopy.get("foo").add(123);
        assertThat(mutableMapCopy).containsValue(List.of(42, 43, 123));

        mutableMapCopy.put("bar", Arrays.asList(567));
        assertThat(mutableMapCopy)
                .hasSize(2)
                .containsEntry("bar", List.of(567));
    }

    @Test
    void copyAsLinkedHashMap_nullShouldReturnEmptyMap() {
        final Map<String, List<Integer>> mutableMapCopy = CollectionUtils.copyAsLinkedHashMap(null);

        final List<Integer> newValue = Arrays.asList(123);

        mutableMapCopy.put("bar", newValue);
        assertThat(mutableMapCopy).containsValue(List.of(123));
    }

    @Test
    void asUnmodifiableLinkedHashMapOfLists() {
        final Map<String, List<Integer>> map = new HashMap<>();
        map.put("foo", Arrays.asList(42, 43));

        final Map<String, List<Integer>> mapCopy = CollectionUtils.asUnmodifiableLinkedHashMapOfLists(map);

        assertThat(mapCopy)
                .hasSize(1)
                .containsValue(List.of(42, 43));

        assertThatThrownBy(() -> mapCopy.put("bar", List.of(123)))
                .isInstanceOf(UnsupportedOperationException.class);

        final List<Integer> listCopy = mapCopy.get("foo");

        assertThatThrownBy(() -> listCopy.add(123))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void asUnmodifiableLinkedHashMapOfLists_nullShouldReturnUnmodifiableEmptyMap() {
        final Map<String, List<Integer>> mapCopy =
                CollectionUtils.asUnmodifiableLinkedHashMapOfLists(null);

        final List<Integer> newValue = List.of(123);

        assertThatThrownBy(() -> mapCopy.put("bar", newValue))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void combine() {
        assertThat(CollectionUtils.combine(List.of(1, 2), 3)).containsExactly(1, 2, 3);
        assertThat(CollectionUtils.combine(List.of(1, 2))).containsExactly(1, 2);
        assertThat(CollectionUtils.combine(Collections.emptyList())).isEmpty();
    }

    @Test
    void flatMap() {
        assertThat(CollectionUtils.flatMap(List.of())).isEmpty();

        assertThat(CollectionUtils.flatMap(
                List.of(List.of(1, 2, 3), List.of(4, 5, 6)))).containsExactly(1, 2, 3, 4, 5, 6);

        assertThat(CollectionUtils.flatMap(
                List.of(List.of(), List.of(4, 5, 6)))).containsExactly(4, 5, 6);
    }

    @Test
    void identityIndexOf() {
        final StringHolder a = new StringHolder("x");
        final StringHolder b = new StringHolder("x");
        final StringHolder c = new StringHolder("x");
        final StringHolder x = new StringHolder("x");
        final List<StringHolder> list = Arrays.asList(c, a, null, b);

        // precondition: all pass equals()
        assertThat(a).isEqualTo(b).isEqualTo(c).isEqualTo(x);

        assertThat(CollectionUtils.identityIndexOf(c, list)).isZero();
        assertThat(CollectionUtils.identityIndexOf(a, list)).isOne();
        assertThat(CollectionUtils.identityIndexOf(null, list)).isEqualTo(2);
        assertThat(CollectionUtils.identityIndexOf(b, list)).isEqualTo(3);
        assertThat(CollectionUtils.identityIndexOf(x, list)).isEqualTo(-1);
    }

    @Test
    void shuffleEmpty() {
        final Collection<Object> collection = new ArrayList<>();
        CollectionUtils.shuffle(collection, random);
        assertThat(collection).isEmpty();
    }

    @Test
    void shuffleSingleElementCollection() {
        final Collection<Object> collection = new HashSet<>(Set.of("foo"));
        CollectionUtils.shuffle(collection, random);
        assertThat(collection).containsOnly("foo");
    }

    @Test
    @NonDeterministicTag
    void shuffleList() {
        final Set<Collection<Object>> shuffledResults = new HashSet<>();
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final Collection<Object> collection = Arrays.asList("foo", "bar", "baz");
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
            final Collection<Object> collection = Arrays.asList("foo", "bar", "baz");
            CollectionUtils.shuffle(collection, random);
            shuffledResults.add(collection);
        }
        assertThat(shuffledResults).hasSize(EXPECTED_NUM_COMBINATIONS);
    }
}
