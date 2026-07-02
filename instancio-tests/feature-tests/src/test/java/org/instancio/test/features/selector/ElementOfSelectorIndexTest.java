/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.test.features.selector;

import org.apache.commons.lang3.StringUtils;
import org.instancio.ElementOfSelector;
import org.instancio.IndexedElementSelector;
import org.instancio.Instancio;
import org.instancio.Scope;
import org.instancio.Select;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.misc.AbcArrayHolder;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.Parameter;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;

import static org.instancio.Select.allStrings;
import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.scope;
import static org.instancio.Select.types;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;

@SuppressWarnings("NullAway")
@ParameterizedClass
@FieldSource("CONTAINERS")
@FeatureTag({Feature.ELEMENT_OF_SELECTOR, Feature.SELECTOR})
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorIndexTest {

    private static final int SIZE = 5;
    private static final int LAST_INDEX = SIZE - 1;
    private static final String EXPECTED_STRING = "_value_";
    private static final StringsAbc EXPECTED_ABC = StringsAbc.builder()
            .a("_a_")
            .b("_b_")
            .c("_c_")
            .build();

    private static final List<Arguments> CONTAINERS = List.of(
            Arguments.of(AbcListHolder.class, Select.elementOf(AbcListHolder::getAbcElements1)),
            Arguments.of(AbcArrayHolder.class, Select.elementOf(AbcArrayHolder::getAbcElements1)));

    @Parameter(0)
    private Class<?> rootClass;

    @Parameter(1)
    private ElementOfSelector containerSelector;

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.ARRAY_MIN_SIZE, SIZE)
            .set(Keys.ARRAY_MAX_SIZE, SIZE)
            .set(Keys.COLLECTION_MIN_SIZE, SIZE)
            .set(Keys.COLLECTION_MAX_SIZE, SIZE);

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class WholeElement {

        private final List<TargetSelector> firstElement = List.of(
                containerSelector.first(),
                containerSelector.at(0),
                containerSelector.at(0, 0),
                containerSelector.range(0, 0),
                containerSelector.except(1, 2, 3, 4)
        );

        @FieldSource("firstElement")
        @ParameterizedTest
        void first(final TargetSelector selector) {
            final var result = Instancio.of(rootClass)
                    .set(selector, EXPECTED_ABC)
                    .create();

            final String subtree = "abcElements1[0]";

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, subtree);
        }

        private final List<TargetSelector> lastElement = List.of(
                containerSelector.last(),
                containerSelector.at(LAST_INDEX),
                containerSelector.at(LAST_INDEX, LAST_INDEX),
                containerSelector.range(LAST_INDEX, LAST_INDEX),
                containerSelector.except(0, 1, 2, 3)
        );

        @FieldSource("lastElement")
        @ParameterizedTest
        void last(final TargetSelector selector) {
            final var result = Instancio.of(rootClass)
                    .set(selector, EXPECTED_ABC)
                    .create();

            final String subtree = "abcElements1[%s]".formatted(LAST_INDEX);

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, subtree);
        }

        private final List<TargetSelector> allElements = List.of(
                containerSelector,
                containerSelector.at(0, 1, 2, 3, 4),
                containerSelector.range(0, 4)
        );

        @FieldSource("allElements")
        @ParameterizedTest
        void all(final TargetSelector selector) {
            final var result = Instancio.of(rootClass)
                    .set(selector, EXPECTED_ABC)
                    .create();

            final String subtree = "abcElements1";

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, subtree);
        }

        private final List<TargetSelector> middleSlice = List.of(
                containerSelector.at(1, 2, 3),
                containerSelector.range(1, 3),
                containerSelector.except(0, 4)
        );

        @FieldSource("middleSlice")
        @ParameterizedTest
        void middleSlice(final TargetSelector selector) {
            final var result = Instancio.of(rootClass)
                    .set(selector, EXPECTED_ABC)
                    .create();

            final String subtree = "abcElements1[1,2,3]";

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, subtree);
        }

        private final List<TargetSelector> indexGreaterThanSize = List.of(
                containerSelector.at(10, 11, 12, 13, 14, 15),
                containerSelector.range(10, 15),
                Select.all(
                        containerSelector.at(10),
                        containerSelector.range(11, 14),
                        containerSelector.at(15))
        );

        @FieldSource("indexGreaterThanSize")
        @ParameterizedTest
        void indexExceedsSettingsSize(final TargetSelector selector) {
            final var result = Instancio.of(rootClass)
                    .set(selector, EXPECTED_ABC)
                    .create();

            final String subtree = "abcElements1[10-15]";

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, subtree);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ElementField {

        private final List<TargetSelector> firstElement = List.of(
                containerSelector.first().field(StringsDef::getE),
                containerSelector.at(0).field(StringsDef::getE),
                containerSelector.at(0, 0).field(StringsDef::getE),
                containerSelector.range(0, 0).field(StringsDef::getE),
                containerSelector.except(1, 2, 3, 4).field(StringsDef::getE)
        );

        @FieldSource("firstElement")
        @ParameterizedTest
        void first(final TargetSelector selector) {
            final var result = Instancio.of(rootClass)
                    .set(selector, EXPECTED_STRING)
                    .create();

            final String subtree = "abcElements1[0].def.e";

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, subtree);
        }

        private final List<TargetSelector> lastElement = List.of(
                containerSelector.last().field(StringsDef::getE),
                containerSelector.at(LAST_INDEX).field(StringsDef::getE),
                containerSelector.at(LAST_INDEX, LAST_INDEX).field(StringsDef::getE),
                containerSelector.range(LAST_INDEX, LAST_INDEX).field(StringsDef::getE),
                containerSelector.except(0, 1, 2, 3).field(StringsDef::getE)
        );

        @FieldSource("lastElement")
        @ParameterizedTest
        void last(final TargetSelector selector) {
            final var result = Instancio.of(rootClass)
                    .set(selector, EXPECTED_STRING)
                    .create();

            final String subtree = "abcElements1[%s].def.e".formatted(LAST_INDEX);

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, subtree);
        }

        private final List<TargetSelector> allElements = List.of(
                containerSelector.field(StringsDef::getE),
                containerSelector.at(0, 1, 2, 3, 4).field(StringsDef::getE),
                containerSelector.range(0, 4).field(StringsDef::getE)
        );

        @FieldSource("allElements")
        @ParameterizedTest
        void all(final TargetSelector selector) {
            final var result = Instancio.of(rootClass)
                    .set(selector, EXPECTED_STRING)
                    .create();

            final String subtree = "abcElements1[*].def.e";

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, subtree);
        }

        private final List<TargetSelector> middleSlice = List.of(
                containerSelector.at(1, 2, 3).field(StringsDef::getE),
                containerSelector.range(1, 3).field(StringsDef::getE),
                containerSelector.except(0, 4).field(StringsDef::getE)
        );

        @FieldSource("middleSlice")
        @ParameterizedTest
        void middleSlice(final TargetSelector selector) {
            final var result = Instancio.of(rootClass)
                    .set(selector, EXPECTED_STRING)
                    .create();

            final String subtree = "abcElements1[1,2,3].def.e";

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, subtree);
        }


        private final List<TargetSelector> indexGreaterThanSize = List.of(
                containerSelector.at(10, 11, 12, 13, 14, 15).field(StringsDef::getE),
                containerSelector.range(10, 15).field(StringsDef::getE),
                Select.all(
                        containerSelector.at(10).field(StringsDef::getE),
                        containerSelector.range(11, 14).field(StringsDef::getE),
                        containerSelector.at(15).field(StringsDef::getE))
        );

        @FieldSource("indexGreaterThanSize")
        @ParameterizedTest
        void indexExceedsSettingsSize(final TargetSelector selector) {
            final var result = Instancio.of(rootClass)
                    .set(selector, EXPECTED_STRING)
                    .create();

            final String subtree = "abcElements1[10-15].def.e";

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, subtree);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ElementTarget {

        private final IndexedElementSelector indexSelector = containerSelector.at(1, 3);

        private final List<TargetSelector> selectors = List.of(
                indexSelector.target(field(StringsDef::getE)),
                indexSelector.target(field(StringsDef::getE).within(scope(StringsDef.class))),
                indexSelector.target(fields().named("e")),
                indexSelector.target(allStrings().within(scope(StringsDef::getE))),
                // Depth: 0:root -> 1:abcElements1 -> 2:abc -> 3:def -> 4:e
                indexSelector.target(fields().named("e").atDepth(4))
        );

        @FieldSource("selectors")
        @ParameterizedTest
        void target(final TargetSelector selector) {
            final var result = Instancio.of(rootClass)
                    .set(selector, EXPECTED_STRING)
                    .create();

            final String subtree = "abcElements1[1,3].def.e";

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, subtree);
        }
    }

    @Test
    void nestedFields_withDifferentIndicesAndValues() {
        final int[] indices = {1, 3, 9};

        final IndexedElementSelector elementSelector = elementOf(field("abcElements1")).at(indices);

        final Scope nestedScope = types(t -> t.getSimpleName().contains("Nested")).toScope();
        final IndexedElementSelector nestedElementSelector = elementOf(
                fields().named("abcElements2").within(nestedScope))
                .at(indices);

        final var result = Instancio.of(rootClass)
                .set(elementSelector.field(StringsAbc::getA), "_abcElements1.a_")
                .set(elementSelector.field(StringsAbc::getB), "_abcElements1.b_")
                .set(elementSelector.field(StringsAbc::getC), "_abcElements1.c_")
                .set(elementSelector.field(StringsDef::getD), "_abcElements1.d_")
                .set(elementSelector.field(StringsDef::getE), "_abcElements1.e_")
                .set(elementSelector.field(StringsDef::getF), "_abcElements1.f_")
                .set(elementSelector.field(StringsGhi::getG), "_abcElements1.g_")
                .set(elementSelector.field(StringsGhi::getH), "_abcElements1.h_")
                .set(elementSelector.field(StringsGhi::getI), "_abcElements1.i_")
                .set(nestedElementSelector.field(StringsAbc::getA), "_abcElements2.a_")
                .set(nestedElementSelector.field(StringsAbc::getB), "_abcElements2.b_")
                .set(nestedElementSelector.field(StringsAbc::getC), "_abcElements2.c_")
                .set(nestedElementSelector.field(StringsDef::getD), "_abcElements2.d_")
                .set(nestedElementSelector.field(StringsDef::getE), "_abcElements2.e_")
                .set(nestedElementSelector.field(StringsDef::getF), "_abcElements2.f_")
                .set(nestedElementSelector.field(StringsGhi::getG), "_abcElements2.g_")
                .set(nestedElementSelector.field(StringsGhi::getH), "_abcElements2.h_")
                .set(nestedElementSelector.field(StringsGhi::getI), "_abcElements2.i_")
                .create();

        final String indicesStr = StringUtils.join(indices, ',');
        assertThatGraph(result)
                .hasValuesEqualToExactlyIn("_abcElements1.a_", "abcElements1[%s].a".formatted(indicesStr))
                .hasValuesEqualToExactlyIn("_abcElements1.b_", "abcElements1[%s].b".formatted(indicesStr))
                .hasValuesEqualToExactlyIn("_abcElements1.c_", "abcElements1[%s].c".formatted(indicesStr))
                .hasValuesEqualToExactlyIn("_abcElements1.d_", "abcElements1[%s].def.d".formatted(indicesStr))
                .hasValuesEqualToExactlyIn("_abcElements1.e_", "abcElements1[%s].def.e".formatted(indicesStr))
                .hasValuesEqualToExactlyIn("_abcElements1.f_", "abcElements1[%s].def.f".formatted(indicesStr))
                .hasValuesEqualToExactlyIn("_abcElements1.g_", "abcElements1[%s].def.ghi.g".formatted(indicesStr))
                .hasValuesEqualToExactlyIn("_abcElements1.h_", "abcElements1[%s].def.ghi.h".formatted(indicesStr))
                .hasValuesEqualToExactlyIn("_abcElements1.i_", "abcElements1[%s].def.ghi.i".formatted(indicesStr))
                // nested
                .hasValuesEqualToExactlyIn("_abcElements2.a_", "nested.abcElements2[%s].a".formatted(indicesStr))
                .hasValuesEqualToExactlyIn("_abcElements2.b_", "nested.abcElements2[%s].b".formatted(indicesStr))
                .hasValuesEqualToExactlyIn("_abcElements2.c_", "nested.abcElements2[%s].c".formatted(indicesStr))
                .hasValuesEqualToExactlyIn("_abcElements2.d_", "nested.abcElements2[%s].def.d".formatted(indicesStr))
                .hasValuesEqualToExactlyIn("_abcElements2.e_", "nested.abcElements2[%s].def.e".formatted(indicesStr))
                .hasValuesEqualToExactlyIn("_abcElements2.f_", "nested.abcElements2[%s].def.f".formatted(indicesStr))
                .hasValuesEqualToExactlyIn("_abcElements2.g_", "nested.abcElements2[%s].def.ghi.g".formatted(indicesStr))
                .hasValuesEqualToExactlyIn("_abcElements2.h_", "nested.abcElements2[%s].def.ghi.h".formatted(indicesStr))
                .hasValuesEqualToExactlyIn("_abcElements2.i_", "nested.abcElements2[%s].def.ghi.i".formatted(indicesStr));
    }
}
