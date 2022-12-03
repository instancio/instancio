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
package org.instancio.test.features.generator.custom;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.TypeToken;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.PopulateAction;
import org.instancio.generator.hints.DataStructureHint;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.interfaces.ItemInterface;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.types;

@FeatureTag({
        Feature.ARRAY_GENERATOR_SUBTYPE,
        Feature.ARRAY_GENERATOR_LENGTH,
        Feature.GENERATE,
        Feature.GENERATOR,
        Feature.POPULATE_ACTION,
        Feature.SUBTYPE
})
@Disabled("TODO - currently arrays are not supported for all Action types")
class CustomArrayGeneratorTest {

    private static final int SIZE_HINT = 3;
    private static final int POPULATED_INDEX = 1;
    private static final String ORIGINAL_VALUE = "original-value";

    private static class ArrayGenerator implements Generator<ItemInterface<String>[]> {
        private final PopulateAction populateAction;

        private ArrayGenerator(final PopulateAction populateAction) {
            this.populateAction = populateAction;
        }

        @Override
        public ItemInterface<String>[] generate(final Random random) {
            final ItemInterface<String>[] array = new ItemInterface[SIZE_HINT];
            array[POPULATED_INDEX] = new Item<>(ORIGINAL_VALUE);
            return array;
        }

        @Override
        public Hints hints() {
            return Hints.builder()
                    .populateAction(populateAction)
                    .hint(DataStructureHint.builder()
                            .dataStructureSize(SIZE_HINT)
                            .build())
                    .build();
        }
    }

    @Test
    @DisplayName("ALL: engine should populate all indices, include the one with an existing value")
    void actionAll() {
        final ItemInterface<String>[] result = Instancio.of(new TypeToken<ItemInterface<String>[]>() {})
                .supply(types().of(ItemInterface[].class), new ArrayGenerator(PopulateAction.ALL))
                .subtype(all(ItemInterface.class), Item.class)
                .create();

        assertThat(result)
                .hasSize(SIZE_HINT)
                .doesNotContainNull()
                .hasOnlyElementsOfType(Item.class)
                .allSatisfy(item -> assertThat(item.getValue()).isNotBlank());

        assertThat(result[POPULATED_INDEX].getValue()).isNotEqualTo(ORIGINAL_VALUE);
    }

    @ParameterizedTest
    @EnumSource(value = PopulateAction.class, mode = EnumSource.Mode.INCLUDE, names = {"NULLS", "NULLS_AND_DEFAULT_PRIMITIVES"})
    @DisplayName("Engine should populate null indices only; occupied index should retain original value")
    void customArrayGeneratorWithSubtype(final PopulateAction populateAction) {
        final ItemInterface<String>[] result = Instancio.of(new TypeToken<ItemInterface<String>[]>() {})
                .supply(types().of(ItemInterface[].class), new ArrayGenerator(populateAction))
                .subtype(all(ItemInterface.class), Item.class)
                .create();

        assertThat(result)
                .hasSize(SIZE_HINT)
                .doesNotContainNull()
                .hasOnlyElementsOfType(Item.class)
                .allSatisfy(item -> assertThat(item.getValue()).isNotBlank());

        assertThat(result[POPULATED_INDEX].getValue()).isEqualTo(ORIGINAL_VALUE);
    }

    @EnumSource(value = PopulateAction.class, mode = EnumSource.Mode.INCLUDE, names = {"NONE", "APPLY_SELECTORS"})
    @ParameterizedTest
    @DisplayName("Engine should not populate null elements in the array; populated index should retain original value")
    void customArrayGeneratorWithApplySelectorsAction(final PopulateAction populateAction) {
        final ItemInterface<String>[] result = Instancio.of(new TypeToken<ItemInterface<String>[]>() {})
                .supply(types().of(ItemInterface[].class), new ArrayGenerator(populateAction))
                .subtype(all(ItemInterface.class), Item.class)
                .create();

        assertThat(result[POPULATED_INDEX].getValue()).isEqualTo(ORIGINAL_VALUE);

        for (int i = 0; i < result.length; i++) {
            if (i != POPULATED_INDEX) {
                assertThat(result[i]).isNull();
            }
        }
    }
}
