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

import org.instancio.ElementOfSelector;
import org.instancio.GroupableSelector;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.SelectorGroup;
import org.instancio.TargetSelector;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodError;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;
import static org.instancio.Select.root;
import static org.instancio.Select.scope;
import static org.instancio.Select.setter;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;

@FeatureTag(Feature.ELEMENT_OF_SELECTOR)
@ExtendWith(InstancioExtension.class)
class ElementOfTargetSelectorTest {

    private static final String EXPECTED_STRING = "_value_";
    private static final StringsAbc EXPECTED_ABC = new StringsAbc();

    /**
     * Variants of the container selector passed to {@code elementOf(container)}.
     */
    @Nested
    class ContainerSelector {

        @Test
        void setterSelector() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .withSetting(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                    .withSetting(Keys.ON_SET_METHOD_ERROR, OnSetMethodError.FAIL)
                    .set(elementOf(setter(AbcListHolder.class, "setAbcElements1")).first(), EXPECTED_ABC)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[0]");
        }

        @Test
        void rootSelectorOnListRoot() {
            final int index = Instancio.gen().ints().range(0, 10).get();
            final List<StringsAbc> result = Instancio.ofList(StringsAbc.class)
                    .set(elementOf(root()).at(index), EXPECTED_ABC)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_ABC, "*[%s]".formatted(index));
        }

        @Test
        void nestedFieldSelector() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .set(elementOf(field(AbcListHolder.Nested.class, "abcElements1")).first(), EXPECTED_ABC)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_ABC, "nested.abcElements1[0]");
        }

        @Test
        void scopedContainerNarrowsMatchingContainers() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .set(elementOf(all(List.class).within(scope(AbcListHolder.Nested.class)))
                            .first()
                            .target(field(StringsAbc::getA)), EXPECTED_STRING)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                            "nested.abcElements1[0].a",
                            "nested.abcElements2[0].a"
                    });
        }

        @Test
        void scopedContainerNarrowsMatchingContainers_wholeElement() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .set(elementOf(all(List.class).within(scope(AbcListHolder.Nested.class))).first(), EXPECTED_ABC)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_ABC, new String[]{
                            "nested.abcElements1[0]",
                            "nested.abcElements2[0]"
                    });
        }
    }

    /**
     * Variants of the element target specified via {@code .target(...)} / {@code .field(...)}.
     */
    @Nested
    class ElementTarget {

        @Test
        void primitiveAndWrapperSelector() {
            record Sample(List<IntegerHolder> list) {}

            final Sample result = Instancio.of(Sample.class)
                    .set(elementOf(Sample::list).at(0, 3, 9).target(allInts()), -1)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(-1, new String[]{
                            "list[0,3,9].primitive",
                            "list[0,3,9].wrapper"
                    });
        }

        @Test
        void selectorGroup() {
            final SelectorGroup group = all(
                    field(StringsAbc::getC),
                    field(StringsDef::getF));

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .set(elementOf(AbcListHolder::getAbcElements1).first().target(group), EXPECTED_STRING)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                            "abcElements1[0].c",
                            "abcElements1[0].def.f"
                    });
        }

        @RepeatedTest(10)
        void recordComponentField() {
            enum OrderStatus {SUBMITTED, SHIPPED, DELIVERED}
            record Order(OrderStatus status) {}
            record Customer(List<Order> orders) {}

            final Customer result = Instancio.of(Customer.class)
                    .set(elementOf(Customer::orders).at(0, 1, 2, 3).field(Order::status), OrderStatus.SHIPPED)
                    .set(elementOf(Customer::orders).at(4).field(Order::status), OrderStatus.DELIVERED)
                    .create();

            assertThat(result.orders).hasSizeGreaterThanOrEqualTo(5);

            assertThatGraph(result)
                    .includingSubtrees("orders[0,1,2,3].status")
                    .hasAllValuesEqualTo(OrderStatus.SHIPPED);

            assertThatGraph(result)
                    .includingSubtrees("orders[4].status")
                    .hasAllValuesEqualTo(OrderStatus.DELIVERED);
        }
    }

    @Nested
    class Validation {

        @Test
        void elementOfRejectsSelectorGroup() {
            final SelectorGroup group = all(
                    field(AbcListHolder::getAbcElements1),
                    field(AbcListHolder::getAbcElements2));

            assertThatThrownBy(() -> elementOf(group))
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("elementOf() does not support selector groups");
        }

        @Test
        void elementOfRejectsContainerThatExpandsToMultipleTargets() {
            final TargetSelector multipleFields = all(field(StringsAbc::getA), field(StringsAbc::getC));

            final TargetSelector target = elementOf(AbcListHolder::getAbcElements1)
                    .first()
                    .target(multipleFields);

            final ElementOfSelector elementOfSelector = elementOf(target);

            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class);

            assertThatThrownBy(() -> api.set(elementOfSelector, EXPECTED_ABC))
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("elementOf() does not support container selectors that expand into multiple targets");
        }

        @Nested
        class ElementTargetNotInElementSubtree {

            @Test
            void genericContainerType() {
                record GenericListHolder<T>(List<T> elements, String name) {}

                final GroupableSelector selector = elementOf(field(GenericListHolder.class, "elements"))
                        .first()
                        .target(field(GenericListHolder.class, "name"));

                final InstancioApi<?> api = Instancio.of(GenericListHolder.class);

                assertThatThrownBy(() -> api.set(selector, "value"))
                        .isInstanceOf(InstancioApiException.class)
                        .hasMessageContaining("did not match any field within the element subtree")
                        .hasMessageContaining("(the container's holder class)")
                        .hasMessageContaining("field declared in the element type '?' (or one of its subtypes)");
            }

            @Test
            void rawContainerType() {
                // Raw container type to covers 'not a ParameterizedType' branch of element-type extraction.
                @SuppressWarnings({"unused", "rawtypes"})
                record RawListHolder(List elements, String name) {}

                final GroupableSelector selector = elementOf(field(RawListHolder.class, "elements"))
                        .target(field(RawListHolder.class, "name"));

                final InstancioApi<?> api = Instancio.of(RawListHolder.class);

                assertThatThrownBy(() -> api.set(selector, "value"))
                        .isInstanceOf(InstancioApiException.class)
                        .hasMessageContaining("did not match any field within the element subtree")
                        .hasMessageContaining("field declared in the element type '?' (or one of its subtypes)");
            }

            @Test
            @SuppressWarnings({"unused", "InnerClassMayBeStatic", "NullAway"})
            void parameterizedTypeWithoutTypeArguments() {
                // Covers the 'zero type arguments' branch of element-type extraction.
                class GenericOuter<T> {
                    class InnerElement {}
                }
                class OuterInnerHolder {
                    GenericOuter<String>.InnerElement container;
                    String name;
                }

                final GroupableSelector selector = elementOf(field(OuterInnerHolder.class, "container"))
                        .target(field(OuterInnerHolder.class, "name"));

                final InstancioApi<?> api = Instancio.of(OuterInnerHolder.class);

                assertThatThrownBy(() -> api.set(selector, "value"))
                        .isInstanceOf(InstancioApiException.class)
                        .hasMessageContaining("did not match any field within the element subtree")
                        .hasMessageContaining("field declared in the element type '?' (or one of its subtypes)");
            }
        }
    }
}
