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
import org.instancio.generator.DataStructureHint;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.PopulateAction;
import org.instancio.test.support.pojo.collections.lists.ListLong;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.types;

@FeatureTag({
        Feature.COLLECTION_GENERATOR_SUBTYPE,
        Feature.COLLECTION_GENERATOR_SIZE,
        Feature.COLLECTION_GENERATOR_WITH_ELEMENTS,
        Feature.GENERATE,
        Feature.GENERATOR,
        Feature.POPULATE_ACTION
})
class CustomCollectionGeneratorTest {

    private static final int SIZE_HINT = 3;
    private static final long EXPECTED_ELEMENT = -12345L;

    static class CustomList<T> extends ArrayList<T> {}

    private static class CustomListGenerator implements Generator<CustomList<Long>> {

        @Override
        public CustomList<Long> generate(final Random random) {
            final CustomList<Long> list = new CustomList<>();
            list.add(EXPECTED_ELEMENT);
            return list;
        }

        @Override
        public Hints hints() {
            return Hints.builder()
                    .populateAction(PopulateAction.ALL)
                    .hint(DataStructureHint.builder()
                            .dataStructureSize(SIZE_HINT)
                            .build())
                    .build();
        }
    }

    @Test
    @DisplayName("Engine should use provided collection instance and add new elements")
    void customCollectionGenerator() {
        final ListLong result = Instancio.of(ListLong.class)
                .supply(types().of(List.class), new CustomListGenerator())
                .create();

        assertThat(result.getList())
                .isExactlyInstanceOf(CustomList.class)
                .doesNotContainNull()
                .contains(EXPECTED_ELEMENT)
                // +1 for the additional element added in the generator
                .hasSize(SIZE_HINT + 1);
    }

    @Test
    void generateWithCustomCollectionSubtype() {
        final ListLong result = Instancio.of(ListLong.class)
                .generate(types().of(List.class), gen -> gen.collection()
                        .size(SIZE_HINT)
                        .with(EXPECTED_ELEMENT)
                        .subtype(CustomList.class))
                .create();

        assertThat(result.getList())
                .isExactlyInstanceOf(CustomList.class)
                .doesNotContainNull()
                .contains(EXPECTED_ELEMENT)
                // +1 for the additional element added in the generator
                .hasSize(SIZE_HINT + 1);
    }

    @Test
    @DisplayName("APPLY_SELECTORS: engine should not populate collection")
    void generateWithPopulateActionApplySelectors() {
        final ListLong result = Instancio.of(ListLong.class)
                .supply(types().of(List.class), new CustomListGenerator() {
                    @Override
                    public Hints hints() {
                        return Hints.defaultHints();
                    }
                })
                .create();

        assertThat(result.getList())
                .as("Should contain only the element added in the custom generator")
                .containsOnly(EXPECTED_ELEMENT);
    }
}
