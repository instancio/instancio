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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.internal.util.Constants;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@ExtendWith(InstancioExtension.class)
class GuavaSupportedMapsTest {

    private static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(new TypeToken<BiMap<UUID, String>>() {}, HashBiMap.class),
                Arguments.of(new TypeToken<HashBiMap<UUID, String>>() {}, HashBiMap.class),
                Arguments.of(new TypeToken<ImmutableBiMap<UUID, String>>() {}, ImmutableBiMap.class),
                Arguments.of(new TypeToken<ImmutableMap<UUID, String>>() {}, ImmutableMap.class),
                Arguments.of(new TypeToken<ImmutableSortedMap<UUID, String>>() {}, ImmutableSortedMap.class)
        );
    }

    @ParameterizedTest
    @MethodSource("args")
    <M extends Map<UUID, String>> void verify(final TypeToken<M> type, final Class<?> expectedSubtype) {
        verifyCreate(type, expectedSubtype);
        verifyCreateWithSize(type, expectedSubtype);
    }

    private static <M extends Map<UUID, String>> void verifyCreate(final TypeToken<M> type, final Class<?> expectedSubtype) {
        final Map<UUID, String> result = Instancio.create(type);

        assertThat(result)
                .as("Failed type: %s, expected subtype: %s", type.get(), expectedSubtype)
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .isInstanceOf(expectedSubtype);
    }

    private static <M extends Map<UUID, String>> void verifyCreateWithSize(final TypeToken<M> type, final Class<?> expectedSubtype) {
        final int size = 5;
        final UUID expectedKey = Instancio.create(UUID.class);
        final String expectedValue = Instancio.create(String.class);
        final Map<UUID, String> result = Instancio.of(type)
                .generate(root(), gen -> gen.map().size(size).with(expectedKey, expectedValue))
                .create();

        assertThat(result)
                .as("Failed type: %s, expected subtype: %s", type.get(), expectedSubtype)
                .hasSize(size + 1) // plus expected entry
                .containsEntry(expectedKey, expectedValue)
                .isInstanceOf(expectedSubtype);
    }
}
