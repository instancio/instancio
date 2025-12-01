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
package org.instancio.test.features.generator.custom;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.generics.basic.Pair;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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
        Feature.AFTER_GENERATE,
        Feature.SELECTOR
})
@ExtendWith(InstancioExtension.class)
class CustomGeneratorWithDataStructuresTest {

    private static final int INITIAL_BLANK_ARRAY_SIZE = 3;

    private static class Container {
        List<String> list;
        Map<String, Long> map;
        Pair<String, Long>[] array;

        List<String> blankList = new ArrayList<>();
        Map<String, Long> blankMap = new HashMap<>();
        Pair<String, Long>[] blankArray = new Pair[INITIAL_BLANK_ARRAY_SIZE];

        public void setList(final List<String> list) {
            this.list = list;
        }

        public void setMap(final Map<String, Long> map) {
            this.map = map;
        }

        public void setArray(final Pair<String, Long>[] array) {
            this.array = array;
        }

        public void setBlankList(final List<String> blankList) {
            this.blankList = blankList;
        }

        public void setBlankMap(final Map<String, Long> blankMap) {
            this.blankMap = blankMap;
        }

        public void setBlankArray(final Pair<String, Long>[] blankArray) {
            this.blankArray = blankArray;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }

    private static Generator<Container> generator(final AfterGenerate afterGenerate) {
        return new Generator<>() {
            @Override
            public Container generate(final Random random) {
                return new Container();
            }

            @Override
            public Hints hints() {
                return Hints.builder().afterGenerate(afterGenerate).build();
            }
        };
    }

    @Nested
    class WithoutSelectorsTest {
        private Container create(final Generator<?> generator) {
            return Instancio.of(Container.class)
                    .supply(all(Container.class), generator)
                    .create();
        }

        @Test
        @DisplayName("DO_NOT_MODIFY: object created by the generator is not modified")
        void doNotModify() {
            final Container result = create(generator(AfterGenerate.DO_NOT_MODIFY));

            assertThat(result.list).isNull();
            assertThat(result.map).isNull();
            assertThat(result.array).isNull();

            assertThat(result.blankList).isEmpty();
            assertThat(result.blankMap).isEmpty();
            assertThat(result.blankArray).hasSize(INITIAL_BLANK_ARRAY_SIZE);
        }

        @Test
        @DisplayName("APPLY_SELECTORS: object created by the generator is not modified")
        void applySelectors() {
            final Container result = create(generator(AfterGenerate.APPLY_SELECTORS));

            assertThat(result.list).isNull();
            assertThat(result.map).isNull();
            assertThat(result.array).isNull();

            assertThat(result.blankList).isEmpty();
            assertThat(result.blankMap).isEmpty();
            assertThat(result.blankArray).hasSize(INITIAL_BLANK_ARRAY_SIZE);
        }

        @Test
        @DisplayName("POPULATE_NULLS only null fields should be populated; non-null fields should not be modified")
        void populateNulls() {
            final Container result = create(generator(AfterGenerate.POPULATE_NULLS));

            assertThatObject(result.list).isFullyPopulated();
            assertThatObject(result.map).isFullyPopulated();
            assertThatObject(result.array).isFullyPopulated();

            assertThat(result.blankList).isEmpty();
            assertThat(result.blankMap).isEmpty();
            assertThat(result.blankArray).hasSize(INITIAL_BLANK_ARRAY_SIZE).containsOnlyNulls();
        }

        @Test
        @DisplayName("POPULATE_NULLS_AND_DEFAULT_PRIMITIVES: only null fields and primitives with default values " +
                "should be populated; non-null fields and non-default primitives should not be modified")
        void populateNullsAndDefaultPrimitives() {
            final Container result = create(generator(AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES));

            assertThatObject(result.list).isFullyPopulated();
            assertThatObject(result.map).isFullyPopulated();
            assertThatObject(result.array).isFullyPopulated();

            assertThat(result.blankList).isEmpty();
            assertThat(result.blankMap).isEmpty();
            assertThat(result.blankArray).hasSize(INITIAL_BLANK_ARRAY_SIZE).containsOnlyNulls();
        }

        @Test
        @DisplayName("POPULATE_ALL: all fields will be populated; all fields will be overwritten")
        void populateAll() {
            final Container result = create(generator(AfterGenerate.POPULATE_ALL));

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
    class WithSelectorsTest {

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
        @DisplayName("DO_NOT_MODIFY: object created by the generator cannot be customised")
        void doNotModify() {
            final Container result = create(generator(AfterGenerate.DO_NOT_MODIFY));

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
        @DisplayName("APPLY_SELECTORS: object created by the generator can be customised")
        void applySelectors() {
            final Container result = create(generator(AfterGenerate.APPLY_SELECTORS));

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
        @DisplayName("POPULATE_NULLS object created by the generator can be customised")
        void populateNulls() {
            final Container result = create(generator(AfterGenerate.POPULATE_NULLS));

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
        @DisplayName("POPULATE_NULLS_AND_DEFAULT_PRIMITIVES: object created by the generator can be customised")
        void populateNullsAndDefaultPrimitives() {
            final Container result = create(generator(AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES));

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
        @DisplayName("POPULATE_ALL: object created by the generator can be customised")
        void populateAll() {
            final Container result = create(generator(AfterGenerate.POPULATE_ALL));

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
