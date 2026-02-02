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
package org.instancio.test.features.generator.map;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.TypeToken;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;

@FeatureTag({
        Feature.GENERATE,
        Feature.MAP_GENERATOR_WITH_ENTRIES,
        Feature.MAP_GENERATOR_WITH_KEYS
})
@NonDeterministicTag("Chance of test failure in case of key collisions")
@ExtendWith(InstancioExtension.class)
class MapGeneratorWithKeysTest {
    private static final int SAMPLE_SIZE = 1000;
    private static final String[] EXPECTED_KEYS = {"one", "two", "three"};
    private static final TypeToken<Map<String, Integer>> MAP_TYPE_TOKEN = new TypeToken<>() {};

    @Nested
    class WithKeysAndMapSizeTest {
        @Test
        void randomSize() {
            final Set<Integer> distinctKeySetSizes = Instancio.of(MAP_TYPE_TOKEN)
                    .generate(all(Map.class), gen -> gen.map().withKeys(EXPECTED_KEYS))
                    .stream()
                    .limit(SAMPLE_SIZE)
                    .map(Map::size)
                    .collect(Collectors.toSet());

            final Set<Integer> expectedKeySetSizes = IntStream.rangeClosed(Constants.MIN_SIZE, Constants.MAX_SIZE)
                    .boxed().collect(Collectors.toSet());

            assertThat(distinctKeySetSizes).isEqualTo(expectedKeySetSizes);
        }

        @Test
        void sizeLessThanNumberOfKeys() {
            final Map<String, Integer> result = Instancio.of(MAP_TYPE_TOKEN)
                    .generate(all(Map.class), gen -> gen.map().withKeys(EXPECTED_KEYS).size(1))
                    .create();

            assertThat(result).hasSize(1).containsOnlyKeys("one");
        }

        @Test
        void sizeEqualsNumberOfKeys() {
            final Map<String, Integer> result = Instancio.of(MAP_TYPE_TOKEN)
                    .generate(all(Map.class), gen -> gen.map().withKeys(EXPECTED_KEYS).size(EXPECTED_KEYS.length))
                    .create();

            assertThat(result).hasSize(EXPECTED_KEYS.length).containsOnlyKeys(EXPECTED_KEYS);
        }

        @Test
        void sizeGreaterThanNumberOfKeys() {
            final int size = EXPECTED_KEYS.length + 5;

            final Map<String, Integer> result = Instancio.of(MAP_TYPE_TOKEN)
                    .generate(all(Map.class), gen -> gen.map().withKeys(EXPECTED_KEYS).size(size))
                    .create();

            assertThat(result).hasSize(size).containsKeys(EXPECTED_KEYS);
        }
    }

    @Nested
    class WithKeysAndWithEntries {
        @Test
        void withKeysAndWithEntries() {
            final int size = 10;
            final int numberOfWithEntries = 1;

            final Map<String, Integer> result = Instancio.of(MAP_TYPE_TOKEN)
                    .generate(all(Map.class), gen -> gen.map()
                            .withKeys(EXPECTED_KEYS)
                            .with("foo", -1)
                            .size(size))
                    .create();

            assertThat(result)
                    .hasSize(size + numberOfWithEntries)
                    .containsKeys(EXPECTED_KEYS)
                    .containsEntry("foo", -1);
        }

        @Test
        void withEntriesTakesPrecedenceOverWithKeysWhenTargetSizeIsReached() {
            final int size = 2;
            final int numberOfWithEntries = 1;

            final Map<String, Integer> result = Instancio.of(MAP_TYPE_TOKEN)
                    .generate(all(Map.class), gen -> gen.map()
                            .withKeys(EXPECTED_KEYS)
                            .with("foo", -1)
                            .size(size))
                    .create();

            assertThat(result)
                    .hasSize(size + numberOfWithEntries)
                    .containsEntry("foo", -1)
                    .containsKeys(EXPECTED_KEYS[0], EXPECTED_KEYS[1]) // size 3 reached
                    .doesNotContainKey(EXPECTED_KEYS[2]); // no space left for this one
        }
    }

    @Test
    void validation() {
        final InstancioApi<?> api = Instancio.of(MAP_TYPE_TOKEN)
                .generate(all(Map.class), gen -> gen.map().withKeys((Object[]) null));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("'map().withKeys(...)' must contain at least one key");
    }
}