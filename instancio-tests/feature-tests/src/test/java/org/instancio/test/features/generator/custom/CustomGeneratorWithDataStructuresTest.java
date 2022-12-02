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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.instancio.Generator;
import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.generator.DataStructureHint;
import org.instancio.generator.Hints;
import org.instancio.generator.PopulateAction;
import org.instancio.test.support.pojo.generics.basic.Pair;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({
        Feature.GENERATE,
        Feature.GENERATOR,
        Feature.ON_COMPLETE,
        Feature.POPULATE_ACTION,
        Feature.SELECTOR
})
class CustomGeneratorWithDataStructuresTest {

    private static final int INITIAL_BLANK_ARRAY_SIZE = 3;

    private static class Container {
        List<String> list;
        Map<String, Long> map;
        Pair<String, Long>[] array;

        List<String> blankList = new ArrayList<>();
        Map<String, Long> blankMap = new HashMap<>();
        Pair<String, Long>[] blankArray = new Pair[INITIAL_BLANK_ARRAY_SIZE];

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }

    private static class ContainerGenerator implements Generator<Container> {

        private final PopulateAction populateAction;

        private ContainerGenerator(final PopulateAction populateAction) {
            this.populateAction = populateAction;
        }

        @Override
        public Container generate(final Random random) {
            return new Container();
        }

        @Override
        public Hints hints() {
            return Hints.builder()
                    .populateAction(populateAction)
                    .hint(DataStructureHint.builder()
                            .dataStructureSize(INITIAL_BLANK_ARRAY_SIZE)
                            .build())
                    .build();
        }
    }

    private final Generator<?> generatorActionApplySelectors = new ContainerGenerator(PopulateAction.APPLY_SELECTORS);
    private final Generator<?> generatorActionPopulateNone = new ContainerGenerator(PopulateAction.NONE);
    private final Generator<?> generatorActionPopulateNulls = new ContainerGenerator(PopulateAction.NULLS);
    private final Generator<?> generatorActionPopulateNullsAndPrimitives = new ContainerGenerator(PopulateAction.NULLS_AND_DEFAULT_PRIMITIVES);
    private final Generator<?> generatorActionPopulateAll = new ContainerGenerator(PopulateAction.ALL);

    @Nested
    class PopulateActionTest {
        private Container create(final Generator<?> generator) {
            return Instancio.of(Container.class)
                    .supply(all(Container.class), generator)
                    .create();
        }

        @Test
        @DisplayName("Action NONE: object created by the generator is not modified")
        void noneAction() {
            final Container result = create(generatorActionApplySelectors);

            assertThat(generatorActionPopulateNone.hints().populateAction())
                    .isEqualTo(PopulateAction.NONE);

            assertThat(result.list).isNull();
            assertThat(result.map).isNull();
            assertThat(result.array).isNull();

            assertThat(result.blankList).isEmpty();
            assertThat(result.blankMap).isEmpty();
            assertThat(result.blankArray).hasSize(INITIAL_BLANK_ARRAY_SIZE);
        }

        @Test
        @DisplayName("Action APPLY_SELECTORS: object created by the generator is not modified")
        void applySelectorsAction() {
            final Container result = create(generatorActionApplySelectors);

            assertThat(generatorActionApplySelectors.hints().populateAction())
                    .isEqualTo(PopulateAction.APPLY_SELECTORS);

            assertThat(result.list).isNull();
            assertThat(result.map).isNull();
            assertThat(result.array).isNull();

            assertThat(result.blankList).isEmpty();
            assertThat(result.blankMap).isEmpty();
            assertThat(result.blankArray).hasSize(INITIAL_BLANK_ARRAY_SIZE);
        }

        @Test
        @DisplayName("Action NULLS: only null fields should be populated; non-null fields should not be modified")
        void populateNullsAction() {
            final Container result = create(generatorActionPopulateNulls);

            assertThat(generatorActionPopulateNulls.hints().populateAction())
                    .isEqualTo(PopulateAction.NULLS);

            assertThatObject(result.list).isFullyPopulated();
            assertThatObject(result.map).isFullyPopulated();
            assertThatObject(result.array).isFullyPopulated();

            assertThat(result.blankList).isEmpty();
            assertThat(result.blankMap).isEmpty();
            assertThat(result.blankArray).hasSize(INITIAL_BLANK_ARRAY_SIZE).containsOnlyNulls();
        }

        @Test
        @DisplayName("Action NULLS_AND_PRIMITIVES: only null fields and primitives with default values " +
                "should be populated; non-null fields and non-default primitives should not be modified")
        void populateNullsAndPrimitivesAction() {
            final Container result = create(generatorActionPopulateNullsAndPrimitives);

            assertThat(generatorActionPopulateNullsAndPrimitives.hints().populateAction())
                    .isEqualTo(PopulateAction.NULLS_AND_DEFAULT_PRIMITIVES);

            assertThatObject(result.list).isFullyPopulated();
            assertThatObject(result.map).isFullyPopulated();
            assertThatObject(result.array).isFullyPopulated();

            assertThat(result.blankList).isEmpty();
            assertThat(result.blankMap).isEmpty();
            assertThat(result.blankArray).hasSize(INITIAL_BLANK_ARRAY_SIZE).containsOnlyNulls();
        }

        @Test
        @DisplayName("Action POPULATE: all fields will be populated; all fields will be overwritten")
        void populateAction() {
            final Container result = create(generatorActionPopulateAll);

            assertThat(generatorActionPopulateAll.hints().populateAction())
                    .isEqualTo(PopulateAction.ALL);

            assertThatObject(result.list).isFullyPopulated();
            assertThatObject(result.map).isFullyPopulated();
            assertThatObject(result.array).isFullyPopulated();

            assertThatObject(result.blankList).isFullyPopulated();
            assertThatObject(result.blankMap).isFullyPopulated();
            assertThatObject(result.blankArray).isFullyPopulated();
        }
    }

    /**
     * Customising objects returned by custom generator using generate()/onComplete().
     */
    @Nested
    class CustomisingObjectsReturnedByGeneratorTest {

        // Anything bigger than default to ensure tests don't pass by luck
        private final int NEW_SIZE = 15;

        private Container create(final Generator<?> generator) {
            return Instancio.of(Container.class)
                    .supply(all(Container.class), generator)
                    .generate(field("list"), gen -> gen.collection().size(NEW_SIZE))
                    .generate(field("blankList"), gen -> gen.collection().size(NEW_SIZE))
                    .generate(field("array"), gen -> gen.array().length(NEW_SIZE))
                    .generate(field("blankArray"), gen -> gen.array().length(NEW_SIZE))
                    .onComplete(all(Container.class), (Container result) -> {
                        if (result.map != null) {
                            result.map.put("foo", 1L);
                        }
                        if (result.blankMap != null) {
                            result.blankMap.put("foo", 1L);
                        }
                    })
                    .lenient()
                    .create();
        }

        @Test
        @DisplayName("Action NONE: object created by the generator cannot be customised")
        void noneAction() {
            final Container result = create(generatorActionPopulateNone);

            assertThat(result.list).isNull();
            assertThat(result.map).isNull();
            assertThat(result.array).isNull();

            assertThat(result.blankList).isEmpty();
            assertThat(result.blankMap)
                    .as("onComplete should still be called")
                    .hasSize(1).containsKey("foo");
            assertThat(result.blankArray).hasSize(INITIAL_BLANK_ARRAY_SIZE);
        }

        @Test
        @DisplayName("Action APPLY_SELECTORS: object created by the generator can be customised")
        void applySelectorsAction() {
            final Container result = create(generatorActionApplySelectors);

            assertThat(result.list).hasSize(NEW_SIZE);
            assertThat(result.map).as("since no selectors were applied").isNull();
            assertThat(result.array).hasSize(NEW_SIZE);

            assertThat(result.blankList).hasSize(NEW_SIZE);
            assertThat(result.blankMap)
                    .as("onComplete should still be called")
                    .hasSize(1).containsKey("foo");
            assertThat(result.blankArray).hasSize(NEW_SIZE);
        }

        @Test
        @DisplayName("Action NULLS: object created by the generator can be customised")
        void populateNullsAction() {
            final Container result = create(generatorActionPopulateNulls);

            assertThat(result.list).hasSize(NEW_SIZE);
            assertThat(result.map)
                    .as("Populated using built-in map generator")
                    .hasSizeLessThan(NEW_SIZE);
            assertThat(result.array).hasSize(NEW_SIZE);

            assertThat(result.blankList).hasSize(NEW_SIZE);
            assertThat(result.blankMap)
                    .as("onComplete should still be called")
                    .hasSize(1).containsKey("foo");
            assertThat(result.blankArray).hasSize(NEW_SIZE);
        }

        @Test
        @DisplayName("Action NULLS_AND_PRIMITIVES: object created by the generator can be customised")
        void populateNullsAndPrimitivesAction() {
            final Container result = create(generatorActionPopulateNullsAndPrimitives);

            assertThat(result.list).hasSize(NEW_SIZE);
            assertThat(result.map)
                    .as("Populated using built-in map generator")
                    .hasSizeLessThan(NEW_SIZE);
            assertThat(result.array).hasSize(NEW_SIZE);

            assertThat(result.blankList).hasSize(NEW_SIZE);
            assertThat(result.blankMap)
                    .as("onComplete should still be called")
                    .hasSize(1).containsKey("foo");
            assertThat(result.blankArray).hasSize(NEW_SIZE);
        }

        @Test
        @DisplayName("Action POPULATE: object created by the generator can be customised")
        void populateAction() {
            final Container result = create(generatorActionPopulateAll);

            assertThat(result.list).hasSize(NEW_SIZE);
            assertThat(result.map)
                    .as("Overwritten by generate()")
                    .hasSizeBetween(2, NEW_SIZE);
            assertThat(result.array).hasSize(NEW_SIZE);

            assertThat(result.blankList).hasSize(NEW_SIZE);
            assertThat(result.blankMap)
                    .as("Overwritten by generate(); onComplete should still be called")
                    .hasSizeBetween(2, NEW_SIZE)
                    .containsKey("foo");
            assertThat(result.blankArray).hasSize(NEW_SIZE);
        }
    }

}
