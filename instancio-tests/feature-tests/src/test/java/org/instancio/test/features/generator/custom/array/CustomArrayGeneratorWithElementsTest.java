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
package org.instancio.test.features.generator.custom.array;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.PopulateAction;
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
        Feature.POPULATE_ACTION
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

    @Test
    @DisplayName("Action NONE: withElements() included; all empty indices populated ")
    void actionNoneWithElementsShouldNotBeSet() {
        final List<String> withElements = Arrays.asList("foo", "bar");
        final WithStringArray result = createArray(Hints.builder()
                .populateAction(PopulateAction.NONE)
                .with(ArrayHint.builder()
                        .withElements(withElements)
                        .build())
                .build());

        assertThat(result.getValues())
                .contains(EXISTING_ELEMENT)
                .containsAll(withElements)
                .doesNotContainNull();
    }

    @Test
    @DisplayName("Action APPLY_SELECTORS: withElements() included; empty indices populated")
    void actionApplySelectorsWithElements() {
        final List<String> withElements = Arrays.asList("foo", "bar");
        final Hints hints = Hints.builder()
                .populateAction(PopulateAction.APPLY_SELECTORS)
                .with(ArrayHint.builder()
                        .withElements(withElements)
                        .build())
                .build();

        assertThat(createArray(hints).getValues())
                .contains(EXISTING_ELEMENT)
                .containsAll(withElements)
                .doesNotContainNull();
    }

    @ParameterizedTest
    @EnumSource(value = PopulateAction.class, mode = EnumSource.Mode.INCLUDE,
            names = {"NULLS", "NULLS_AND_DEFAULT_PRIMITIVES"})
    @DisplayName("Actions NULLS and NULLS_AND_DEFAULT_PRIMITIVES: withElements() included; nulls populated ")
    void withElementsShouldBeSet(final PopulateAction action) {
        final List<String> withElements = Arrays.asList("foo", "bar");
        final WithStringArray result = createArray(Hints.builder()
                .populateAction(action)
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
        @DisplayName("Hints are null")
        void nullHints() {
            assertThat(createArray(null).getValues())
                    .contains(EXISTING_ELEMENT)
                    .doesNotContainNull();
        }

        @Test
        @DisplayName("PopulateAction is null")
        void nullPopulateAction() {
            final Hints hints = Hints.builder().build();
            assertThat(hints.populateAction()).isNull();
            assertThat(createArray(hints).getValues())
                    .contains(EXISTING_ELEMENT)
                    .doesNotContainNull();
        }

        @ParameterizedTest
        @EnumSource(value = PopulateAction.class, mode = EnumSource.Mode.INCLUDE,
                names = {"NONE", "APPLY_SELECTORS", "NULLS", "NULLS_AND_DEFAULT_PRIMITIVES"})
        @DisplayName("ArrayHint is null")
        void nullArrayHint_shouldPopulateArray(final PopulateAction action) {
            final Hints hints = Hints.withPopulateAction(action);
            assertThat(hints.get(ArrayHint.class)).isNull();
            assertThat(createArray(hints).getValues())
                    .contains(EXISTING_ELEMENT) // not overwritten
                    .doesNotContainNull();
        }

        @ParameterizedTest
        @EnumSource(value = PopulateAction.class, mode = EnumSource.Mode.INCLUDE, names = "ALL")
        @DisplayName("ArrayHint is null")
        void nullArrayHint_actionAll(final PopulateAction action) {
            final Hints hints = Hints.withPopulateAction(action);
            assertThat(hints.get(ArrayHint.class)).isNull();
            assertThat(createArray(hints).getValues())
                    .doesNotContain(EXISTING_ELEMENT) // overwritten
                    .doesNotContainNull();
        }
    }

    @Test
    @DisplayName("PopulateAction is set to NULLS via Settings")
    void populateActionIsNullsViaSettings() {
        assertArrayIsPopulated(null);

        assertArrayIsPopulated(Hints.builder()
                .with(ArrayHint.builder().build())
                .build());
    }

    private static void assertArrayIsPopulated(final Hints hints) {
        final WithStringArray result = Instancio.of(WithStringArray.class)
                .supply(all(String[].class), new ArrayGenerator(hints))
                .withSettings(Settings.create()
                        .set(Keys.GENERATOR_HINT_POPULATE_ACTION, PopulateAction.NULLS))
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
