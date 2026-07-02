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

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Model;
import org.instancio.TargetSelector;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.misc.AbcArrayHolder;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.record.StringsAbcRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;

@FeatureTag({Feature.ELEMENT_OF_SELECTOR, Feature.IGNORE})
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorIgnoreTest {

    private static final int SIZE = 5;

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.ARRAY_MIN_SIZE, SIZE)
            .set(Keys.ARRAY_MAX_SIZE, SIZE)
            .set(Keys.COLLECTION_MIN_SIZE, SIZE)
            .set(Keys.COLLECTION_MAX_SIZE, SIZE);

    /**
     * Ignoring a field within elements, across the supported index selections.
     */
    @Nested
    class ElementField {

        @Test
        void fieldOfAllElements() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .ignore(elementOf(AbcListHolder::getAbcElements1).field(StringsAbc::getA))
                    .create();

            assertThatGraph(result)
                    .hasValuesOfTypeEqualToExactlyIn(String.class, null, "abcElements1[*].a");
        }

        @Test
        void fieldOfFirstElement() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .ignore(elementOf(AbcListHolder::getAbcElements1).first().field(StringsAbc::getA))
                    .create();

            assertThatGraph(result)
                    .hasValuesOfTypeEqualToExactlyIn(String.class, null, "abcElements1[0].a");
        }

        @Test
        void fieldOfElementsInRange() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .ignore(elementOf(AbcListHolder::getAbcElements1).range(1, 3).field(StringsAbc::getA))
                    .create();

            assertThatGraph(result)
                    .hasValuesOfTypeEqualToExactlyIn(String.class, null, "abcElements1[1-3].a");
        }

        @Test
        void primitiveFieldIsLeftAtDefaultValue() {
            record WithPrimitive(int number, String string) {}
            record Holder(List<WithPrimitive> list) {}

            final Holder result = Instancio.of(Holder.class)
                    .ignore(elementOf(field(Holder.class, "list")).field(WithPrimitive::number))
                    .create();

            assertThatGraph(result)
                    .hasValuesOfTypeEqualToExactlyIn(int.class, 0, "list[*].number");

            assertThatGraph(result)
                    .hasNoValueOfTypeEqualTo(String.class, null);
        }
    }

    /**
     * Ignoring a deeper target within elements via {@code .target(...)},
     * including map keys and values.
     */
    @Nested
    class ElementTarget {

        @Test
        void targetWithinFirstElement() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .ignore(elementOf(AbcListHolder::getAbcElements1).first().target(all(StringsDef.class)))
                    .create();

            assertThatGraph(result)
                    .hasValuesOfTypeEqualToExactlyIn(StringsDef.class, null, "abcElements1[0]");
        }

        @Test
        void deepTargetWithinAllElements() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .ignore(elementOf(AbcListHolder::getAbcElements1).target(field(StringsDef::getD)))
                    .create();

            assertThatGraph(result)
                    .hasValuesOfTypeEqualToExactlyIn(String.class, null, "abcElements1[*].def.d");
        }

        @Test
        void ignoreMapValueWithinElement() {
            record Element(Map<Long, Integer> data) {}
            record Holder(List<Element> elements) {}

            final Holder result = Instancio.of(Holder.class)
                    .withSetting(Keys.MAP_MIN_SIZE, 2)
                    .withSetting(Keys.MAP_MAX_SIZE, 2)
                    .ignore(elementOf(field(Holder.class, "elements")).at(0).target(all(Integer.class)))
                    .create();

            assertThat(result.elements).hasSize(SIZE);
            assertThat(result.elements.get(0).data).as("value ignored -> map left empty").isEmpty();
            assertThat(result.elements.subList(1, SIZE))
                    .allSatisfy(element -> assertThat(element.data).hasSize(2));
        }

        @Test
        void ignoreMapKeyWithinElement() {
            record Element(Map<Long, Integer> data) {}
            record Holder(List<Element> elements) {}

            final Holder result = Instancio.of(Holder.class)
                    .withSetting(Keys.MAP_MIN_SIZE, 2)
                    .withSetting(Keys.MAP_MAX_SIZE, 2)
                    .ignore(elementOf(field(Holder.class, "elements")).at(0).target(all(Long.class)))
                    .create();

            assertThat(result.elements).hasSize(SIZE);
            assertThat(result.elements.get(0).data).as("key ignored -> map left empty").isEmpty();
            assertThat(result.elements.subList(1, SIZE))
                    .allSatisfy(element -> assertThat(element.data).hasSize(2));
        }
    }

    /**
     * The container holding the elements may be an array or hold record elements.
     */
    @Nested
    class ContainerVariations {

        @Test
        void arrayContainer() {
            final AbcArrayHolder result = Instancio.of(AbcArrayHolder.class)
                    .ignore(elementOf(AbcArrayHolder::getAbcElements1).first().field(StringsAbc::getA))
                    .create();

            assertThatGraph(result)
                    .hasValuesOfTypeEqualToExactlyIn(String.class, null, "abcElements1[0].a");
        }

        @Test
        void recordElements() {
            record Holder(List<StringsAbcRecord> list) {}

            final Holder result = Instancio.of(Holder.class)
                    .ignore(elementOf(field(Holder::list)).field(StringsAbcRecord::a))
                    .create();

            assertThatGraph(result)
                    .hasValuesOfTypeEqualToExactlyIn(String.class, null, "list[*].a");
        }
    }

    /**
     * {@code elementOf(...)} ignores compose with selector groups and reusable models.
     */
    @Nested
    class GroupAndModel {

        @Test
        void groupOfNestedTargets() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .ignore(all(
                            elementOf(AbcListHolder::getAbcElements1).field(StringsAbc::getA),
                            elementOf(AbcListHolder::getAbcElements2).field(StringsAbc::getB)))
                    .create();

            assertThatGraph(result)
                    .hasValuesOfTypeEqualToExactlyIn(String.class, null,
                            "abcElements1[*].a", "abcElements2[*].b");
        }

        @Test
        void ignoreDefinedInModel() {
            final Model<AbcListHolder> model = Instancio.of(AbcListHolder.class)
                    .ignore(elementOf(AbcListHolder::getAbcElements1).first().field(StringsAbc::getA))
                    .toModel();

            final AbcListHolder result = Instancio.create(model);

            assertThatGraph(result)
                    .hasValuesOfTypeEqualToExactlyIn(String.class, null, "abcElements1[0].a");
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UnmatchedSelectors {

        private final List<TargetSelector> wholeElementSelectors = List.of(
                elementOf(AbcListHolder::getAbcElements1),
                elementOf(AbcListHolder::getAbcElements1).first(),
                elementOf(AbcListHolder::getAbcElements1).range(1, 3),
                elementOf(AbcListHolder::getAbcElements1).target(all(StringsAbc.class)),
                all(elementOf(AbcListHolder::getAbcElements1).field(StringsAbc::getA),
                        elementOf(AbcListHolder::getAbcElements2))
        );

        @FeatureTag(Feature.UNSUPPORTED)
        @FieldSource("wholeElementSelectors")
        @ParameterizedTest
        void wholeElementSelectorsAreReportedAsUnused(final TargetSelector selector) {
            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .ignore(selector);

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(UnusedSelectorException.class)
                    .hasMessageContaining("elementOf(AbcListHolder::getAbcElements");
        }

        @FeatureTag(Feature.UNSUPPORTED)
        @Test
        void lenientWholeElementSelectorIsANoOp() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .ignore(elementOf(AbcListHolder::getAbcElements1).first().lenient())
                    .create();

            assertThat(result.getAbcElements1())
                    .hasSize(SIZE)
                    .allSatisfy(element -> assertThat(element.getA()).isNotNull());
        }

        @Test
        void indexBeyondSizeDoesNotWidenContainer() {
            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .ignore(elementOf(AbcListHolder::getAbcElements1).at(SIZE + 2).field(StringsAbc::getA));

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(UnusedSelectorException.class)
                    .hasMessageContaining("elementOf(AbcListHolder::getAbcElements1)");
        }

        @Test
        void lenientIndexBeyondSize() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .ignore(elementOf(AbcListHolder::getAbcElements1).at(SIZE + 2).field(StringsAbc::getA).lenient())
                    .create();

            assertThat(result.getAbcElements1())
                    .hasSize(SIZE)
                    .allSatisfy(element -> assertThat(element.getA()).isNotNull());
        }
    }

}
