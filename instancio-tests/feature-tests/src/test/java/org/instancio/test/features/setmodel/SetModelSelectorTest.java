/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.test.features.setmodel;

import lombok.Data;
import lombok.Value;
import org.assertj.core.api.Condition;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.SelectorGroup;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;

@FeatureTag({Feature.MODEL, Feature.SET_MODEL})
@ExtendWith(InstancioExtension.class)
class SetModelSelectorTest {
    private static final String FOO = "_foo_";

    private static final Condition<Item> ONLY_FOOS = new Condition<>(item ->
            FOO.equals(item.getItemString1()) && FOO.equals(item.getItemString2()), "All fields set to FOO");

    private static final Condition<Item> NO_FOOS = new Condition<>(item ->
            !FOO.equals(item.getItemString1()) && !FOO.equals(item.getItemString2()), "No field set to FOO");

    private static @Value class SelectorPair {
        TargetSelector stringSelector;
        TargetSelector modelSelector;
    }

    /**
     * POJO structure for testing:
     *
     * <pre>
     * <0:Outer>
     *  ├──<1:Outer: String outerString>
     *  ├──<1:Outer: Item item>
     *  │   ├──<2:Item: String itemString1>
     *  │   └──<2:Item: String itemString2>
     *  └──<1:Outer: Inner inner>
     *      ├──<2:Inner: String innerString>
     *      ├──<2:Inner: Item item1>
     *      │   ├──<3:Item: String itemString1>
     *      │   └──<3:Item: String itemString2>
     *      └──<2:Inner: Item item2>
     *          ├──<3:Item: String itemString1>
     *          └──<3:Item: String itemString2>
     * </pre>
     *
     * <p>The goal is to create a {@code Model<Item>} with either one or both
     * its fields set to "foo". Then use {@code setModel(TargetSelector, Model)}
     * to inject the model into various places.
     *
     * <p>This should be done using as many variations of selectors as possible.
     */
    private static @Data class Outer {
        String outerString;
        Item item;
        Inner inner;
    }

    private static @Data class Inner {
        String innerString;
        Item item1;
        Item item2;
    }

    private static @Data class Item {
        String itemString1;
        String itemString2;
    }

    /**
     * Set both:
     *
     * <ul>
     *   <li>{@link Item#getItemString1()}</li>
     *   <li>{@link Item#getItemString2()}</li>
     * </ul>
     *
     * <p>in {@link Inner#item1} and {@link Inner#item2} to "foo".
     * All other strings should be random.
     */
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class SetAllInnerItemFieldsToFooTest {

        private Stream<Arguments> selectors() {
            final List<SelectorPair> pairs = Instancio.ofCartesianProduct(SelectorPair.class)
                    .with(field(SelectorPair::getStringSelector),
                            allStrings(),
                            all(allStrings()),
                            all(field("itemString1"), field("itemString2")),
                            all(field(Item::getItemString1), field(Item::getItemString2)),
                            types(t -> t == String.class),
                            types().of(String.class),
                            fields(f -> f.getType() == String.class),
                            fields().ofType(String.class)
                    )
                    .with(field(SelectorPair::getModelSelector),
                            all(Item.class).atDepth(2),
                            all(field(Inner::getItem1), field(Inner::getItem2)),
                            fields().declaredIn(Inner.class).matching("^item\\d$"),
                            fields().declaredIn(Inner.class).matching("^item\\d$").atDepth(d -> d == 2),
                            fields(f -> f.getType() == Item.class).atDepth(2)
                    )
                    .list();

            return pairs.stream().map(pair -> Arguments.of(pair.getStringSelector(), pair.getModelSelector()));
        }

        @MethodSource("selectors")
        @ParameterizedTest
        void verify(
                final TargetSelector stringSelector,
                final TargetSelector modelSelector) {

            final Model<Item> itemModel = Instancio.of(Item.class)
                    .set(stringSelector, FOO)
                    .toModel();

            final Outer result = Instancio.of(Outer.class)
                    .setModel(modelSelector, itemModel)
                    .create();

            assertThat(result.outerString).isNotEqualTo(FOO);
            assertThat(result.item).has(NO_FOOS);
            assertThat(result.inner.innerString).isNotEqualTo(FOO);
            assertThat(result.inner.item1).has(ONLY_FOOS);
            assertThat(result.inner.item2).has(ONLY_FOOS);
        }
    }

    /**
     * Set both:
     *
     * <ul>
     *   <li>{@link Item#getItemString1()}</li>
     *   <li>{@link Item#getItemString2()}</li>
     * </ul>
     *
     * <p>in {@link Inner#item2} to "foo".
     * All other strings should be random.
     */
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class SetBothInnerItem2FieldsToFooTest {

        private Stream<Arguments> selectors() {
            final List<SelectorPair> pairs = Instancio.ofCartesianProduct(SelectorPair.class)
                    .with(field(SelectorPair::getStringSelector),
                            allStrings(),
                            all(allStrings()),
                            all(field("itemString1"), field("itemString2")),
                            all(field(Item::getItemString1), field(Item::getItemString2)),
                            types(t -> t == String.class),
                            types().of(String.class),
                            fields(f -> f.getType() == String.class),
                            fields().ofType(String.class),
                            allStrings().within(fields().ofType(String.class).matching("^itemString\\d$").toScope()),
                            fields().ofType(String.class).within(fields().matching("^itemString\\d$").toScope()),
                            types().of(String.class).within(fields().matching("^itemString\\d$").toScope())
                    )
                    .with(field(SelectorPair::getModelSelector),
                            field(Inner::getItem2),
                            fields().declaredIn(Inner.class).named("item2"),
                            fields().declaredIn(Inner.class).matching("item2").atDepth(d -> d == 2),
                            fields(f -> f.getDeclaringClass() == Inner.class && f.getName().equals("item2"))
                    )
                    .list();

            return pairs.stream().map(pair -> Arguments.of(pair.getStringSelector(), pair.getModelSelector()));
        }

        @MethodSource("selectors")
        @ParameterizedTest
        void verify(
                final TargetSelector stringSelector,
                final TargetSelector modelSelector) {

            final Model<Item> itemModel = Instancio.of(Item.class)
                    .set(stringSelector, FOO)
                    .toModel();

            final Outer result = Instancio.of(Outer.class)
                    .setModel(modelSelector, itemModel)
                    .create();

            assertThat(result.outerString).isNotEqualTo(FOO);
            assertThat(result.item).has(NO_FOOS);
            assertThat(result.inner.innerString).isNotEqualTo(FOO);
            assertThat(result.inner.item1).has(NO_FOOS);
            assertThat(result.inner.item2).has(ONLY_FOOS);
        }
    }

    /**
     * Set only:
     *
     * <ul>
     *   <li>{@link Item#getItemString2()}</li>
     * </ul>
     *
     * <p>in {@link Inner#item2} to "foo".
     * All other strings should be random.
     */
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class SetInnerItem2String2ToFooTest {

        private Stream<Arguments> selectors() {
            final List<SelectorPair> pairs = Instancio.ofCartesianProduct(SelectorPair.class)
                    .with(field(SelectorPair::getStringSelector),
                            field("itemString2"),
                            field(Item::getItemString2),
                            all(field(Item::getItemString2)),
                            fields(f -> f.getType() == String.class && f.getName().equals("itemString2")),
                            fields().ofType(String.class).named("itemString2")
                    )
                    .with(field(SelectorPair::getModelSelector),
                            field(Inner::getItem2),
                            field(Inner::getItem2).atDepth(2),
                            fields().declaredIn(Inner.class).named("item2"),
                            fields(f -> f.getDeclaringClass() == Inner.class && f.getName().equals("item2"))
                    )
                    .list();

            return pairs.stream().map(pair -> Arguments.of(pair.getStringSelector(), pair.getModelSelector()));
        }

        @MethodSource("selectors")
        @ParameterizedTest
        void verify(
                final TargetSelector stringSelector,
                final TargetSelector modelSelector) {

            final Model<Item> itemModel = Instancio.of(Item.class)
                    .set(stringSelector, FOO)
                    .toModel();

            final Outer result = Instancio.of(Outer.class)
                    .setModel(modelSelector, itemModel)
                    .create();

            assertThat(result.outerString).isNotEqualTo(FOO);
            assertThat(result.item).has(NO_FOOS);
            assertThat(result.inner.innerString).isNotEqualTo(FOO);
            assertThat(result.inner.item1).has(NO_FOOS);
            assertThat(result.inner.item2.itemString1).isNotEqualTo(FOO);
            assertThat(result.inner.item2.itemString2).isEqualTo(FOO);
        }
    }

    /**
     * Set only:
     *
     * <ul>
     *   <li>{@link Item#getItemString2()}</li>
     * </ul>
     *
     * <p>in {@link Outer#item} to "foo".
     * All other strings should be random.
     */
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class SetOuterItemString2ToFooTest {

        private Stream<Arguments> selectors() {
            final List<SelectorPair> pairs = Instancio.ofCartesianProduct(SelectorPair.class)
                    .with(field(SelectorPair::getStringSelector),
                            field("itemString2"),
                            field(Item::getItemString2),
                            all(field(Item::getItemString2)),
                            fields(f -> f.getType() == String.class && f.getName().equals("itemString2")),
                            fields().ofType(String.class).named("itemString2")
                    )
                    .with(field(SelectorPair::getModelSelector),
                            types().of(Item.class).atDepth(1),
                            field(Outer::getItem),
                            fields().declaredIn(Outer.class).named("item"),
                            fields(f -> f.getDeclaringClass() == Outer.class && f.getName().equals("item"))
                    )
                    .list();

            return pairs.stream().map(pair -> Arguments.of(pair.getStringSelector(), pair.getModelSelector()));
        }

        @MethodSource("selectors")
        @ParameterizedTest
        void verify(
                final TargetSelector stringSelector,
                final TargetSelector modelSelector) {

            final Model<Item> itemModel = Instancio.of(Item.class)
                    .set(stringSelector, FOO)
                    .toModel();

            final Outer result = Instancio.of(Outer.class)
                    .setModel(modelSelector, itemModel)
                    .create();

            assertThat(result.outerString).isNotEqualTo(FOO);
            assertThat(result.item.itemString1).isNotEqualTo(FOO);
            assertThat(result.item.itemString2).isEqualTo(FOO);
            assertThat(result.inner.innerString).isNotEqualTo(FOO);
            assertThat(result.inner.item1).has(NO_FOOS);
            assertThat(result.inner.item2).has(NO_FOOS);
        }
    }

    @Test
    void modelSelectorGroup() {
        final Model<Item> model = Instancio.of(Item.class)
                .set(allStrings(), FOO)
                .toModel();

        final SelectorGroup modelSelectors = all(
                fields().named("item").declaredIn(Outer.class),
                field(Inner::getItem2));

        final Outer result = Instancio.of(Outer.class)
                .setModel(modelSelectors, model)
                .create();

        assertThat(result.item).has(ONLY_FOOS);
        assertThat(result.inner.item1).has(NO_FOOS);
        assertThat(result.inner.item2).has(ONLY_FOOS);
    }

    @Test
    void primitiveSelector() {
        final Model<Integer> model = Instancio.of(Integer.class)
                .generate(all(int.class), gen -> gen.ints().range(-10, -1))
                .toModel();

        final IntegerHolder result = Instancio.of(IntegerHolder.class)
                .setModel(all(int.class), model)
                .create();

        // The model should be applied to the primitive but not the wrapper type
        assertThat(result.getPrimitive()).isBetween(-10, -1);
        assertThat(result.getWrapper()).isPositive();
    }

    @Test
    void primitiveWrapperSelector() {
        final Model<Integer> model = Instancio.of(Integer.class)
                .generate(allInts(), gen -> gen.ints().range(-10, -1))
                .toModel();

        final IntegerHolder result = Instancio.of(IntegerHolder.class)
                .setModel(allInts(), model)
                .create();

        assertThat(result.getPrimitive()).isBetween(-10, -1);
        assertThat(result.getWrapper()).isBetween(-10, -1);
    }
}