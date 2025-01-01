/*
 * Copyright 2022-2025 the original author or authors.
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
import org.instancio.TypeToken;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.hints.ArrayHint;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.interfaces.ItemInterface;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.types;

@FeatureTag({
        Feature.AFTER_GENERATE,
        Feature.ARRAY_GENERATOR_SUBTYPE,
        Feature.ARRAY_GENERATOR_LENGTH,
        Feature.GENERATE,
        Feature.GENERATOR,
        Feature.SUBTYPE
})
@ExtendWith(InstancioExtension.class)
class CustomArrayOfObjectsGeneratorTest {

    private static final int ARRAY_SIZE = 3;
    private static final int POPULATED_INDEX = 1;
    private static final String ORIGINAL_VALUE = "original-value";
    private static final String NEW_VALUE = "new-value";

    private static class ArrayGenerator implements Generator<ItemInterface<String>[]> {
        private final AfterGenerate afterGenerate;

        private ArrayGenerator(final AfterGenerate afterGenerate) {
            this.afterGenerate = afterGenerate;
        }

        @Override
        public ItemInterface<String>[] generate(final Random random) {
            final ItemInterface<String>[] array = new ItemInterface[ARRAY_SIZE];
            array[POPULATED_INDEX] = new Item<>(ORIGINAL_VALUE);
            return array;
        }

        @Override
        public Hints hints() {
            return Hints.builder()
                    .afterGenerate(afterGenerate)
                    .with(ArrayHint.builder().build())
                    .build();
        }
    }

    @Test
    @DisplayName("Engine should populate all indices, including the one with an existing value")
    void populateAll() {
        final ItemInterface<String>[] result = Instancio.of(new TypeToken<ItemInterface<String>[]>() {})
                .supply(types().of(ItemInterface[].class), new ArrayGenerator(AfterGenerate.POPULATE_ALL))
                .subtype(all(ItemInterface.class), Item.class)
                .create();

        assertThat(result)
                .hasSize(ARRAY_SIZE)
                .doesNotContainNull()
                .hasOnlyElementsOfType(Item.class)
                .allSatisfy(item -> assertThat(item.getValue()).isNotBlank());

        assertThat(result[POPULATED_INDEX].getValue()).isNotEqualTo(ORIGINAL_VALUE);
    }

    @ParameterizedTest
    @EnumSource(value = AfterGenerate.class, mode = EnumSource.Mode.INCLUDE,
            names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    @DisplayName("Engine should populate null indices only; occupied index should retain original value")
    void customArrayGeneratorWithSubtype(final AfterGenerate afterGenerate) {
        final ItemInterface<String>[] result = Instancio.of(new TypeToken<ItemInterface<String>[]>() {})
                .supply(types().of(ItemInterface[].class), new ArrayGenerator(afterGenerate))
                .subtype(all(ItemInterface.class), Item.class)
                .create();

        assertThat(result)
                .hasSize(ARRAY_SIZE)
                .doesNotContainNull()
                .hasOnlyElementsOfType(Item.class)
                .allSatisfy(item -> assertThat(item.getValue()).isNotBlank());

        assertThat(result[POPULATED_INDEX].getValue()).isEqualTo(ORIGINAL_VALUE);
    }

    @ParameterizedTest
    @EnumSource(value = AfterGenerate.class, mode = EnumSource.Mode.INCLUDE, names = {"DO_NOT_MODIFY", "APPLY_SELECTORS"})
    @DisplayName("Should populate empty indices; existing value should not be modified")
    void shouldNotPopulate(final AfterGenerate afterGenerate) {
        final ItemInterface<String>[] result = Instancio.of(new TypeToken<ItemInterface<String>[]>() {})
                .supply(types().of(ItemInterface[].class), new ArrayGenerator(afterGenerate))
                .subtype(all(ItemInterface.class), Item.class)
                .create();

        assertThat(result[POPULATED_INDEX].getValue()).isEqualTo(ORIGINAL_VALUE);

        for (int i = 0; i < result.length; i++) {
            if (i != POPULATED_INDEX) {
                assertThat(result[i]).isNull();
            }
        }
    }

    /**
     * Test applying a selector to array element's field
     * using different {@link AfterGenerate} values.
     */
    @Nested
    class ArrayElementFieldTest {

        @Test
        void doNotModify_shouldNotApplySelectorToArrayElementField() {
            final AfterGenerate afterGenerate = AfterGenerate.DO_NOT_MODIFY;

            final ItemInterface<String>[] result = Instancio.of(new TypeToken<ItemInterface<String>[]>() {})
                    .supply(types().of(ItemInterface[].class), new ArrayGenerator(afterGenerate))
                    .subtype(all(ItemInterface.class), Item.class)
                    .set(field(Item<String>::getValue), NEW_VALUE)
                    .lenient() // since selector is not applied
                    .create();

            assertThat(result[POPULATED_INDEX].getValue()).isEqualTo(ORIGINAL_VALUE);

            for (int i = 0; i < result.length; i++) {
                if (i != POPULATED_INDEX) {
                    assertThat(result[i]).isNull();
                }
            }
        }

        @Test
        void applySelectors_shouldApplySelectorToArrayElementField_ButNotPopulateNullIndices() {
            final AfterGenerate afterGenerate = AfterGenerate.APPLY_SELECTORS;

            final ItemInterface<String>[] result = Instancio.of(new TypeToken<ItemInterface<String>[]>() {})
                    .supply(types().of(ItemInterface[].class), new ArrayGenerator(afterGenerate))
                    .subtype(all(ItemInterface.class), Item.class)
                    .set(field(Item<String>::getValue), NEW_VALUE)
                    .create();

            assertThat(result[POPULATED_INDEX].getValue()).isEqualTo(NEW_VALUE);

            for (int i = 0; i < result.length; i++) {
                if (i != POPULATED_INDEX) {
                    assertThat(result[i]).isNull();
                }
            }
        }

        @ParameterizedTest
        @EnumSource(value = AfterGenerate.class, mode = EnumSource.Mode.EXCLUDE, names = {"DO_NOT_MODIFY", "APPLY_SELECTORS"})
        void shouldPopulateNullIndicesAndApplySelectorToAllElements(final AfterGenerate afterGenerate) {
            final ItemInterface<String>[] result = Instancio.of(new TypeToken<ItemInterface<String>[]>() {})
                    .supply(types().of(ItemInterface[].class), new ArrayGenerator(afterGenerate))
                    .subtype(all(ItemInterface.class), Item.class)
                    .set(field(Item<String>::getValue), NEW_VALUE)
                    .create();

            assertThat(result).allMatch(r -> NEW_VALUE.equals(r.getValue()));
        }
    }
}
