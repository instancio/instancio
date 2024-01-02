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
package org.instancio.test.features.subtype;

import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.basic.StringHolderAlternativeImpl;
import org.instancio.test.support.pojo.collections.lists.ListLong;
import org.instancio.test.support.pojo.collections.lists.TwoListsOfItemString;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.generics.basic.ItemAlternativeImpl;
import org.instancio.test.support.pojo.generics.basic.NonGenericItemStringExtension;
import org.instancio.test.support.pojo.interfaces.ItemInterface;
import org.instancio.test.support.pojo.interfaces.ListOfItemInterfaceString;
import org.instancio.test.support.pojo.interfaces.ListOfStringHolderInterface;
import org.instancio.test.support.pojo.interfaces.StringHolderInterface;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.scope;
import static org.instancio.Select.types;

@FeatureTag({
        Feature.SELECTOR,
        Feature.PREDICATE_SELECTOR,
        Feature.SUBTYPE,
        Feature.COLLECTION_GENERATOR_SUBTYPE,
        Feature.SCOPE
})
class CollectionElementSubtypeMappingTest {

    @Test
    @DisplayName("Subtype mapping from List to Vector")
    void singleList() {
        final ListLong result = Instancio.of(ListLong.class)
                .subtype(all(List.class), Vector.class)
                .create();

        assertThat(result.getList()).isNotEmpty().isInstanceOf(Vector.class);
    }

    @Test
    @DisplayName("Given subtype mapping from List to Vector and a generator, the generator takes precedence")
    void multipleLists() {
        final TwoListsOfItemString result = Instancio.of(TwoListsOfItemString.class)
                .subtype(all(List.class), Vector.class)
                .generate(field("list1"), gen -> gen.collection().subtype(LinkedList.class))
                .create();

        assertThat(result.getList1()).isNotEmpty().isInstanceOf(LinkedList.class);
        assertThat(result.getList2()).isNotEmpty().isInstanceOf(Vector.class);
    }

    @Test
    void listWithSelectors() {
        final TwoListsOfItemString result = Instancio.of(TwoListsOfItemString.class)
                .subtype(all(List.class).within(scope(TwoListsOfItemString.class, "list1")), Vector.class)
                .create();

        assertThat(result.getList1()).isNotEmpty().isExactlyInstanceOf(Vector.class);
        assertThat(result.getList2()).isNotEmpty().isExactlyInstanceOf(ArrayList.class);
    }

    @Test
    void listElementWithScope() {
        final TwoListsOfItemString result = Instancio.of(TwoListsOfItemString.class)
                .subtype(all(Item.class).within(scope(TwoListsOfItemString.class, "list1")),
                        NonGenericItemStringExtension.class)
                .create();

        assertThat(result.getList1()).isNotEmpty().allSatisfy(
                item -> assertThat(item).isExactlyInstanceOf(NonGenericItemStringExtension.class));

        assertThat(result.getList2()).isNotEmpty().allSatisfy(
                item -> assertThat(item).isExactlyInstanceOf(Item.class));
    }

    @ParameterizedTest
    @MethodSource("stringHolderInterfaceSelectors")
    @DisplayName("Subtype mapping of non-generic class (as collection element)")
    void listContainingNonGenericElement(final TargetSelector selector) {
        final ListOfStringHolderInterface result = Instancio.of(ListOfStringHolderInterface.class)
                .subtype(selector, StringHolderAlternativeImpl.class)
                .create();

        assertThat(result.getList()).isNotEmpty()
                .hasOnlyElementsOfType(StringHolderAlternativeImpl.class)
                .allSatisfy(item -> assertThat(item.getValue()).isNotBlank());
    }

    @ParameterizedTest
    @MethodSource("itemInterfaceSelectors")
    @DisplayName("Subtype mapping of a generic class (as collection element)")
    void listContainingGenericElement(final TargetSelector selector) {
        final ListOfItemInterfaceString result = Instancio.of(ListOfItemInterfaceString.class)
                .subtype(selector, ItemAlternativeImpl.class)
                .create();

        assertThat(result.getList()).isNotEmpty()
                .hasOnlyElementsOfType(ItemAlternativeImpl.class)
                .allSatisfy(item -> assertThat(item.getValue()).isNotBlank());
    }

    @ParameterizedTest
    @MethodSource("collectionAndElementSelectors")
    @DisplayName("Use subtype() for collection values, and generator spec subtype() for the collection itself")
    void subtypeValuesAndGeneratorSubtypeOfCollection(
            final TargetSelector collectionSelector,
            final TargetSelector elementSelector) {

        final ListOfStringHolderInterface result = Instancio.of(ListOfStringHolderInterface.class)
                .generate(collectionSelector, gen -> gen.collection().subtype(LinkedList.class))
                .subtype(elementSelector, StringHolder.class)
                .create();

        assertThat(result.getList())
                .isExactlyInstanceOf(LinkedList.class)
                .hasOnlyElementsOfType(StringHolder.class)
                .extracting(StringHolderInterface::getValue)
                .doesNotContainNull();
    }

    private static Stream<Arguments> itemInterfaceSelectors() {
        return Stream.of(
                Arguments.of(all(ItemInterface.class)),
                Arguments.of(types().of(ItemInterface.class)));
    }

    private static Stream<Arguments> stringHolderInterfaceSelectors() {
        return Stream.of(
                Arguments.of(all(StringHolderInterface.class)),
                Arguments.of(types().of(StringHolderInterface.class)));
    }

    private static Stream<Arguments> collectionAndElementSelectors() {
        return Stream.of(
                // regular + regular
                Arguments.of(all(List.class), all(StringHolderInterface.class)),
                Arguments.of(field("list"), all(StringHolderInterface.class)),

                // regular + predicate
                Arguments.of(all(List.class), types().of(StringHolderInterface.class)),
                Arguments.of(field("list"), types().of(StringHolderInterface.class)),

                // predicate + predicate
                Arguments.of(types().of(Collection.class), types().of(StringHolderInterface.class)),
                Arguments.of(fields().named("list"), types().of(StringHolderInterface.class))
        );
    }
}
