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
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Model;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.scope;
import static org.instancio.Select.types;

/**
 * Selectors are stored as map keys, so a selector replaces an earlier one that it equals.
 * Distinct selectors should never be equal, even if their descriptions are the same.
 *
 * <p>See also {@link GH1918Test}.
 */
@FeatureTag({
        Feature.SELECTOR,
        Feature.PREDICATE_SELECTOR,
        Feature.ELEMENT_OF_SELECTOR,
        Feature.DEPTH_SELECTOR,
        Feature.SCOPE,
        Feature.ASSIGN,
        Feature.MODEL,
        Feature.SET_MODEL,
        Feature.CARTESIAN_PRODUCT,
        Feature.IGNORE
})
@ExtendWith(InstancioExtension.class)
class SelectorEqualityTest {

    private record Item(String value) {}

    private record Inner(List<Item> items) {}

    private record Outer(List<Item> items, Inner inner) {}

    private record InnerPair(Inner a, Inner b) {}

    private record Leaf(String value) {}

    private record Element(String value, Leaf leaf) {}

    private record Elements(List<Element> elements) {}

    private record Copies(String a, String b, String copyOfA, String copyOfB) {}

    private record CopiesList(List<Copies> items) {}

    private static final class A {
        private record Item(String value) {}

        private record Holder(List<String> items) {}
    }

    private static final class B {
        private record Item(String value) {}

        private record Holder(List<String> items) {}
    }

    private record ItemsWithSameSimpleName(A.Item a, B.Item b) {}

    private record ItemsWithSameSimpleNameAndCopies(A.Item a, B.Item b, String copyOfA, String copyOfB) {}

    private record HoldersWithSameSimpleName(A.Holder a, B.Holder b) {}

    private record OnlyA(A.Item a) {}

    @Nested
    class DistinctSelectorsWithSameDescriptionTest {

        @Test
        void elementOfWithDifferentContainerScopes() {
            final Outer result = Instancio.of(Outer.class)
                    .set(elementOf(all(List.class)), new Item("any"))
                    .set(elementOf(all(List.class).within(scope(Inner.class))), new Item("inner"))
                    .create();

            assertThat(result.items()).containsOnly(new Item("any"));
            assertThat(result.inner().items()).containsOnly(new Item("inner"));
        }

        @Test
        void elementOfWithDifferentContainerDepths() {
            final Outer result = Instancio.of(Outer.class)
                    .set(elementOf(all(List.class).atDepth(1)), new Item("depth1"))
                    .set(elementOf(all(List.class).atDepth(2)), new Item("depth2"))
                    .create();

            assertThat(result.items()).containsOnly(new Item("depth1"));
            assertThat(result.inner().items()).containsOnly(new Item("depth2"));
        }

        @Test
        void elementOfWithContainerFieldsInClassesWithSameSimpleName() {
            final HoldersWithSameSimpleName result = Instancio.of(HoldersWithSameSimpleName.class)
                    .set(elementOf(field(A.Holder.class, "items")), "a")
                    .set(elementOf(field(B.Holder.class, "items")), "b")
                    .create();

            assertThat(result.a().items()).containsOnly("a");
            assertThat(result.b().items()).containsOnly("b");
        }

        @Test
        void elementOfWithDifferentInnerSelectorDepths() {
            final Elements result = Instancio.of(Elements.class)
                    .set(elementOf(field(Elements.class, "elements")).target(all(String.class).atDepth(3)), "depth3")
                    .set(elementOf(field(Elements.class, "elements")).target(all(String.class).atDepth(4)), "depth4")
                    .create();

            assertThat(result.elements()).isNotEmpty().allSatisfy(element -> {
                assertThat(element.value()).isEqualTo("depth3");
                assertThat(element.leaf().value()).isEqualTo("depth4");
            });
        }

        @Test
        void fieldsDeclaredInClassesWithSameSimpleName() {
            final ItemsWithSameSimpleName result = Instancio.of(ItemsWithSameSimpleName.class)
                    .set(fields().declaredIn(A.Item.class), "a")
                    .set(fields().declaredIn(B.Item.class), "b")
                    .create();

            assertThat(result.a().value()).isEqualTo("a");
            assertThat(result.b().value()).isEqualTo("b");
        }

        @Test
        void typesOfClassesWithSameSimpleName() {
            final ItemsWithSameSimpleName result = Instancio.of(ItemsWithSameSimpleName.class)
                    .set(types().of(A.Item.class), new A.Item("a"))
                    .set(types().of(B.Item.class), new B.Item("b"))
                    .create();

            assertThat(result.a().value()).isEqualTo("a");
            assertThat(result.b().value()).isEqualTo("b");
        }

        @Test
        void ignoreFieldsDeclaredInClassesWithSameSimpleName() {
            final ItemsWithSameSimpleName result = Instancio.of(ItemsWithSameSimpleName.class)
                    .ignore(fields().declaredIn(A.Item.class))
                    .ignore(fields().declaredIn(B.Item.class))
                    .create();

            assertThat(result.a().value()).isNull();
            assertThat(result.b().value()).isNull();
        }

        @Test
        void cartesianProductWithFieldsDeclaredInClassesWithSameSimpleName() {
            final List<ItemsWithSameSimpleName> results = Instancio.ofCartesianProduct(ItemsWithSameSimpleName.class)
                    .with(fields().declaredIn(A.Item.class), "a1", "a2")
                    .with(fields().declaredIn(B.Item.class), "b")
                    .create();

            assertThat(results)
                    .extracting(r -> r.a().value() + "," + r.b().value())
                    .containsExactlyInAnyOrder("a1,b", "a2,b");
        }
    }

    @Nested
    class AssignmentTest {

        @Test
        void originsWithSameDescription() {
            final ItemsWithSameSimpleNameAndCopies result = Instancio.of(ItemsWithSameSimpleNameAndCopies.class)
                    .assign(Assign.valueOf(fields().declaredIn(A.Item.class))
                            .to(field(ItemsWithSameSimpleNameAndCopies.class, "copyOfA")))
                    .assign(Assign.valueOf(fields().declaredIn(B.Item.class))
                            .to(field(ItemsWithSameSimpleNameAndCopies.class, "copyOfB")))
                    .create();

            assertThat(result.copyOfA()).isEqualTo(result.a().value());
            assertThat(result.copyOfB()).isEqualTo(result.b().value());
        }

        @Test
        void destinationsWithSameDescription() {
            final ItemsWithSameSimpleName result = Instancio.of(ItemsWithSameSimpleName.class)
                    .assign(Assign.valueOf(fields().declaredIn(A.Item.class)).set("a"))
                    .assign(Assign.valueOf(fields().declaredIn(B.Item.class)).set("b"))
                    .create();

            assertThat(result.a().value()).isEqualTo("a");
            assertThat(result.b().value()).isEqualTo("b");
        }

        /**
         * The model's origin selectors are rebaked to fire within the element frame,
         * and should remain distinct from each other.
         */
        @Test
        void setModelWithElementOfTargetAndMultipleAssignments() {
            final Model<Copies> model = Instancio.of(Copies.class)
                    .assign(Assign.valueOf(field(Copies.class, "a")).to(field(Copies.class, "copyOfA")))
                    .assign(Assign.valueOf(field(Copies.class, "b")).to(field(Copies.class, "copyOfB")))
                    .toModel();

            final CopiesList result = Instancio.of(CopiesList.class)
                    .setModel(elementOf(field(CopiesList.class, "items")), model)
                    .create();

            assertThat(result.items()).isNotEmpty().allSatisfy(copies -> {
                assertThat(copies.copyOfA()).isEqualTo(copies.a());
                assertThat(copies.copyOfB()).isEqualTo(copies.b());
            });
        }
    }

    @Nested
    class UnusedSelectorTest {

        @Test
        void unusedSelectorWithSameDescriptionAsUsedSelector() {
            final InstancioApi<OnlyA> api = Instancio.of(OnlyA.class)
                    .set(fields().declaredIn(A.Item.class), "a")
                    .set(fields().declaredIn(B.Item.class), "b");

            assertThatThrownBy(api::create).isExactlyInstanceOf(UnusedSelectorException.class);
        }

        /**
         * The model's selector is unused under {@code b} because {@code b.items} is ignored.
         * It should still be reported even though the same selector is used under {@code a}.
         */
        @Test
        void sameModelForTwoTargetsWithSelectorUnusedUnderOne() {
            final Model<Inner> model = Instancio.of(Inner.class)
                    .set(elementOf(field(Inner.class, "items")).first(), new Item("foo"))
                    .toModel();

            final InstancioApi<InnerPair> api = Instancio.of(InnerPair.class)
                    .setModel(field(InnerPair.class, "a"), model)
                    .setModel(field(InnerPair.class, "b"), model)
                    .ignore(field(Inner.class, "items").within(scope(InnerPair.class, "b")));

            assertThatThrownBy(api::create).isExactlyInstanceOf(UnusedSelectorException.class);
        }
    }
}
