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
package org.instancio.test.features.generator.custom.map;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.TypeToken;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
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
    @EnumSource(value = AfterGenerate.class)
    @DisplayName("If generateEntries is zero, map should remain unchanged")
    void generateEntriesIsZero(final AfterGenerate afterGenerate) {
        final Hints hints = Hints.builder()
                .afterGenerate(afterGenerate)
                .with(MapHint.builder()
                        .generateEntries(0) // no entries should be generated
                        .build())
                .build();

        assertThat(createMap(hints)).containsOnlyKeys(EXISTING_KEY);
    }

    @Nested
    class AfterGenerateTest {

        @ParameterizedTest
        @EnumSource(value = AfterGenerate.class, mode = EnumSource.Mode.EXCLUDE, names = "DO_NOT_MODIFY")
        void shouldGenerateEntries(final AfterGenerate afterGenerate) {
            final Hints hints = Hints.builder()
                    .afterGenerate(afterGenerate)
                    .with(MapHint.builder().generateEntries(GENERATE_ENTRIES).build())
                    .build();

            assertThat(createMap(hints))
                    .isExactlyInstanceOf(CustomMap.class)
                    .hasSize(INITIAL_SIZE + GENERATE_ENTRIES)
                    .containsKey(EXISTING_KEY)
                    .doesNotContainKey(null);
        }

        @Test
        void shouldNotGenerateEntries() {
            final Hints hints = Hints.builder()
                    .afterGenerate(AfterGenerate.DO_NOT_MODIFY)
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
    @DisplayName("If generateEntries is not specified (i.e. zero) map remains unchanged regardless of AfterGenerate")
    class MissingHintsTest {

        @Test
        void nullHints() {
            assertThat(createMap(null)).containsOnlyKeys(EXISTING_KEY);
        }

        @Test
        void nullAfterGenerate() {
            final Hints hints = Hints.builder().build();
            assertThat(hints.afterGenerate()).isNull();
            assertThat(createMap(hints)).containsOnlyKeys(EXISTING_KEY);
        }

        @ParameterizedTest
        @EnumSource(AfterGenerate.class)
        void nullMapHint(final AfterGenerate afterGenerate) {
            final Hints hints = Hints.afterGenerate(afterGenerate);
            assertThat(hints.get(MapHint.class)).isNull();
            assertThat(createMap(hints)).containsOnlyKeys(EXISTING_KEY);
        }

        @Test
        @DisplayName("AfterGenerate is null but generateEntries is specified")
        void generateEntriesWithNullAfterGenerate() {
            final Hints hints = Hints.builder()
                    .with(MapHint.builder().generateEntries(GENERATE_ENTRIES).build())
                    .build();

            assertThat(hints.afterGenerate()).isNull();
            assertThat(createMap(hints))
                    .hasSize(INITIAL_SIZE + GENERATE_ENTRIES)
                    .containsKey(EXISTING_KEY)
                    .doesNotContainKey(null);
        }
    }

    @Nested
    class WithEntriesTest {

        @ParameterizedTest
        @EnumSource(value = AfterGenerate.class)
        void shouldIncludeWithEntries(final AfterGenerate afterGenerate) {
            final Hints hints = Hints.builder()
                    .afterGenerate(afterGenerate)
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

    }

    @Test
    @DisplayName("generateEntries is specified, but AfterGenerate is set to DO_NOT_MODIFY via Settings")
    void settingIsDoNotModify() {
        final Hints hints = Hints.builder()
                .with(MapHint.builder().generateEntries(GENERATE_ENTRIES).build())
                .build();

        assertThat(hints.afterGenerate())
                .as("Should fallback to value specified via Settings")
                .isNull();

        final CustomMap<String, Integer> result = Instancio.of(new TypeToken<CustomMap<String, Integer>>() {})
                .supply(types().of(Map.class), new CustomMapGenerator(hints))
                .withSettings(Settings.create().set(Keys.AFTER_GENERATE_HINT, AfterGenerate.DO_NOT_MODIFY))
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
