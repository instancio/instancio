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
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.hints.CollectionHint;
import org.instancio.test.support.pojo.collections.lists.ListLong;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allLongs;
import static org.instancio.Select.types;

@FeatureTag({
        Feature.COLLECTION_GENERATOR_SUBTYPE,
        Feature.COLLECTION_GENERATOR_SIZE,
        Feature.COLLECTION_GENERATOR_WITH_ELEMENTS,
        Feature.GENERATE,
        Feature.GENERATOR,
        Feature.AFTER_GENERATE
})
class CustomCollectionGeneratorTest {

    private static final long EXISTING_ELEMENT = -12345L;
    private static final int INITIAL_SIZE = 1;
    private static final int GENERATE_ELEMENTS = 3;

    static class CustomList<T> extends ArrayList<T> {}

    private static class CustomListGenerator implements Generator<CustomList<Long>> {
        private final Hints hints;

        private CustomListGenerator(final Hints hints) {
            this.hints = hints;
        }

        @Override
        public CustomList<Long> generate(final Random random) {
            final CustomList<Long> list = new CustomList<>();
            list.add(EXISTING_ELEMENT);
            return list;
        }

        @Override
        public Hints hints() {
            return hints;
        }
    }

    @ParameterizedTest
    @EnumSource(value = AfterGenerate.class, mode = EnumSource.Mode.EXCLUDE, names = "DO_NOT_MODIFY")
    @DisplayName("Should use collection instance from generator and generate elements")
    void customListSpecifiedAsSubtype(final AfterGenerate afterGenerate) {
        final Hints hints = Hints.builder().afterGenerate(afterGenerate)
                .with(CollectionHint.builder().generateElements(GENERATE_ELEMENTS).build())
                .build();

        final ListLong result = Instancio.of(ListLong.class)
                .supply(types().of(List.class), new CustomListGenerator(hints))
                .create();

        assertThat(result.getList())
                .isExactlyInstanceOf(CustomList.class)
                .doesNotContainNull()
                .contains(EXISTING_ELEMENT)
                .hasSize(INITIAL_SIZE + GENERATE_ELEMENTS);
    }

    @Test
    @DisplayName("Should generate list with custom subtype specified via gen.collection().subtype()")
    void customCollectionSubtypeSpecifiedViaBuiltInCollectionGenerator() {
        final ListLong result = Instancio.of(ListLong.class)
                .generate(types().of(List.class), gen -> gen.collection()
                        .size(GENERATE_ELEMENTS)
                        .with(EXISTING_ELEMENT)
                        .subtype(CustomList.class)) // subtype
                .create();

        assertThat(result.getList())
                .isExactlyInstanceOf(CustomList.class)
                .doesNotContainNull()
                .contains(EXISTING_ELEMENT)
                .hasSize(INITIAL_SIZE + GENERATE_ELEMENTS);
    }

    @Nested
    class WithAppliedSelector {

        @ParameterizedTest
        @EnumSource(value = AfterGenerate.class, mode = EnumSource.Mode.EXCLUDE, names = "DO_NOT_MODIFY")
        @DisplayName("Should generate specified number of elements and apply selector")
        void withAppliedSelector(final AfterGenerate afterGenerate) {
            final Hints hints = Hints.builder().afterGenerate(afterGenerate)
                    .with(CollectionHint.builder().generateElements(GENERATE_ELEMENTS).build())
                    .build();

            final ListLong result = Instancio.of(ListLong.class)
                    .supply(types().of(List.class), new CustomListGenerator(hints))
                    .generate(allLongs(), gen -> gen.longs().min(Long.MAX_VALUE))
                    .create();

            assertThat(result.getList())
                    .as("Should contain the element added in the custom generator + generated elements")
                    .isExactlyInstanceOf(CustomList.class)
                    .contains(EXISTING_ELEMENT, Long.MAX_VALUE)
                    .hasSize(INITIAL_SIZE + GENERATE_ELEMENTS);
        }

        @Test
        @DisplayName("DO_NOT_MODIFY: should generate elements and ignore matching selector on user-supplied values")
        void doNotModify() {
            final Hints hints = Hints.builder().afterGenerate(AfterGenerate.DO_NOT_MODIFY)
                    .with(CollectionHint.builder().generateElements(GENERATE_ELEMENTS).build())
                    .build();

            final ListLong result = Instancio.of(ListLong.class)
                    .supply(types().of(List.class), new CustomListGenerator(hints))
                    .generate(allLongs(), gen -> gen.longs().min(Long.MAX_VALUE))
                    .create();


            assertThat(result.getList())
                    .as("Existing element should be retained")
                    .contains(EXISTING_ELEMENT, Long.MAX_VALUE)
                    .hasSize(INITIAL_SIZE + GENERATE_ELEMENTS);
        }
    }
}
