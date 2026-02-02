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
package org.instancio.test.guava.collect;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.guava.GenGuava;
import org.instancio.internal.util.Constants;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@ExtendWith(InstancioExtension.class)
class GuavaSupportedMultimapsTest {

    private static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(new TypeToken<ArrayListMultimap<UUID, String>>() {}, ArrayListMultimap.class),
                Arguments.of(new TypeToken<HashMultimap<UUID, String>>() {}, HashMultimap.class),
                Arguments.of(new TypeToken<ImmutableListMultimap<UUID, String>>() {}, ImmutableListMultimap.class),
                Arguments.of(new TypeToken<ImmutableMultimap<UUID, String>>() {}, ImmutableMultimap.class),
                Arguments.of(new TypeToken<ImmutableSetMultimap<UUID, String>>() {}, ImmutableSetMultimap.class),
                Arguments.of(new TypeToken<LinkedHashMultimap<UUID, String>>() {}, LinkedHashMultimap.class),
                Arguments.of(new TypeToken<LinkedListMultimap<UUID, String>>() {}, LinkedListMultimap.class),
                Arguments.of(new TypeToken<ListMultimap<UUID, String>>() {}, ArrayListMultimap.class),
                Arguments.of(new TypeToken<SortedSetMultimap<UUID, String>>() {}, TreeMultimap.class),
                Arguments.of(new TypeToken<SetMultimap<UUID, String>>() {}, HashMultimap.class),
                Arguments.of(new TypeToken<TreeMultimap<UUID, String>>() {}, TreeMultimap.class));
    }

    @ParameterizedTest
    @MethodSource("args")
    <M extends Multimap<UUID, String>> void verifyCreate(final TypeToken<M> type, final Class<?> expectedSubtype) {
        final Multimap<UUID, String> result = Instancio.create(type);

        assertThat(result)
                .as("Failed type: %s, expected subtype: %s", type.get(), expectedSubtype)
                .isInstanceOf(expectedSubtype);

        assertThat(result.size()).isBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);
    }

    @ParameterizedTest
    @MethodSource("args")
    <M extends Multimap<UUID, String>> void verifyCreateWithSize(final TypeToken<M> type, final Class<?> expectedSubtype) {
        final int size = 5;
        final Multimap<UUID, String> result = Instancio.of(type)
                .generate(root(), GenGuava.multimap().size(size))
                .create();

        assertThat(result)
                .as("Failed type: %s, expected subtype: %s", type.get(), expectedSubtype)
                .isInstanceOf(expectedSubtype);

        assertThat(result.size()).isEqualTo(size);
    }
}
