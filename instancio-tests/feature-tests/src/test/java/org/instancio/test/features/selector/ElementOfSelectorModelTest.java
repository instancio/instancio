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

import org.instancio.Assign;
import org.instancio.IndexedElementSelector;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.InstancioCollectionsApi;
import org.instancio.InstancioObjectApi;
import org.instancio.Model;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.misc.AbcArrayHolder;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.misc.AbcListHolder.Nested;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.root;
import static org.instancio.Select.scope;
import static org.instancio.Select.types;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;

@FeatureTag({Feature.ELEMENT_OF_SELECTOR, Feature.MODEL, Feature.SET_MODEL})
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorModelTest {

    private static final int SIZE = 5;
    private static final int LAST_INDEX = SIZE - 1;
    private static final String EXPECTED_STRING = "_value_";
    private static final String EXPECTED_OVERRIDE_STRING = "_override_";

    private static final StringsAbc EXPECTED_ABC = StringsAbc.builder().a("_a_").build();
    private static final StringsAbc EXPECTED_OVERRIDE_ABC = StringsAbc.builder().a("_a-override_").build();

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.COLLECTION_MIN_SIZE, SIZE)
            .set(Keys.COLLECTION_MAX_SIZE, SIZE)
            .set(Keys.ARRAY_MIN_SIZE, SIZE)
            .set(Keys.ARRAY_MAX_SIZE, SIZE)
            .set(Keys.MAP_MIN_SIZE, SIZE)
            .set(Keys.MAP_MAX_SIZE, SIZE);

    private record TwoNested(Nested left, Nested right) {}

    private record ListOfLists(List<List<StringsAbc>> outer) {}

    private record DeepLists(List<List<List<StringsAbc>>> levels) {}

    private record MapOfLists(Map<String, List<StringsAbc>> byKey) {}

    private record Dept(List<StringsAbc> members) {}

    private record Team(List<Dept> depts) {}

    private record ArrayOfArrays(StringsAbc[][] grid) {}


    @org.junit.jupiter.api.Nested
    class CreateFromModel {

        @Test
        void elementOfFromModel() {
            final Model<AbcListHolder> model = Instancio.of(AbcListHolder.class)
                    .set(elementOf(AbcListHolder::getAbcElements1).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.create(model);

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[0].a");
        }

        @Test
        void withCollectionWidening() {
            final int expectedSize = 6;
            final int lastIndex = expectedSize - 1;

            final Model<AbcListHolder> model = Instancio.of(AbcListHolder.class)
                    .set(elementOf(AbcListHolder::getAbcElements1).at(lastIndex).field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(model)
                    .withSetting(Keys.COLLECTION_MIN_SIZE, 1)
                    .withSetting(Keys.COLLECTION_MAX_SIZE, 1)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[%s].a".formatted(lastIndex));
        }
    }

    @org.junit.jupiter.api.Nested
    class Inheritance {

        @Test
        void multiLevelOverrides() {
            final Model<AbcListHolder> baseModel = Instancio.of(AbcListHolder.class)
                    .set(elementOf(AbcListHolder::getAbcElements1).at(1), EXPECTED_ABC)
                    .set(elementOf(AbcListHolder::getAbcElements1).at(2), EXPECTED_ABC)
                    .set(elementOf(AbcListHolder::getAbcElements1).at(3), EXPECTED_ABC)
                    .toModel();

            final Model<AbcListHolder> overrideModel = Instancio.of(baseModel)
                    .set(elementOf(AbcListHolder::getAbcElements1).at(2), EXPECTED_OVERRIDE_ABC)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(root(), overrideModel)
                    .set(elementOf(AbcListHolder::getAbcElements1).at(3), EXPECTED_OVERRIDE_ABC)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[1]");

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_OVERRIDE_ABC, "abcElements1[2,3]");
        }

        @Test
        void setModelAtRoot() {
            final Model<AbcListHolder> model = Instancio.of(AbcListHolder.class)
                    .set(elementOf(AbcListHolder::getAbcElements1).first(), EXPECTED_ABC)
                    .set(elementOf(AbcListHolder::getAbcElements1).last(), EXPECTED_ABC)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(root(), model)
                    .set(elementOf(AbcListHolder::getAbcElements1).last(), EXPECTED_OVERRIDE_ABC)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[0]");

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_OVERRIDE_ABC, "abcElements1[%s]".formatted(LAST_INDEX));
        }

        @Test
        void wholeElement() {
            final Model<StringsAbc> model = Instancio.of(StringsAbc.class)
                    .set(all(StringsAbc.class), EXPECTED_ABC)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(AbcListHolder::getAbcElements1).first(), model)
                    .set(elementOf(AbcListHolder::getAbcElements1).last(), EXPECTED_OVERRIDE_ABC)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[0]");

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_OVERRIDE_ABC, "abcElements1[%s]".formatted(LAST_INDEX));
        }

        @Test
        void elementField() {
            final Model<StringsAbc> model = Instancio.of(StringsAbc.class)
                    .set(all(field(StringsDef::getD), field(StringsDef::getE)), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(AbcListHolder::getAbcElements1).first(), model)
                    .set(elementOf(AbcListHolder::getAbcElements1).first().field(StringsDef::getD),
                            EXPECTED_OVERRIDE_STRING)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_OVERRIDE_STRING, "abcElements1[0].def.d");

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[0].def.e");
        }
    }

    @org.junit.jupiter.api.Nested
    class SetListModel {

        @Test
        void firstElementField_toOneList() {
            final Model<List<StringsAbc>> model = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), model)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[0].a");
        }

        @Test
        void allElementsField_toOneList() {
            final Model<List<StringsAbc>> model = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), model)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[*].a");
        }

        @Test
        void indexedElementsField_toAllLists() {
            final int[] indices = {0, 1, 2, 9};

            final Model<List<StringsAbc>> model = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).at(indices).field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(all(List.class), model)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                            "**.abcElements1[0,1,2,9].a",
                            "**.abcElements2[0,1,2,9].a"
                    });
        }

        @Test
        void multipleIndicesPreserved() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).at(0, 2, 4).field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .size(field(AbcListHolder::getAbcElements1), SIZE)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            assertThat(result.getAbcElements1()).hasSize(SIZE);
            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[0,2,4].a");
        }

        @Test
        void wholeElementValue() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first(), EXPECTED_ABC)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[0]");
        }

        @Test
        void appliedWithSelectorScope() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(all(List.class).within(scope(Nested.class)), innerModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                            "nested.abcElements1[0].a",
                            "nested.abcElements2[0].a"
                    });
        }

        @Test
        void nestedElementFieldAtIndex() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).at(1).field(StringsDef::getE), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[1].def.e");
        }
    }

    @org.junit.jupiter.api.Nested
    class SetElementModel {

        @Test
        void atSelectedIndices() {
            final int[] indices = {0, 1, 2, 9};

            final Model<StringsAbc> model = Instancio.of(StringsAbc.class)
                    .set(field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(AbcListHolder::getAbcElements1).at(indices), model)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[0,1,2,9].a");
        }

        @Test
        void withMixedCustomisations() {
            final Model<StringsAbc> model = Instancio.of(StringsAbc.class)
                    .ignore(field(StringsDef::getD))
                    .set(field(StringsAbc::getA), EXPECTED_STRING)
                    .generate(field(StringsAbc::getB), gen -> gen.oneOf(EXPECTED_STRING))
                    .filter(field(StringsAbc::getC), (String s) -> !s.isEmpty())
                    .assign(Assign.valueOf(StringsAbc::getA).to(StringsDef::getF))
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(AbcListHolder::getAbcElements1).at(0, 8), model)
                    .setModel(elementOf(Nested::getAbcElements2).at(2, 7), model)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                    "abcElements1[0,8].a",
                    "abcElements1[0,8].b",
                    "abcElements1[0,8].def.f", // via assign
                    "nested.abcElements2[2,7].a",
                    "nested.abcElements2[2,7].b",
                    "nested.abcElements2[2,7].def.f" // via assign
            });

            assertThatGraph(result)
                    .includingSubtrees("abcElements1[0,8].def.d", "nested.abcElements2[2,7].def.d")
                    .hasAllValuesOfTypeEqualTo(String.class, null);
        }

        @Test
        void scopedContainer() {
            final Model<StringsAbc> model = Instancio.of(StringsAbc.class)
                    .set(field(StringsAbc::getA), EXPECTED_STRING)
                    .assign(Assign.valueOf(StringsAbc::getA).to(StringsDef::getF))
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(all(List.class).within(scope(Nested.class))).at(0), model)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                    "nested.abcElements1[0].a",
                    "nested.abcElements1[0].def.f", // via assign (f = a)
                    "nested.abcElements2[0].a",
                    "nested.abcElements2[0].def.f"  // via assign
            });
        }

        @Test
        void rootSelectorAppliesToWholeElement() {
            final Model<StringsAbc> innerModel = Instancio.of(StringsAbc.class)
                    .set(root(), EXPECTED_ABC)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(AbcListHolder::getAbcElements1).first(), innerModel)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[0]");
        }
    }

    @org.junit.jupiter.api.Nested
    class InnerElementOfWithFieldSelector {

        @Test
        void fieldCollectionSelector() {
            final Model<Nested> innerModel = Instancio.of(Nested.class)
                    .set(elementOf(field(Nested::getAbcElements1)).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getNested), innerModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "nested.abcElements1[0].a");
        }

        @Test
        void fieldCollectionSelector_indexPreserved() {
            final Model<Nested> innerModel = Instancio.of(Nested.class)
                    .set(elementOf(field(Nested::getAbcElements1)).at(2).field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getNested), innerModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "nested.abcElements1[2].a");
        }

        @Test
        void allListContainerSelector_keptAndScoped() {
            final Model<Nested> innerModel = Instancio.of(Nested.class)
                    .set(elementOf(all(List.class)).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getNested), innerModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                            "nested.abcElements1[0].a",
                            "nested.abcElements2[0].a"
                    });
        }

        @Test
        void appliesOnlyWithinScope() {
            record InnerContainer(Nested nested) {}
            record OuterContainer(Nested nested, InnerContainer innerContainer) {}

            final Model<Nested> nestedModel = Instancio.of(Nested.class)
                    .set(elementOf(field(Nested::getAbcElements1)).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final OuterContainer result = Instancio.of(OuterContainer.class)
                    .setModel(all(Nested.class).within(scope(InnerContainer.class)), nestedModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "innerContainer.nested.abcElements1[0].a");
        }
    }

    @org.junit.jupiter.api.Nested
    class CarriedThroughSetModel {

        @Test
        void lenient() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(field(StringsAbc::getA)).first().lenient(), new StringsAbc())
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            assertThat(result.getAbcElements1()).isNotEmpty();
        }

        @Test
        void assignment() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .assign(Assign.valueOf(elementOf(root()).first().field(StringsAbc::getA))
                            .to(elementOf(root()).last().field(StringsAbc::getA)))
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            final List<StringsAbc> list = result.getAbcElements1();
            assertThat(list).hasSizeGreaterThan(1);
            assertThat(list.get(list.size() - 1).getA()).isEqualTo(list.get(0).getA());
        }

        @Test
        void assignmentWithRegularOrigin() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .assign(Assign.valueOf(field(StringsAbc::getA))
                            .to(elementOf(root()).first().field(StringsAbc::getB)))
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .set(elementOf(AbcListHolder::getAbcElements1).first().field(StringsAbc::getA), "_foo_")
                    .set(elementOf(AbcListHolder.Nested::getAbcElements1).first().field(StringsAbc::getA), "_bar_")
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .setModel(field(AbcListHolder.Nested::getAbcElements1), innerModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn("_foo_", new String[]{
                            "abcElements1[0].a",
                            "abcElements1[0].b"
                    });

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn("_bar_", new String[]{
                            "nested.abcElements1[0].a",
                            "nested.abcElements1[0].b"
                    });
        }

        @Test
        void filter() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .generate(field(StringsAbc::getA), gen -> gen.oneOf("x", "y", "z"))
                    .filter(elementOf(root()).first().field(StringsAbc::getA), (String s) -> s.equals("y"))
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            assertThatGraph(result)
                    .includingSubtrees("abcElements1[*].a")
                    .allValuesOfTypeSatisfy(String.class, val -> assertThat(val).isIn("x", "y", "z"));

            // First is fixed to "y" via filter()
            assertThatGraph(result)
                    .includingPaths("abcElements1[0].a")
                    .hasAllValuesEqualTo("y");
        }

        @Test
        void nullable() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .withNullable(elementOf(root()).first().field(StringsAbc::getA))
                    .toModel();

            final int size = 100; // big enough for withNullable() to produce null

            final List<AbcListHolder> results = Instancio.ofList(AbcListHolder.class)
                    .size(size)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            assertThat(results)
                    .hasSize(size)
                    .map(it -> it.getAbcElements1().get(0))
                    .map(StringsAbc::getA)
                    .containsNull();

            assertThatGraph(results)
                    .excludingSubtrees("**.abcElements1[0].a")
                    .allValuesOfTypeSatisfy(String.class, val -> assertThat(val).isNotBlank());
        }

        @Test
        void ignoreViaDoublyNestedModel() {
            final Model<StringsAbc> innermost = Instancio.of(StringsAbc.class)
                    .ignore(field(StringsAbc::getA))
                    .ignore(field(StringsGhi::getI))
                    .toModel();

            final Model<List<StringsAbc>> middle = Instancio.ofList(StringsAbc.class)
                    .setModel(elementOf(root()).first(), innermost)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), middle)
                    .create();

            final String[] subtrees = {"abcElements1[0].a", "abcElements1[0].def.ghi.i"};

            assertThatGraph(result)
                    .includingSubtrees(subtrees)
                    .hasAllValuesOfTypeEqualTo(String.class, null);

            assertThatGraph(result)
                    .excludingSubtrees("abcElements1[0].a", "abcElements1[0].def.ghi.i")
                    .hasNoValueOfTypeEqualTo(String.class, null);
        }
    }

    @org.junit.jupiter.api.Nested
    class NestedSetModel {

        @Test
        void listModelInsideNestedModel() {
            final Model<List<StringsAbc>> listModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final Model<AbcListHolder.Nested> nestedModel = Instancio.of(Nested.class)
                    .setModel(field(AbcListHolder.Nested::getAbcElements1), listModel)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getNested), nestedModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "nested.abcElements1[0].a");
        }

        @RepeatedTest(10)
        void elementModel_appliedTo_listModel_atGivenIndex() {
            final int targetIndex = Instancio.gen().ints().range(0, SIZE - 1).get();

            final Model<StringsAbc> elementModel = Instancio.of(StringsAbc.class)
                    .assign(Assign.valueOf(field(StringsAbc::getA)).to(field(StringsAbc::getB)))
                    .toModel();

            final Model<List<StringsAbc>> listModel = Instancio.ofList(StringsAbc.class)
                    .setModel(elementOf(root()).at(targetIndex), elementModel)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), listModel)
                    .create();

            assertThat(result.getAbcElements1()).hasSize(SIZE);

            for (int i = 0; i < result.getAbcElements1().size(); i++) {
                final StringsAbc element = result.getAbcElements1().get(i);

                if (i == targetIndex) {
                    assertThat(element.getA()).isEqualTo(element.getB());
                } else {
                    assertThat(element.getA()).isNotEqualTo(element.getB());
                }
            }
        }
    }

    @org.junit.jupiter.api.Nested
    class StrictMode {

        @Test
        void reportsUnusedInnerElementOf() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(field(StringsAbc::getA)).first(), new StringsAbc())
                    .toModel();

            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel);

            assertThatThrownBy(api::create)
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("Unused selector");
        }

        @Test
        void reportsUnusedDecomposedIgnore() {
            final Model<StringsAbc> innermost = Instancio.of(StringsAbc.class)
                    .ignore(field(Nested::getAbcElements1))
                    .toModel();

            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(AbcListHolder::getAbcElements1).first(), innermost);

            assertThatThrownBy(api::create)
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("Unused selector");
        }

        @Test
        void scopeNeverPresent_reportedAsUnused() {
            record NoNested(List<StringsAbc> abcElements1) {}

            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final InstancioApi<NoNested> api = Instancio.of(NoNested.class)
                    .setModel(all(List.class).within(scope(Nested.class)), innerModel);

            assertThatThrownBy(api::create)
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("Unused selector");
        }
    }

    @org.junit.jupiter.api.Nested
    class IndexSpecsThroughSetModel {

        @Test
        void rangePreserved() {
            final Model<List<StringsAbc>> model = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).range(1, 3).field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), model)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[1-3].a");
        }

        @Test
        void exceptPreserved() {
            final Model<List<StringsAbc>> model = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).except(0, 4).field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), model)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[1,2,3].a");
        }
    }

    @org.junit.jupiter.api.Nested
    class RootSelectorOnElementModel {

        @Test
        void rootSet_atMultipleIndices() {
            final Model<StringsAbc> innerModel = Instancio.of(StringsAbc.class)
                    .set(root(), EXPECTED_ABC)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(AbcListHolder::getAbcElements1).at(0, 2, 4), innerModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[0,2,4]");
        }

        @Test
        void fieldSelector_scopedToTargetedElementOnly() {
            final Model<StringsAbc> innerModel = Instancio.of(StringsAbc.class)
                    .set(field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(AbcListHolder::getAbcElements1).first(), innerModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[0].a");
        }
    }

    @org.junit.jupiter.api.Nested
    class ApiMethodsThroughSetModel {

        @Test
        void supplySupplierOnElementField() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .supply(elementOf(root()).first().field(StringsAbc::getA), () -> EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[0].a");
        }

        @Test
        void setWholeElementMakesSubtreeReadOnly() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first(), StringsAbc.builder().a(EXPECTED_STRING).build())
                    .set(elementOf(root()).first().field(StringsAbc::getB).lenient(), EXPECTED_OVERRIDE_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[0].a");
            assertThatGraph(result)
                    .includingPaths("abcElements1[0].b")
                    .hasAllValuesOfTypeEqualTo(String.class, null);
        }

        @Test
        void supplyGeneratorWholeElementIsModifiable() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .supply(elementOf(root()).first(), random -> new StringsAbc())
                    .set(elementOf(root()).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[0].a");
            // Other fields are populated because the generated object is modifiable.
            assertThat(result.getAbcElements1().get(0).getB()).isNotBlank();
        }

        @Test
        void generateOnElementField() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .generate(elementOf(root()).field(StringsAbc::getA), gen -> gen.oneOf(EXPECTED_STRING))
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[*].a");
        }

        @Test
        void setNestedElementFieldViaTarget() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first().target(field(StringsGhi::getG)), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[0].def.ghi.g");
        }

        @Test
        void ignoreElementField() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .ignore(elementOf(root()).first().field(StringsAbc::getA))
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            assertThatGraph(result)
                    .includingSubtrees("abcElements1[0].a")
                    .hasAllValuesOfTypeEqualTo(String.class, null);
            assertThatGraph(result)
                    .excludingSubtrees("abcElements1[0].a")
                    .hasNoValueOfTypeEqualTo(String.class, null);
        }

        @Test
        void ignoreNestedElementFieldViaTarget() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .ignore(elementOf(root()).field(StringsAbc::getA))
                    .ignore(elementOf(root()).first().target(all(StringsGhi.class)))
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            assertThatGraph(result)
                    .includingPaths("abcElements1[0].def.ghi")
                    .hasAllValuesOfTypeEqualTo(StringsGhi.class, null);
            assertThatGraph(result)
                    .includingSubtrees("abcElements1[*].a")
                    .hasAllValuesOfTypeEqualTo(String.class, null);
        }

        @Test
        void uniqueElementField() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .generate(field(StringsAbc::getA), gen -> gen.oneOf("p", "q", "r", "s", "t"))
                    .withUnique(elementOf(root()).field(StringsAbc::getA))
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            final List<String> values = result.getAbcElements1().stream()
                    .map(StringsAbc::getA)
                    .toList();

            assertThat(values)
                    .hasSize(SIZE)
                    .doesNotHaveDuplicates();
        }

        @Test
        void onCompleteElementFieldMutates() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .onComplete(elementOf(root()).first(), (StringsAbc e) -> e.setA("modified"))
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn("modified", "abcElements1[0].a");
        }

        @Test
        void assignGivenConditional() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .assign(Assign.given(elementOf(root()).first().field(StringsAbc::getA))
                            .is(EXPECTED_STRING)
                            .set(elementOf(root()).first().field(StringsAbc::getB), EXPECTED_OVERRIDE_STRING))
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[0].a");
            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_OVERRIDE_STRING, "abcElements1[0].b");
        }
    }

    @org.junit.jupiter.api.Nested
    class WithSubtype {

        @Test
        void subtypeRejectsElementOfSelectorInModel() {
            final InstancioCollectionsApi<List<StringsAbc>> api = Instancio.ofList(StringsAbc.class);
            final IndexedElementSelector selector = elementOf(root()).first();

            assertThatThrownBy(() -> api.subtype(selector, StringsAbc.class))
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("subtype() does not support elementOf() selectors");
        }

        @Test
        void containerSubtypeComposesWithElementOfThroughSetModel() {
            final Model<AbcListHolder> innerModel = Instancio.of(AbcListHolder.class)
                    .subtype(field(AbcListHolder::getAbcElements1), java.util.LinkedList.class)
                    .set(elementOf(AbcListHolder::getAbcElements1).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(root(), innerModel)
                    .create();

            assertThat(result.getAbcElements1()).isInstanceOf(java.util.LinkedList.class);
            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[0].a");
        }

        @Test
        void elementOfFieldComposesWithRegularFieldSetInSameModel() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .set(field(StringsAbc::getB), EXPECTED_OVERRIDE_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[0].a");
            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_OVERRIDE_STRING, "abcElements1[*].b");
        }
    }

    @org.junit.jupiter.api.Nested
    class AssignmentOriginRebaking {

        @RepeatedTest(5)
        void assignFiresOnlyInTargetedFrame() {
            final int targetIndex = Instancio.gen().ints().range(0, LAST_INDEX).get();

            final Model<StringsAbc> elementModel = Instancio.of(StringsAbc.class)
                    .assign(Assign.valueOf(StringsAbc::getA).to(StringsAbc::getB))
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(AbcListHolder::getAbcElements1).at(targetIndex), elementModel)
                    .create();

            final List<StringsAbc> list = result.getAbcElements1();
            for (int i = 0; i < list.size(); i++) {
                final StringsAbc el = list.get(i);
                if (i == targetIndex) {
                    assertThat(el.getB()).isEqualTo(el.getA());
                } else {
                    assertThat(el.getB()).isNotEqualTo(el.getA());
                }
            }
        }

        @Test
        void assignAtMultipleIndices() {
            final Model<StringsAbc> elementModel = Instancio.of(StringsAbc.class)
                    .assign(Assign.valueOf(StringsAbc::getA).to(StringsDef::getF))
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(AbcListHolder::getAbcElements1).at(0, 3), elementModel)
                    .create();

            final List<StringsAbc> list = result.getAbcElements1();
            for (int i = 0; i < list.size(); i++) {
                final StringsAbc el = list.get(i);
                if (i == 0 || i == 3) {
                    assertThat(el.getDef().getF()).isEqualTo(el.getA());
                } else {
                    assertThat(el.getDef().getF()).isNotEqualTo(el.getA());
                }
            }
        }

        @Test
        void assignScopedToTargetList_notSibling() {
            final Model<StringsAbc> elementModel = Instancio.of(StringsAbc.class)
                    .assign(Assign.valueOf(StringsAbc::getA).to(StringsAbc::getB))
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(AbcListHolder::getAbcElements1).at(1), elementModel)
                    .create();

            final List<StringsAbc> sibling = result.getAbcElements2();
            assertThat(sibling)
                    .isNotEmpty()
                    .allSatisfy(el -> assertThat(el.getB()).isNotEqualTo(el.getA()));
        }
    }

    @org.junit.jupiter.api.Nested
    class ModelOverridePrecedence {

        @Test
        void outerSet_overridesDerivedAndBase_atSameIndex() {
            final Model<AbcListHolder> base = Instancio.of(AbcListHolder.class)
                    .set(elementOf(AbcListHolder::getAbcElements1).at(3), EXPECTED_ABC)
                    .toModel();

            final Model<AbcListHolder> derived = Instancio.of(base)
                    .set(elementOf(AbcListHolder::getAbcElements1).at(3), EXPECTED_OVERRIDE_ABC)
                    .toModel();

            final StringsAbc outerValue = StringsAbc.builder().a("_outer_").build();
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(root(), derived)
                    .set(elementOf(AbcListHolder::getAbcElements1).at(3), outerValue)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(outerValue, "abcElements1[3]");
        }

        @Test
        void outerSet_onElementOfTarget_overridesInnerModelField() {
            final Model<StringsAbc> innerModel = Instancio.of(StringsAbc.class)
                    .set(field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(AbcListHolder::getAbcElements1).first(), innerModel)
                    .set(elementOf(AbcListHolder::getAbcElements1).first().field(StringsAbc::getA), EXPECTED_OVERRIDE_STRING)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_OVERRIDE_STRING, "abcElements1[0].a");
        }

        @Test
        void outerIgnore_winsOverInnerModelSet_butInnerSetReportedUnused() {
            final Model<StringsAbc> innerModel = Instancio.of(StringsAbc.class)
                    .set(field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(AbcListHolder::getAbcElements1).first(), innerModel)
                    .ignore(elementOf(AbcListHolder::getAbcElements1).first().field(StringsAbc::getA));

            assertThatThrownBy(api::create)
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("Unused selector");

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .lenient()
                    .setModel(elementOf(AbcListHolder::getAbcElements1).first(), innerModel)
                    .ignore(elementOf(AbcListHolder::getAbcElements1).first().field(StringsAbc::getA))
                    .create();

            assertThatGraph(result)
                    .includingSubtrees("abcElements1[0].a")
                    .hasAllValuesOfTypeEqualTo(String.class, null);
        }

        @Test
        void outerGenerate_onSubField_isMaskedByInnerWholeElementSet() {
            final Model<StringsAbc> innerModel = Instancio.of(StringsAbc.class)
                    .set(all(StringsAbc.class), EXPECTED_ABC)
                    .toModel();

            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(AbcListHolder::getAbcElements1).first(), innerModel)
                    .generate(elementOf(AbcListHolder::getAbcElements1).first().field(StringsAbc::getA),
                            gen -> gen.oneOf(EXPECTED_OVERRIDE_STRING));

            assertThatThrownBy(api::create)
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("Unused selector");

            final AbcListHolder lenient = Instancio.of(AbcListHolder.class)
                    .lenient()
                    .setModel(elementOf(AbcListHolder::getAbcElements1).first(), innerModel)
                    .generate(elementOf(AbcListHolder::getAbcElements1).first().field(StringsAbc::getA),
                            gen -> gen.oneOf(EXPECTED_OVERRIDE_STRING))
                    .create();

            assertThatGraph(lenient).hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[0]");
        }

        @Test
        void wholeElementSet_masksSameElementFieldSelector() {
            // set()-provided instances are not modified, so the sub-field selector is unused
            final Model<StringsAbc> model = Instancio.of(StringsAbc.class)
                    .set(all(StringsAbc.class), EXPECTED_ABC)
                    .set(field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(AbcListHolder::getAbcElements1).first(), model);

            assertThatThrownBy(api::create)
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("Unused selector");

            final AbcListHolder lenient = Instancio.of(AbcListHolder.class)
                    .lenient()
                    .setModel(elementOf(AbcListHolder::getAbcElements1).first(), model)
                    .create();

            // under lenient(), the whole-element value wins and the field selector is dropped
            assertThatGraph(lenient).hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[0]");
        }

        @Test
        void ignoreInDerivedModel_winsOverBaseSet_butBaseSetReportedUnused() {
            final Model<StringsAbc> base = Instancio.of(StringsAbc.class)
                    .set(field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final Model<StringsAbc> derived = Instancio.of(base)
                    .ignore(field(StringsAbc::getA))
                    .toModel();

            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(AbcListHolder::getAbcElements1).first(), derived);

            assertThatThrownBy(api::create)
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("Unused selector");

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .lenient()
                    .setModel(elementOf(AbcListHolder::getAbcElements1).first(), derived)
                    .create();

            assertThatGraph(result)
                    .includingSubtrees("abcElements1[0].a")
                    .hasAllValuesOfTypeEqualTo(String.class, null);
        }
    }

    @org.junit.jupiter.api.Nested
    class ScopeChains {

        @Test
        void innerElementOf_stackedWithinNamedScope() {
            final Model<Nested> innerModel = Instancio.of(Nested.class)
                    .set(elementOf(field(Nested::getAbcElements1)).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final TwoNested result = Instancio.of(TwoNested.class)
                    .setModel(all(Nested.class).within(scope(TwoNested.class, "right")), innerModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "right.abcElements1[0].a");
        }

        @Test
        void innerElementOfRoot_withFieldToScope() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(all(List.class)
                            .within(fields().named("abcElements1").declaredIn(AbcListHolder.class).toScope()), innerModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[0].a");
        }

        @Test
        void elementTargetWithinScopeAndDepth() {
            final Model<StringsAbc> elementModel = Instancio.of(StringsAbc.class)
                    .set(field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(types().of(List.class)
                            .atDepth(d -> d > 0)
                            .within(scope(Nested.class))).at(0), elementModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                            "nested.abcElements1[0].a",
                            "nested.abcElements2[0].a"
                    });
        }
    }

    @org.junit.jupiter.api.Nested
    class NestedModelComposition {

        @Test
        void listModelInNestedModel_appliedWithinScope() {
            final Model<List<StringsAbc>> listModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final Model<Nested> nestedModel = Instancio.of(Nested.class)
                    .setModel(field(Nested::getAbcElements2), listModel)
                    .toModel();

            final TwoNested result = Instancio.of(TwoNested.class)
                    .setModel(all(Nested.class).within(scope(TwoNested.class, "left")), nestedModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "left.abcElements2[0].a");
        }

        @Test
        void indexFramesComposeAcrossLevels() {
            final Model<List<StringsAbc>> listModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).at(1).field(StringsDef::getE), EXPECTED_STRING)
                    .toModel();

            final Model<Nested> nestedModel = Instancio.of(Nested.class)
                    .setModel(field(Nested::getAbcElements1), listModel)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getNested), nestedModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "nested.abcElements1[1].def.e");
        }
    }

    @org.junit.jupiter.api.Nested
    class ModelReuse {

        @Test
        void sameListModel_appliedToMultipleScopedTargets() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .setModel(field(Nested::getAbcElements2), innerModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                            "abcElements1[0].a",
                            "nested.abcElements2[0].a"
                    });
        }

        @Test
        void sameElementModel_appliedToTwoDistinctScopedTargets() {
            final Model<StringsAbc> elementModel = Instancio.of(StringsAbc.class)
                    .set(field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(AbcListHolder::getAbcElements1).at(0), elementModel)
                    .setModel(elementOf(Nested::getAbcElements2).at(LAST_INDEX), elementModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                            "abcElements1[0].a",
                            "nested.abcElements2[%s].a".formatted(LAST_INDEX)
                    });
        }
    }

    @org.junit.jupiter.api.Nested
    class Arrays {

        @Test
        void elementModel_atIndices() {
            final Model<StringsAbc> innerModel = Instancio.of(StringsAbc.class)
                    .set(field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcArrayHolder result = Instancio.of(AbcArrayHolder.class)
                    .setModel(elementOf(AbcArrayHolder::getAbcElements1).at(0, 2), innerModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[0,2].a");
        }

        @Test
        void rootSet_wholeArrayElement() {
            final Model<StringsAbc> innerModel = Instancio.of(StringsAbc.class)
                    .set(root(), EXPECTED_ABC)
                    .toModel();

            final AbcArrayHolder result = Instancio.of(AbcArrayHolder.class)
                    .setModel(elementOf(AbcArrayHolder::getAbcElements1).first(), innerModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[0]");
        }

        @Test
        void assignOrigin_onArray() {
            final Model<StringsAbc> elementModel = Instancio.of(StringsAbc.class)
                    .assign(Assign.valueOf(StringsAbc::getA).to(StringsAbc::getB))
                    .toModel();

            final AbcArrayHolder result = Instancio.of(AbcArrayHolder.class)
                    .setModel(elementOf(AbcArrayHolder::getAbcElements1).at(1), elementModel)
                    .create();

            final StringsAbc[] arr = result.getAbcElements1();
            for (int i = 0; i < arr.length; i++) {
                if (i == 1) {
                    assertThat(arr[i].getB()).isEqualTo(arr[i].getA());
                } else {
                    assertThat(arr[i].getB()).isNotEqualTo(arr[i].getA());
                }
            }
        }

        @Test
        void ignoreElementFieldViaInnerArrayModel() {
            final Model<StringsAbc[]> innerModel = Instancio.of(StringsAbc[].class)
                    .ignore(elementOf(root()).first().field(StringsAbc::getA))
                    .toModel();

            final AbcArrayHolder result = Instancio.of(AbcArrayHolder.class)
                    .setModel(field(AbcArrayHolder::getAbcElements1), innerModel)
                    .create();

            assertThat(result.getAbcElements1()[0].getA()).isNull();
            assertThat(result.getAbcElements1()[1].getA()).isNotBlank();
        }

        @Test
        void widensArrayThroughSetModel() {
            final int targetIndex = 8;

            final Model<StringsAbc> innerModel = Instancio.of(StringsAbc.class)
                    .set(field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcArrayHolder result = Instancio.of(AbcArrayHolder.class)
                    .withSetting(Keys.ARRAY_MIN_SIZE, 1)
                    .withSetting(Keys.ARRAY_MAX_SIZE, 1)
                    .setModel(elementOf(AbcArrayHolder::getAbcElements1).at(targetIndex), innerModel)
                    .create();

            assertThat(result.getAbcElements1()).hasSize(targetIndex + 1);
            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[%s].a".formatted(targetIndex));
        }

        @Test
        void innerArrayModel_appliedToOuterArrayOfArrays() {
            final Model<StringsAbc[]> innerArrayModel = Instancio.of(StringsAbc[].class)
                    .set(elementOf(root()).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final ArrayOfArrays result = Instancio.of(ArrayOfArrays.class)
                    .setModel(all(StringsAbc[].class).within(scope(ArrayOfArrays.class)), innerArrayModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "grid[*][0].a");
        }
    }

    @org.junit.jupiter.api.Nested
    class IndexWideningThroughSetModel {

        @Test
        void elementModelAtHighIndex_widensContainerPastSizeSetting() {
            final int targetIndex = 8;

            final Model<StringsAbc> innerModel = Instancio.of(StringsAbc.class)
                    .set(field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .withSetting(Keys.COLLECTION_MIN_SIZE, 1)
                    .withSetting(Keys.COLLECTION_MAX_SIZE, 1)
                    .setModel(elementOf(AbcListHolder::getAbcElements1).at(targetIndex), innerModel)
                    .create();

            assertThat(result.getAbcElements1()).hasSize(targetIndex + 1);
            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[%s].a".formatted(targetIndex));
        }

        @Test
        void listModelAtHighIndex_widensContainerPastSizeSetting() {
            final int targetIndex = 8;

            final Model<List<StringsAbc>> model = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).at(targetIndex).field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .withSetting(Keys.COLLECTION_MIN_SIZE, 1)
                    .withSetting(Keys.COLLECTION_MAX_SIZE, 1)
                    .setModel(field(AbcListHolder::getAbcElements1), model)
                    .create();

            assertThat(result.getAbcElements1()).hasSize(targetIndex + 1);
            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[%s].a".formatted(targetIndex));
        }

        @Test
        void directElementOfWidensContainerPastSizeSetting() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .withSetting(Keys.COLLECTION_MIN_SIZE, 2)
                    .withSetting(Keys.COLLECTION_MAX_SIZE, 2)
                    .set(elementOf(AbcListHolder::getAbcElements1).at(9).field(StringsAbc::getA), EXPECTED_STRING)
                    .create();

            assertThat(result.getAbcElements1()).hasSize(10);
            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[9].a");
        }

        @Test
        void ofBlankElementOfExceedsExplicitZeroSize() {
            final Model<List<StringsAbc>> firstFieldModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final InstancioApi<AbcListHolder> api = Instancio.ofBlank(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), firstFieldModel);

            assertThatThrownBy(api::create)
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("requires at least 1 elements, but an explicit size of 0 was set");
        }
    }

    @org.junit.jupiter.api.Nested
    class SettingsInterplay {

        @Test
        void collectionElementsNullableDoesNotAffectTargetedElementSet() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final int size = 50;
            final List<AbcListHolder> results = Instancio.ofList(AbcListHolder.class)
                    .size(size)
                    .withSetting(Keys.COLLECTION_ELEMENTS_NULLABLE, true)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            // COLLECTION_ELEMENTS_NULLABLE can null out outer holder elements too, so guard `it`.
            assertThat(results).filteredOn(it -> it != null && it.getAbcElements1().get(0) != null)
                    .isNotEmpty()
                    .allSatisfy(it -> assertThatGraph(it)
                            .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[0].a"));
        }

        @Test
        void maxDepthLimitsTraversalButElementOfStillApplies() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .withSetting(Keys.MAX_DEPTH, 4)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[0].a");
        }
    }

    @org.junit.jupiter.api.Nested
    class OuterEntryPoints {

        @Test
        void ofMapValueModel() {
            // inner model is of the map value type, applied via setModel onto the value
            final Model<AbcListHolder> valueModel = Instancio.of(AbcListHolder.class)
                    .set(elementOf(AbcListHolder::getAbcElements1).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final Map<String, AbcListHolder> result = Instancio.ofMap(String.class, AbcListHolder.class)
                    .size(2)
                    .setModel(all(AbcListHolder.class), valueModel)
                    .create();

            assertThat(result.values())
                    .hasSize(2)
                    .allSatisfy(it -> assertThatGraph(it)
                            .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[0].a"));
        }

        @Test
        void ofObjectFill_doesNotApplyToPreExistingElements() {
            final AbcListHolder existing = new AbcListHolder();
            final List<StringsAbc> list = new ArrayList<>();
            list.add(new StringsAbc());
            list.add(new StringsAbc());
            list.add(new StringsAbc());
            existing.setAbcElements1(list);

            final Model<List<StringsAbc>> firstFieldModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            // fill() does not re-create the existing list field, so the inner model's elementOf
            // does not apply to the pre-existing elements; assert the unused-selector behaviour.
            final InstancioObjectApi<AbcListHolder> api = Instancio.ofObject(existing)
                    .setModel(field(AbcListHolder::getAbcElements1), firstFieldModel);

            assertThatThrownBy(api::fill)
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("Unused selector");
        }
    }

    @org.junit.jupiter.api.Nested
    class ComplexGraphs {

        @Test
        void innerListModelWithElementOfRoot_appliedToAllInnerLists() {
            final Model<List<StringsAbc>> innerListModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final ListOfLists result = Instancio.of(ListOfLists.class)
                    .setModel(all(List.class).within(scope(ListOfLists.class)), innerListModel)
                    .lenient()
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "outer[*][0].a");
        }

        @Test
        void wholeInnerElementSet_viaAllListScope_typeMismatch() {
            final Model<List<StringsAbc>> innerListModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).at(0, 2), EXPECTED_ABC)
                    .toModel();

            final InstancioApi<ListOfLists> api = Instancio.of(ListOfLists.class)
                    .setModel(all(List.class).within(scope(ListOfLists.class)), innerListModel)
                    .lenient();

            assertThatThrownBy(api::create)
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("Type mismatch");
        }

        @Test
        void setModelElementOfWithNestedElementOfModel_isUnused() {
            final Model<List<StringsAbc>> innerListModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final InstancioApi<ListOfLists> api = Instancio.of(ListOfLists.class)
                    .setModel(elementOf(ListOfLists::outer), innerListModel);

            assertThatThrownBy(api::create)
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("Unused selector")
                    .hasMessageContaining("elementOf(elementOf(ListOfLists::outer))");
        }

        @Test
        void innerListModel_appliedToMapValueLists() {
            final Model<List<StringsAbc>> innerListModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            // Map keys appear as the index in graph paths: byKey[someKey][0].a
            final MapOfLists result = Instancio.of(MapOfLists.class)
                    .setModel(all(List.class).within(scope(MapOfLists.class)), innerListModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "byKey[*][0].a");
        }

        @Test
        void deptModelCustomizesItsMembers_appliedToAllTeamDepts() {
            final Model<Dept> deptModel = Instancio.of(Dept.class)
                    .set(elementOf(Dept::members).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final Team result = Instancio.of(Team.class)
                    .setModel(elementOf(Team::depts), deptModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "depts[*].members[0].a");
        }

        @Test
        void outerIndexNotHonoured_whenElementModelTargetsNestedContainer() {
            final Model<Dept> deptModel = Instancio.of(Dept.class)
                    .set(elementOf(Dept::members).at(1).field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final Team result = Instancio.of(Team.class)
                    .setModel(elementOf(Team::depts).at(0, 3), deptModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "depts[*].members[1].a");
        }

        @Test
        void innermostListModel_appliedToDeepestLists() {
            final Model<List<StringsAbc>> innermostModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final DeepLists result = Instancio.of(DeepLists.class)
                    .withSetting(Keys.COLLECTION_MIN_SIZE, 2)
                    .withSetting(Keys.COLLECTION_MAX_SIZE, 2)
                    .setModel(all(List.class).within(scope(DeepLists.class)), innermostModel)
                    .lenient()
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "levels[*][*][0].a");
        }

        @Test
        void indexFramesDoNotBleedAcrossLevels() {
            final Model<List<StringsAbc>> innermostModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).at(1).field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final DeepLists result = Instancio.of(DeepLists.class)
                    .withSetting(Keys.COLLECTION_MIN_SIZE, 2)
                    .withSetting(Keys.COLLECTION_MAX_SIZE, 2)
                    .setModel(all(List.class).within(scope(DeepLists.class)), innermostModel)
                    .lenient()
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "levels[*][*][1].a");
        }

        @Test
        void distinctModelsToEachSiblingList() {
            // abcElements1 and abcElements2 are two sibling List<StringsAbc> fields.
            final Model<List<StringsAbc>> firstModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final Model<List<StringsAbc>> secondModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).last().field(StringsAbc::getA), EXPECTED_OVERRIDE_STRING)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), firstModel)
                    .setModel(field(AbcListHolder::getAbcElements2), secondModel)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[0].a");
            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_OVERRIDE_STRING, "abcElements2[%s].a".formatted(LAST_INDEX));
        }

        @Test
        void sameElementModelReusedAcrossManyArrayTargets() {
            final Model<StringsAbc> elementModel = Instancio.of(StringsAbc.class)
                    .set(field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final ArrayOfArrays result = Instancio.of(ArrayOfArrays.class)
                    .setModel(all(StringsAbc[].class).within(scope(ArrayOfArrays.class)),
                            Instancio.of(StringsAbc[].class)
                                    .setModel(elementOf(root()).first(), elementModel)
                                    .setModel(elementOf(root()).last(), elementModel)
                                    .toModel())
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "grid[*][0,%s].a".formatted(LAST_INDEX));
        }

        @Test
        void targetWithNestedElementOf_throws() {
            final IndexedElementSelector elementOfSelector = elementOf(Team::depts).first();
            final IndexedElementSelector elementOfTarget = elementOf(Dept::members).first();

            assertThatThrownBy(() -> elementOfSelector.target(elementOfTarget))
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("'.target(...)' does not support a nested elementOf() selector");
        }

        @Test
        void combineListAndArrayTargetsInOneBuild() {
            record MixedHolder(List<StringsAbc> list, StringsAbc[] array) {}

            final Model<StringsAbc> model = Instancio.of(StringsAbc.class)
                    .set(field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final MixedHolder result = Instancio.of(MixedHolder.class)
                    .setModel(elementOf(field("list")).first(), model)
                    .setModel(elementOf(field("array")).last(), model)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                            "list[0].a",
                            "array[%s].a".formatted(LAST_INDEX)
                    });
        }

        @Test
        void selfReferentialChildrenStayEmpty_widenNotForced() {
            record RecursiveHolder(StringsAbc value, List<RecursiveHolder> children) {}

            final Model<List<RecursiveHolder>> childrenModel = Instancio.ofList(RecursiveHolder.class)
                    .set(elementOf(root()).first().target(field(StringsAbc::getA)), EXPECTED_STRING)
                    .toModel();

            final RecursiveHolder result = Instancio.of(RecursiveHolder.class)
                    .withSetting(Keys.MAX_DEPTH, 6)
                    .withSetting(Keys.COLLECTION_MIN_SIZE, 2)
                    .withSetting(Keys.COLLECTION_MAX_SIZE, 2)
                    .setModel(field(RecursiveHolder::children), childrenModel)
                    .lenient()
                    .create();

            assertThat(result.children()).isEmpty();
            assertThatGraph(result)
                    .includingPaths("value.a")
                    .hasNoValueEqualTo(EXPECTED_STRING);
        }

        @Test
        void cartesianProductWithSetModelElementOf() {
            record AbcListPair(Integer id, List<StringsAbc> abcList) {}

            final Model<List<StringsAbc>> innerListModel = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).first().field(StringsAbc::getA), EXPECTED_STRING)
                    .toModel();

            final List<AbcListPair> results = Instancio.ofCartesianProduct(AbcListPair.class)
                    .with(field(AbcListPair::id), 1, 2, 3)
                    .setModel(field(AbcListPair::abcList), innerListModel)
                    .create();

            assertThat(results)
                    .hasSize(3)
                    .allSatisfy(pair -> assertThatGraph(pair)
                            .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcList[0].a"));
        }
    }
}
