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
package org.instancio.test.features.generator.custom.array;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.hints.ArrayHint;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.arrays.object.WithStringArray;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({
        Feature.ARRAY_GENERATOR_WITH,
        Feature.ARRAY_GENERATOR_LENGTH,
        Feature.GENERATOR,
        Feature.AFTER_GENERATE
})
class CustomArrayGeneratorWithElementsTest {

    private static final int ARRAY_SIZE = 5;
    private static final int POPULATED_INDEX = 1;
    private static final String EXISTING_ELEMENT = "original-value";

    private static class ArrayGenerator implements Generator<String[]> {
        private final Hints hints;

        private ArrayGenerator(final Hints hints) {
            this.hints = hints;
        }

        @Override
        public String[] generate(final Random random) {
            final String[] array = new String[ARRAY_SIZE];
            array[POPULATED_INDEX] = EXISTING_ELEMENT;
            return array;
        }

        @Override
        public Hints hints() {
            return hints;
        }
    }

    @ParameterizedTest
    @EnumSource(AfterGenerate.class)
    void shouldIncludeWithElements(final AfterGenerate afterGenerate) {
        final List<String> withElements = Arrays.asList("foo", "bar");
        final WithStringArray result = createArray(Hints.builder()
                .afterGenerate(afterGenerate)
                .with(ArrayHint.builder()
                        .withElements(withElements)
                        .build())
                .build());

        assertThat(result.getValues())
                .contains(EXISTING_ELEMENT)
                .containsAll(withElements);

        if (afterGenerate == AfterGenerate.DO_NOT_MODIFY || afterGenerate == AfterGenerate.APPLY_SELECTORS) {
            assertThat(result.getValues()).containsNull();
        } else {
            assertThat(result.getValues()).doesNotContainNull();
        }
    }

    @Test
    @DisplayName("APPLY_SELECTORS: withElements() included; empty indices not populated")
    void applySelectors() {
        final List<String> withElements = Arrays.asList("foo", "bar");
        final Hints hints = Hints.builder()
                .afterGenerate(AfterGenerate.APPLY_SELECTORS)
                .with(ArrayHint.builder()
                        .withElements(withElements)
                        .build())
                .build();

        assertThat(createArray(hints).getValues())
                .contains(EXISTING_ELEMENT)
                .containsAll(withElements)
                .containsNull();
    }

    @ParameterizedTest
    @EnumSource(value = AfterGenerate.class, mode = EnumSource.Mode.INCLUDE,
            names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    void withElementsShouldBeSet(final AfterGenerate afterGenerate) {
        final List<String> withElements = Arrays.asList("foo", "bar");
        final WithStringArray result = createArray(Hints.builder()
                .afterGenerate(afterGenerate)
                .with(ArrayHint.builder()
                        .withElements(withElements)
                        .build())
                .build());

        assertThat(result.getValues())
                .contains(EXISTING_ELEMENT)
                .containsAll(withElements)
                .doesNotContainNull();
    }

    @Nested
    class BoundaryTest {

        @Test
        void withElementsIsEmpty() {
            final WithStringArray result = createArray(Hints.builder()
                    .with(ArrayHint.builder()
                            .withElements(Collections.emptyList())
                            .build())
                    .build());

            assertThat(result.getValues())
                    .contains(EXISTING_ELEMENT)
                    .doesNotContainNull();
        }

        @Test
        void withElementsIsGreaterThanArrayLength() {
            final List<String> withElements = Arrays.asList("one", "two", "three", "four", "five", "six");

            final WithStringArray result = createArray(Hints.builder()
                    .with(ArrayHint.builder()
                            .withElements(withElements)
                            .build())
                    .build());

            assertThat(result.getValues())
                    .contains(EXISTING_ELEMENT)
                    .contains("one", "two", "three", "four")
                    .doesNotContainNull();
        }
    }

    @Nested
    class MissingHintsTest {

        @Test
        void nullHints() {
            assertThat(createArray(null).getValues())
                    .contains(EXISTING_ELEMENT)
                    .doesNotContainNull();
        }

        @Test
        void nullAfterGenerate() {
            final Hints hints = Hints.builder().build();
            assertThat(hints.afterGenerate()).isNull();
            assertThat(createArray(hints).getValues())
                    .contains(EXISTING_ELEMENT)
                    .doesNotContainNull();
        }

        @ParameterizedTest
        @EnumSource(value = AfterGenerate.class, mode = EnumSource.Mode.INCLUDE,
                names = {"DO_NOT_MODIFY", "APPLY_SELECTORS"})
        void nullArrayHint_shouldNotPopulateArray(final AfterGenerate afterGenerate) {
            final Hints hints = Hints.afterGenerate(afterGenerate);
            assertThat(hints.get(ArrayHint.class)).isNull();
            assertThat(createArray(hints).getValues())
                    .contains(EXISTING_ELEMENT) // not overwritten
                    .containsNull();
        }

        @ParameterizedTest
        @EnumSource(value = AfterGenerate.class, mode = EnumSource.Mode.INCLUDE,
                names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
        void nullArrayHint_shouldPopulateArray(final AfterGenerate afterGenerate) {
            final Hints hints = Hints.afterGenerate(afterGenerate);
            assertThat(hints.get(ArrayHint.class)).isNull();
            assertThat(createArray(hints).getValues())
                    .contains(EXISTING_ELEMENT) // not overwritten
                    .doesNotContainNull();
        }

        @ParameterizedTest
        @EnumSource(value = AfterGenerate.class, mode = EnumSource.Mode.INCLUDE, names = "POPULATE_ALL")
        void nullArrayHint_populateAll(final AfterGenerate afterGenerate) {
            final Hints hints = Hints.afterGenerate(afterGenerate);
            assertThat(hints.get(ArrayHint.class)).isNull();
            assertThat(createArray(hints).getValues())
                    .doesNotContain(EXISTING_ELEMENT) // overwritten
                    .doesNotContainNull();
        }
    }

    @Test
    void settingIsPopulateNulls() {
        assertArrayIsPopulated(null);

        assertArrayIsPopulated(Hints.builder()
                .with(ArrayHint.builder().build())
                .build());
    }

    private static void assertArrayIsPopulated(final Hints hints) {
        final WithStringArray result = Instancio.of(WithStringArray.class)
                .supply(all(String[].class), new ArrayGenerator(hints))
                .withSettings(Settings.create()
                        .set(Keys.AFTER_GENERATE_HINT, AfterGenerate.POPULATE_NULLS))
                .create();

        assertThat(result.getValues())
                .contains(EXISTING_ELEMENT)
                .doesNotContainNull();
    }

    private static WithStringArray createArray(final Hints hints) {
        return Instancio.of(WithStringArray.class)
                .supply(all(String[].class), new ArrayGenerator(hints))
                .create();
    }
}
