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
package org.instancio.test.features.generator.map;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@FeatureTag(Feature.MAP_GENERATOR_SUBTYPE)
class MapGeneratorSupportedTypesTest {

    private static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(new TypeToken<ConcurrentHashMap<UUID, String>>() {}, ConcurrentHashMap.class),
                Arguments.of(new TypeToken<ConcurrentMap<UUID, String>>() {}, ConcurrentHashMap.class),
                Arguments.of(new TypeToken<ConcurrentNavigableMap<UUID, String>>() {}, ConcurrentSkipListMap.class),
                Arguments.of(new TypeToken<Hashtable<UUID, String>>() {}, Hashtable.class),
                Arguments.of(new TypeToken<Map<UUID, String>>() {}, HashMap.class),
                Arguments.of(new TypeToken<NavigableMap<UUID, String>>() {}, TreeMap.class),
                Arguments.of(new TypeToken<SortedMap<UUID, String>>() {}, TreeMap.class),
                Arguments.of(new TypeToken<WeakHashMap<UUID, String>>() {}, WeakHashMap.class)
        );
    }

    @ParameterizedTest
    @MethodSource("args")
    <M extends Map<UUID, String>> void verify(final TypeToken<M> type, final Class<?> expectedSubtype) {
        final int size = 5;
        final UUID expectedKey = Instancio.create(UUID.class);
        final String expectedValue = Instancio.create(String.class);
        final Map<UUID, String> result = Instancio.of(type)
                .generate(root(), gen -> gen.map().size(size).with(expectedKey, expectedValue))
                .create();

        assertThat(result)
                .as("Failed type: %s, expected subtype: %s", type.get(), expectedSubtype)
                .hasSize(size + 1)
                .containsEntry(expectedKey, expectedValue)
                .isExactlyInstanceOf(expectedSubtype);
    }
}
