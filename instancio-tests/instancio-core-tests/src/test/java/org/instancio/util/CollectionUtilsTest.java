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
package org.instancio.util;

import org.instancio.Random;
import org.instancio.internal.random.DefaultRandom;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CollectionUtilsTest {
    private static final int SAMPLE_SIZE = 100;
    private static final int EXPECTED_NUM_COMBINATIONS = 6;
    private final Random random = new DefaultRandom();

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
