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
package org.instancio.test.features.generator.custom.map;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.TypeToken;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.PopulateAction;
import org.instancio.generator.hints.MapHint;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.types;

@FeatureTag({
        Feature.MAP_GENERATOR_SIZE,
        Feature.GENERATOR
})
@ExtendWith(InstancioExtension.class)
class CustomMapGeneratorSizeTest {

    private static final String EXISTING_KEY = "existing-key";
    private static final int INITIAL_SIZE = 1;
    private static final int GENERATE_ENTRIES = 3;

    private static final Map<String, Integer> WITH_ENTRIES = new HashMap<String, Integer>() {{
        put("foo", 123);
        put("bar", 234);
        put("baz", 345);
    }};

    private static class CustomMap<K, V> extends HashMap<K, V> {}

    private static class CustomMapGenerator implements Generator<CustomMap<String, Integer>> {
        private final Hints hints;

        private CustomMapGenerator(final Hints hints) {
            this.hints = hints;
        }

        @Override
        public CustomMap<String, Integer> generate(final Random random) {
            final CustomMap<String, Integer> map = new CustomMap<>();
            map.put(EXISTING_KEY, -12345);
            return map;
        }

        @Override
        public Hints hints() {
            return hints;
        }
    }

    @ParameterizedTest
    @EnumSource(value = PopulateAction.class)
    @DisplayName("If generateEntries is zero, map should remain unchanged")
    void generateEntriesIsZero(final PopulateAction action) {
        final Hints hints = Hints.builder()
                .populateAction(action)
                .with(MapHint.builder()
                        .generateEntries(0) // no entries should be generated
                        .build())
                .build();

        assertThat(createMap(hints)).containsOnlyKeys(EXISTING_KEY);
    }

    @Nested
    class PopulateActionTest {

        @ParameterizedTest
        @EnumSource(value = PopulateAction.class, mode = EnumSource.Mode.EXCLUDE, names = "NONE")
        @DisplayName("Should generate entries if action is not NONE")
        void shouldGenerateEntriesIfActionIsNotNone(final PopulateAction action) {
            final Hints hints = Hints.builder()
                    .populateAction(action)
                    .with(MapHint.builder().generateEntries(GENERATE_ENTRIES).build())
                    .build();

            assertThat(createMap(hints))
                    .isExactlyInstanceOf(CustomMap.class)
                    .hasSize(INITIAL_SIZE + GENERATE_ENTRIES)
                    .containsKey(EXISTING_KEY)
                    .doesNotContainKey(null);
        }

        @Test
        @DisplayName("Should generate entries if action is NONE")
        void shouldNotGenerateEntriesIfActionIsNone() {
            final Hints hints = Hints.builder()
                    .populateAction(PopulateAction.NONE)
                    .with(MapHint.builder()
                            .generateEntries(GENERATE_ENTRIES)
                            .build())
                    .build();

            assertThat(createMap(hints))
                    .containsKey(EXISTING_KEY)
                    .hasSize(INITIAL_SIZE + GENERATE_ENTRIES);
        }
    }

    @Nested
    @DisplayName("If generateEntries is not specified (i.e. zero) map remains unchanged regardless of action")
    class MissingHintsTest {

        @Test
        @DisplayName("Hints are null")
        void nullHints() {
            assertThat(createMap(null)).containsOnlyKeys(EXISTING_KEY);
        }

        @Test
        @DisplayName("PopulateAction is null")
        void nullPopulateAction() {
            final Hints hints = Hints.builder().build();
            assertThat(hints.populateAction()).isNull();
            assertThat(createMap(hints)).containsOnlyKeys(EXISTING_KEY);
        }

        @ParameterizedTest
        @EnumSource(PopulateAction.class)
        @DisplayName("MapHint is null")
        void nullMapHint(final PopulateAction action) {
            final Hints hints = Hints.withPopulateAction(action);
            assertThat(hints.get(MapHint.class)).isNull();
            assertThat(createMap(hints)).containsOnlyKeys(EXISTING_KEY);
        }

        @Test
        @DisplayName("PopulateAction is null but generateEntries is specified")
        void nullPopulateActionButWithGenerateEntries() {
            final Hints hints = Hints.builder()
                    .with(MapHint.builder().generateEntries(GENERATE_ENTRIES).build())
                    .build();

            assertThat(hints.populateAction()).isNull();
            assertThat(createMap(hints))
                    .hasSize(INITIAL_SIZE + GENERATE_ENTRIES)
                    .containsKey(EXISTING_KEY)
                    .doesNotContainKey(null);
        }
    }

    @Nested
    class WithEntriesTest {

        @ParameterizedTest
        @DisplayName("withEntries() included if action is NOT NONE")
        @EnumSource(value = PopulateAction.class, mode = EnumSource.Mode.EXCLUDE, names = "NONE")
        void withEntriesWhenActionIsNotNone(final PopulateAction action) {
            final Hints hints = Hints.builder()
                    .populateAction(action)
                    .with(MapHint.builder()
                            .generateEntries(GENERATE_ENTRIES)
                            .withEntries(WITH_ENTRIES)
                            .build())
                    .build();

            final int expectedSize = INITIAL_SIZE + GENERATE_ENTRIES + WITH_ENTRIES.size();

            assertThat(createMap(hints))
                    .hasSize(expectedSize)
                    .doesNotContainKey(null)
                    .containsKeys(EXISTING_KEY)
                    .containsKeys("foo", "bar", "baz");
        }

        @Test
        @DisplayName("withEntries() should be included if action is NONE")
        void withEntriesWhenActionIsNone() {
            final Hints hints = Hints.builder()
                    .populateAction(PopulateAction.NONE)
                    .with(MapHint.builder()
                            .generateEntries(GENERATE_ENTRIES)
                            .withEntries(WITH_ENTRIES)
                            .build())
                    .build();

            final int expectedSize = INITIAL_SIZE + GENERATE_ENTRIES + WITH_ENTRIES.size();
            assertThat(createMap(hints))
                    .containsKey(EXISTING_KEY)
                    .hasSize(expectedSize);
        }
    }

    @Test
    @DisplayName("generateEntries is specified, but PopulateAction is set to NONE via Settings")
    void populateActionIsNoneViaSettings() {
        final Hints hints = Hints.builder()
                .with(MapHint.builder().generateEntries(GENERATE_ENTRIES).build())
                .build();

        assertThat(hints.populateAction())
                .as("Should fallback to action specified via Settings")
                .isNull();

        final CustomMap<String, Integer> result = Instancio.of(new TypeToken<CustomMap<String, Integer>>() {})
                .supply(types().of(Map.class), new CustomMapGenerator(hints))
                .withSettings(Settings.create().set(Keys.GENERATOR_HINT_POPULATE_ACTION, PopulateAction.NONE))
                .create();

        assertThat(result)
                .hasSize(INITIAL_SIZE + GENERATE_ENTRIES)
                .containsKey(EXISTING_KEY);
    }

    private static CustomMap<String, Integer> createMap(final Hints hints) {
        return Instancio.of(new TypeToken<CustomMap<String, Integer>>() {})
                .supply(types().of(Map.class), new CustomMapGenerator(hints))
                .create();
    }

}
