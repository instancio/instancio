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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;

/**
 * Selectors created from different predicates, such as {@code fields(Predicate)}
 * and {@code types(Predicate)}, should not override each other.
 *
 * <p>See https://github.com/instancio/instancio/issues/1918
 */
@FeatureTag({
        Feature.PREDICATE_SELECTOR,
        Feature.ELEMENT_OF_SELECTOR,
        Feature.SET_MODEL,
        Feature.MODEL
})
@ExtendWith(InstancioExtension.class)
class GH1918Test {

    private record Pair(String one, String two) {}

    private record StringAndInteger(String string, Integer integer) {}

    private record Item(String value) {}

    private record ItemPair(Item left, Item right) {}

    private record Lists(List<Item> list1, List<Item> list2) {}

    private record ListsHolder(Lists lists) {}

    private static Predicate<Field> named(final String name) {
        return f -> f.getName().equals(name);
    }

    @Test
    void set() {
        final Pair result = Instancio.of(Pair.class)
                .set(fields(f -> f.getName().contains("one")), "foo")
                .set(fields(f -> f.getName().contains("two")), "bar")
                .create();

        assertThat(result).isEqualTo(new Pair("foo", "bar"));
    }

    @Test
    void setWithTypesOfDifferentValueTypes() {
        final StringAndInteger result = Instancio.of(StringAndInteger.class)
                .set(types(t -> t == String.class), "foo")
                .set(types(t -> t == Integer.class), 123)
                .create();

        assertThat(result).isEqualTo(new StringAndInteger("foo", 123));
    }

    @Test
    void ignore() {
        final Pair result = Instancio.of(Pair.class)
                .ignore(fields(named("one")))
                .ignore(fields(named("two")))
                .create();

        assertThat(result).hasAllNullFieldsOrProperties();
    }

    @Test
    void assign() {
        final Pair result = Instancio.of(Pair.class)
                .assign(Assign.valueOf(fields(named("one"))).set("foo"),
                        Assign.valueOf(fields(named("two"))).set("bar"))
                .create();

        assertThat(result).isEqualTo(new Pair("foo", "bar"));
    }

    @Test
    void cartesianProduct() {
        final List<Pair> results = Instancio.ofCartesianProduct(Pair.class)
                .with(fields(named("one")), "a", "b")
                .with(fields(named("two")), "c")
                .create();

        assertThat(results).containsExactlyInAnyOrder(
                new Pair("a", "c"),
                new Pair("b", "c"));
    }

    @Test
    void elementOfSelector() {
        final Lists result = Instancio.of(Lists.class)
                .set(elementOf(fields(named("list1"))), new Item("foo"))
                .set(elementOf(fields(named("list2"))), new Item("bar"))
                .create();

        assertThat(result.list1()).isNotEmpty().containsOnly(new Item("foo"));
        assertThat(result.list2()).isNotEmpty().containsOnly(new Item("bar"));
    }

    @Test
    void setModelWithPredicateTargets() {
        final Model<Item> leftModel = Instancio.of(Item.class).set(all(String.class), "left").toModel();
        final Model<Item> rightModel = Instancio.of(Item.class).set(all(String.class), "right").toModel();

        final ItemPair result = Instancio.of(ItemPair.class)
                .setModel(fields(named("left")), leftModel)
                .setModel(fields(named("right")), rightModel)
                .create();

        assertThat(result).isEqualTo(new ItemPair(new Item("left"), new Item("right")));
    }

    @Test
    void setModelContainingElementOfPredicateSelectors() {
        final Model<Lists> model = Instancio.of(Lists.class)
                .set(elementOf(fields(named("list1"))).first(), new Item("foo"))
                .set(elementOf(fields(named("list2"))).first(), new Item("bar"))
                .toModel();

        final ListsHolder result = Instancio.of(ListsHolder.class)
                .setModel(field(ListsHolder.class, "lists"), model)
                .create();

        assertThat(result.lists().list1().get(0)).isEqualTo(new Item("foo"));
        assertThat(result.lists().list2().get(0)).isEqualTo(new Item("bar"));
    }

    @Test
    void setModelContainingUnusedElementOfPredicateSelector() {
        final Model<Lists> model = Instancio.of(Lists.class)
                .set(elementOf(fields(named("list1"))).first(), new Item("foo"))
                .set(elementOf(fields(named("nonexistent"))).first(), new Item("bar"))
                .toModel();

        final InstancioApi<ListsHolder> api = Instancio.of(ListsHolder.class)
                .setModel(field(ListsHolder.class, "lists"), model);

        assertThatThrownBy(api::create).isExactlyInstanceOf(UnusedSelectorException.class);
    }

    /**
     * Reusing a predicate instance should behave like any other repeated selector:
     * the last one wins, and strict mode does not report the earlier one as unused.
     */
    @Test
    void samePredicateInstanceShouldOverridePreviousSelector() {
        final Predicate<Field> predicate = named("one");

        final Pair result = Instancio.of(Pair.class)
                .set(fields(predicate), "foo")
                .set(fields(predicate), "bar")
                .create();

        assertThat(result.one()).isEqualTo("bar");
    }

    /**
     * Same as above, but overriding a selector inherited from a model.
     */
    @Test
    void samePredicateInstanceShouldOverrideModelSelector() {
        final Predicate<Field> predicate = named("one");

        final Model<Pair> model = Instancio.of(Pair.class)
                .set(fields(predicate), "foo")
                .toModel();

        final Pair result = Instancio.of(model)
                .set(fields(predicate), "bar")
                .create();

        assertThat(result.one()).isEqualTo("bar");
    }
}
