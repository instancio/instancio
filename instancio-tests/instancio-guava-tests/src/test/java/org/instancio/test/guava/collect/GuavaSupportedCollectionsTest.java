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
package org.instancio.test.guava.collect;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.internal.util.Constants;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@ExtendWith(InstancioExtension.class)
class GuavaSupportedCollectionsTest {

    private static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(new TypeToken<ConcurrentHashMultiset<UUID>>() {}, ConcurrentHashMultiset.class),
                Arguments.of(new TypeToken<HashMultiset<UUID>>() {}, HashMultiset.class),
                Arguments.of(new TypeToken<ImmutableList<UUID>>() {}, ImmutableList.class),
                Arguments.of(new TypeToken<ImmutableMultiset<UUID>>() {}, ImmutableMultiset.class),
                Arguments.of(new TypeToken<ImmutableSet<UUID>>() {}, ImmutableSet.class),
                Arguments.of(new TypeToken<ImmutableSortedMultiset<UUID>>() {}, ImmutableSortedMultiset.class),
                Arguments.of(new TypeToken<ImmutableSortedSet<UUID>>() {}, ImmutableSortedSet.class),
                Arguments.of(new TypeToken<LinkedHashMultiset<UUID>>() {}, LinkedHashMultiset.class),
                Arguments.of(new TypeToken<Multiset<UUID>>() {}, HashMultiset.class),
                Arguments.of(new TypeToken<SortedMultiset<UUID>>() {}, SortedMultiset.class),
                Arguments.of(new TypeToken<TreeMultiset<UUID>>() {}, TreeMultiset.class)
        );
    }

    @ParameterizedTest
    @MethodSource("args")
    <C extends Collection<UUID>> void verifyCreate(final TypeToken<C> type, final Class<?> expectedSubtype) {
        final Collection<UUID> result = Instancio.create(type);

        assertThat(result)
                .as("Failed type: %s, expected subtype: %s", type.get(), expectedSubtype)
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .isInstanceOf(expectedSubtype);
    }

    @ParameterizedTest
    @MethodSource("args")
    <C extends Collection<UUID>> void verifyCreateWithSize(final TypeToken<C> type, final Class<?> expectedSubtype) {
        final int size = 5;
        final UUID expected = Instancio.create(UUID.class);
        final Collection<UUID> result = Instancio.of(type)
                .generate(root(), gen -> gen.collection().size(size).with(expected))
                .create();

        assertThat(result)
                .as("Failed type: %s, expected subtype: %s", type.get(), expectedSubtype)
                .hasSize(size + 1) // plus expected element
                .contains(expected)
                .isInstanceOf(expectedSubtype);
    }
}
