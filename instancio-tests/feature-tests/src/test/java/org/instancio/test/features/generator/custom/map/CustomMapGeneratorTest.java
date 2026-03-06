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
package org.instancio.test.features.generator.custom.map;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.hints.MapHint;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.collections.maps.MapIntegerItemOfString;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allInts;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.types;

@FeatureTag({
        Feature.MAP_GENERATOR_SUBTYPE,
        Feature.MAP_GENERATOR_SIZE,
        Feature.GENERATE,
        Feature.GENERATOR,
        Feature.AFTER_GENERATE
})
@NonDeterministicTag("Small chance of generating duplicate keys, which might fail some of the tests")
@ExtendWith(InstancioExtension.class)
class CustomMapGeneratorTest {

    private static final int EXISTING_KEY = Integer.MIN_VALUE;
    private static final String EXISTING_VALUE = "foo";
    private static final int INITIAL_SIZE = 1;
    private static final int GENERATE_ENTRIES = 3;

    @WithSettings
    private static final Settings SETTINGS = Settings.create()
            .set(Keys.INTEGER_MIN, EXISTING_KEY + 1)
            .set(Keys.INTEGER_MAX, Integer.MAX_VALUE)
            .lock();

    static class CustomMap<K, V> extends HashMap<K, V> {}

    private static class CustomMapGenerator implements Generator<CustomMap<Integer, Item<String>>> {
        private final Hints hints;

        private CustomMapGenerator(final Hints hints) {
            this.hints = hints;
        }

        @Override
        public CustomMap<Integer, Item<String>> generate(final Random random) {
            final CustomMap<Integer, Item<String>> map = new CustomMap<>();
            map.put(EXISTING_KEY, new Item<>(EXISTING_VALUE));
            return map;
        }

        @Override
        public Hints hints() {
            return hints;
        }
    }

    @ParameterizedTest
    @EnumSource(value = AfterGenerate.class, mode = EnumSource.Mode.EXCLUDE, names = "DO_NOT_MODIFY")
    @DisplayName("Should use map instance from generator and generate entries")
    void customMapSpecifiedAsSubtype(final AfterGenerate afterGenerate) {
        final Hints hints = Hints.builder().afterGenerate(afterGenerate)
                .with(MapHint.builder().generateEntries(GENERATE_ENTRIES).build())
                .build();

        final MapIntegerItemOfString result = Instancio.of(MapIntegerItemOfString.class)
                .supply(types().of(Map.class), new CustomMapGenerator(hints))
                .create();

        assertThat(result.getMap())
                .isExactlyInstanceOf(CustomMap.class)
                .doesNotContainKey(null)
                .containsKey(EXISTING_KEY)
                .hasSize(INITIAL_SIZE + GENERATE_ENTRIES);
    }

    @Test
    @DisplayName("Should generate map with custom subtype specified via gen.map().subtype()")
    void customMapSubtypeSpecifiedViaBuiltInMapGenerator() {
        final MapIntegerItemOfString result = Instancio.of(MapIntegerItemOfString.class)
                .generate(types().of(Map.class), gen -> gen.map()
                        .size(GENERATE_ENTRIES)
                        .with(EXISTING_KEY, "baz")
                        .subtype(CustomMap.class)) // subtype
                .create();

        assertThat(result.getMap())
                .isExactlyInstanceOf(CustomMap.class)
                .doesNotContainKey(null)
                .containsKey(EXISTING_KEY)
                .hasSize(INITIAL_SIZE + GENERATE_ENTRIES);
    }

    @Nested
    @ExtendWith(InstancioExtension.class)
    class WithAppliedSelector {

        @WithSettings
        private final Settings settings = SETTINGS;

        @ParameterizedTest
        @EnumSource(value = AfterGenerate.class, mode = EnumSource.Mode.EXCLUDE, names = "DO_NOT_MODIFY")
        @DisplayName("Should create custom map with specified number of entries and apply selector")
        void withAppliedSelector(final AfterGenerate afterGenerate) {
            final Hints hints = Hints.builder().afterGenerate(afterGenerate)
                    .with(MapHint.builder().generateEntries(GENERATE_ENTRIES).build())
                    .build();

            final int minKeyValue = Integer.MIN_VALUE;
            final int maxKeyValue = 0;

            final MapIntegerItemOfString result = Instancio.of(MapIntegerItemOfString.class)
                    .supply(types().of(Map.class), new CustomMapGenerator(hints))
                    .generate(allInts(), gen -> gen.ints().min(minKeyValue).max(maxKeyValue))
                    .create();

            assertThat(result.getMap())
                    .as("Should contain the entry added in the custom generator + generated entries")
                    .isExactlyInstanceOf(CustomMap.class)
                    .containsKey(EXISTING_KEY)
                    .doesNotContainKey(null)
                    .hasSize(INITIAL_SIZE + GENERATE_ENTRIES);

            assertThat(result.getMap().keySet())
                    .filteredOn(key -> key != EXISTING_KEY)
                    .hasSize(GENERATE_ENTRIES)
                    .allSatisfy(key -> assertThat(key).isBetween(minKeyValue, maxKeyValue));
        }

        @SuppressWarnings("NullAway")
        @Test
        @DisplayName("DO_NOT_MODIFY: should ignore matching selector")
        void doNotModify() {
            final Hints hints = Hints.builder().afterGenerate(AfterGenerate.DO_NOT_MODIFY)
                    .with(MapHint.builder().generateEntries(GENERATE_ENTRIES).build())
                    .build();

            final String expectedPrefix = "baz-";
            final MapIntegerItemOfString result = Instancio.of(MapIntegerItemOfString.class)
                    .supply(types().of(Map.class), new CustomMapGenerator(hints))
                    .generate(allStrings(), gen -> gen.string().prefix(expectedPrefix))
                    .create();

            assertThat(result.getMap())
                    .as("Should contain generated entries")
                    .hasSize(INITIAL_SIZE + GENERATE_ENTRIES);

            assertThat(result.getMap().values())
                    .as("Original entry should NOT be modified using selectors")
                    .extracting(Item::getValue)
                    .filteredOn(value -> value.equals(EXISTING_VALUE))
                    .hasSize(INITIAL_SIZE)
                    .allSatisfy(value -> assertThat(value).isEqualTo(EXISTING_VALUE));

            assertThat(result.getMap().values())
                    .extracting(Item::getValue)
                    .filteredOn(value -> !value.equals(EXISTING_VALUE))
                    .hasSize(GENERATE_ENTRIES)
                    .allSatisfy(value -> assertThat(value).startsWith(expectedPrefix));

            assertThat(result.getMap().get(EXISTING_KEY).getValue()).isEqualTo(EXISTING_VALUE);
        }
    }
}
