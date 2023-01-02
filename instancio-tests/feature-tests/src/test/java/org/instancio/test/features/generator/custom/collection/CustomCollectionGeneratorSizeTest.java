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
package org.instancio.test.features.generator.custom.collection;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.TypeToken;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.hints.CollectionHint;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.types;

@FeatureTag({
        Feature.COLLECTION_GENERATOR_SIZE,
        Feature.COLLECTION_GENERATOR_WITH_ELEMENTS,
        Feature.GENERATOR
})
class CustomCollectionGeneratorSizeTest {

    private static final String EXISTING_ELEMENT = "existing-element";
    private static final int INITIAL_SIZE = 1;
    private static final int GENERATE_ELEMENTS = 3;

    private static class CustomList<T> extends ArrayList<T> {}

    private static class CustomListOfStringGenerator implements Generator<CustomList<String>> {
        private final Hints hints;

        private CustomListOfStringGenerator(final Hints hints) {
            this.hints = hints;
        }

        @Override
        public CustomList<String> generate(final Random random) {
            final CustomList<String> list = new CustomList<>();
            list.add(EXISTING_ELEMENT);
            return list;
        }

        @Override
        public Hints hints() {
            return hints;
        }
    }

    @ParameterizedTest
    @EnumSource(value = AfterGenerate.class)
    @DisplayName("If generateElements is zero, collection should remain unchanged")
    void generateElementsIsZero(final AfterGenerate afterGenerate) {
        final Hints hints = Hints.builder()
                .afterGenerate(afterGenerate)
                .with(CollectionHint.builder()
                        .generateElements(0) // no elements should be generated
                        .build())
                .build();

        assertThat(createList(hints)).containsOnly(EXISTING_ELEMENT);
    }

    @Nested
    class AfterGenerateTest {

        @ParameterizedTest
        @EnumSource(value = AfterGenerate.class)
        void shouldGenerateElements(final AfterGenerate afterGenerate) {
            final Hints hints = Hints.builder()
                    .afterGenerate(afterGenerate)
                    .with(CollectionHint.builder().generateElements(GENERATE_ELEMENTS).build())
                    .build();

            assertThat(createList(hints))
                    .isExactlyInstanceOf(CustomList.class)
                    .hasSize(INITIAL_SIZE + GENERATE_ELEMENTS)
                    .doesNotContainNull()
                    .contains(EXISTING_ELEMENT);
        }
    }

    @Nested
    @DisplayName("If generateElements is not specified (i.e. zero) collection remains unchanged regardless of AfterGenerate")
    class MissingHintsTest {

        @Test
        void nullHints() {
            assertThat(createList(null)).containsOnly(EXISTING_ELEMENT);
        }

        @Test
        void nullAfterGenerate() {
            final Hints hints = Hints.builder().build();
            assertThat(hints.afterGenerate()).isNull();
            assertThat(createList(hints)).containsOnly(EXISTING_ELEMENT);
        }

        @ParameterizedTest
        @EnumSource(AfterGenerate.class)
        void nullCollectionHint(final AfterGenerate afterGenerate) {
            final Hints hints = Hints.afterGenerate(afterGenerate);
            assertThat(hints.get(CollectionHint.class)).isNull();
            assertThat(createList(hints)).containsOnly(EXISTING_ELEMENT);
        }

        @Test
        @DisplayName("AfterGenerate is null but generateElements is specified")
        void withGenerateElementsAndNullAfterGenerate() {
            final Hints hints = Hints.builder()
                    .with(CollectionHint.builder().generateElements(GENERATE_ELEMENTS).build())
                    .build();

            assertThat(hints.afterGenerate()).isNull();
            assertThat(createList(hints))
                    .hasSize(INITIAL_SIZE + GENERATE_ELEMENTS)
                    .contains(EXISTING_ELEMENT);
        }
    }

    @Nested
    class WithElementsTest {

        @ParameterizedTest
        @EnumSource(value = AfterGenerate.class)
        void shouldIncludeWithElements(final AfterGenerate afterGenerate) {
            final List<String> withElements = Arrays.asList("foo", "bar", "baz");

            final Hints hints = Hints.builder()
                    .afterGenerate(afterGenerate)
                    .with(CollectionHint.builder()
                            .generateElements(GENERATE_ELEMENTS)
                            .withElements(withElements)
                            .build())
                    .build();

            final int expectedSize = INITIAL_SIZE + GENERATE_ELEMENTS + withElements.size();

            assertThat(createList(hints))
                    .hasSize(expectedSize)
                    .doesNotContainNull()
                    .contains(EXISTING_ELEMENT, "foo", "bar", "baz");
        }
    }

    @Test
    @DisplayName("generateElements is specified, but AfterGenerate is set to DO_NOT_MODIFY via Settings")
    void settingIsDoNotModify() {
        final Hints hints = Hints.builder()
                .with(CollectionHint.builder().generateElements(GENERATE_ELEMENTS).build())
                .build();

        assertThat(hints.afterGenerate())
                .as("Should fallback to value specified via Settings")
                .isNull();

        final CustomList<String> result = Instancio.of(new TypeToken<CustomList<String>>() {})
                .supply(types().of(List.class), new CustomListOfStringGenerator(hints))
                .withSettings(Settings.create().set(Keys.AFTER_GENERATE_HINT, AfterGenerate.DO_NOT_MODIFY))
                .create();

        assertThat(result)
                .contains(EXISTING_ELEMENT)
                .hasSize(INITIAL_SIZE + GENERATE_ELEMENTS);
    }

    private static CustomList<String> createList(final Hints hints) {
        return Instancio.of(new TypeToken<CustomList<String>>() {})
                .supply(types().of(List.class), new CustomListOfStringGenerator(hints))
                .create();
    }

}
