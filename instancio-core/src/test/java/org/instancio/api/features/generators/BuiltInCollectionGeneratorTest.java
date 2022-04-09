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
package org.instancio.api.features.generators;

import org.apache.commons.lang3.RandomUtils;
import org.instancio.Instancio;
import org.instancio.test.support.pojo.collections.TwoStringCollections;
import org.instancio.test.support.pojo.collections.lists.ListString;
import org.instancio.test.support.pojo.collections.lists.TwoListsOfItemString;
import org.instancio.test.support.pojo.collections.maps.SortedMapIntegerString;
import org.instancio.test.support.pojo.collections.maps.TwoMapsOfIntegerItemString;
import org.instancio.test.support.pojo.collections.sets.HashSetLong;
import org.instancio.test.support.pojo.collections.sets.SetLong;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.testsupport.Constants;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.Vector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Bindings.all;
import static org.instancio.Bindings.field;
import static org.instancio.testsupport.asserts.ReflectionAssert.assertThatObject;

// TODO test nested lists/maps
class BuiltInCollectionGeneratorTest {
    private static final int EXPECTED_SIZE = RandomUtils.nextInt(90, 100);

    @Nested
    class CollectionTypeTest {

        @Disabled
        @Test
        @DisplayName("TODO need to decide how to handle this case: should all(Foo.class) target Foo base types?")
        void TODO() {

            // NOTE the binding is targetting 'Set' but HashSetLong declares a 'HashSet'.
            //  Therefore custom generator lookup fails; defaults to generator for HashSet
            final int minSize = 100;
            final HashSetLong result = Instancio.of(HashSetLong.class)
                    //.generate(allLongs(), gen -> gen.longs().min(Long.MIN_VALUE).max(Long.MAX_VALUE))
                    .generate(all(Set.class), gen -> gen.collection().minSize(minSize))
                    .create();

            assertThatObject(result).isFullyPopulated();
            assertThat(result.getSet()).hasSizeGreaterThan(minSize * 90 / 100);
        }

        @Test
        @DisplayName("When collection type not specified: field type HashSet")
        void collectionTypeNotSpecifiedWithHashSetField() {
            final int minSize = 100;

            final HashSetLong result = Instancio.of(HashSetLong.class)
                    .generate(all(HashSet.class), gen -> gen.collection().minSize(minSize)) //.type() not specified
                    .create();

            assertThat(result.getSet()).hasSizeGreaterThanOrEqualTo(minSize);
        }

        @Test
        @DisplayName("When collection type not specified: field type Set")
        void collectionTypeNotSpecifiedWithSetField() {
            final int minSize = 100;

            final SetLong result = Instancio.of(SetLong.class)
                    .generate(all(Set.class), gen -> gen.collection().minSize(minSize)) //.type() not specified
                    .create();

            assertThat(result.getSet()).hasSizeGreaterThanOrEqualTo(minSize);
        }

        @Test
        @DisplayName("When collection type not specified: field type Collection")
        void collectionTypeNotSpecifiedWithCollectionField() {
            final int minSize = 100;

            final TwoStringCollections result = Instancio.of(TwoStringCollections.class)
                    .generate(all(Collection.class), gen -> gen.collection().minSize(minSize)) //.type() not specified
                    .create();

            assertThat(result.getOne()).hasSizeGreaterThanOrEqualTo(minSize);
            assertThat(result.getTwo()).hasSizeGreaterThanOrEqualTo(minSize);
        }


        @Test
        @DisplayName("When map type not specified: field type SortedMap")
        void sortedMap() {
            final SortedMapIntegerString result = Instancio.of(SortedMapIntegerString.class)
                    .generate(all(SortedMap.class), gen -> gen.map()
                            .minSize(EXPECTED_SIZE)) // .type() not specified
                    .create();

            assertThat(result.getMap()).hasSizeBetween(EXPECTED_SIZE, EXPECTED_SIZE);
        }

    }

    @Nested
    class ListTest {

        @Test
        @DisplayName("List should be of expected type")
        void listWithSubType() {
            final TwoListsOfItemString result = Instancio.of(TwoListsOfItemString.class)
                    .generate(field("list1"), gen -> gen.collection().type(Vector.class))
                    .create();

            assertThat(result.getList1()).isNotEmpty().isInstanceOf(Vector.class)
                    .allSatisfy(item -> assertThat(item.getValue()).isNotBlank());

            assertThat(result.getList2()).isNotEmpty().isInstanceOf(ArrayList.class)
                    .allSatisfy(item -> assertThat(item.getValue()).isNotBlank());
        }

        @Test
        void withElements() {
            final String[] expectedElements = {"foo", "bar"};
            final ListString result = Instancio.of(ListString.class)
                    .generate(field("list"), gen -> gen.collection().with(expectedElements))
                    .create();

            assertThat(result.getList()).contains(expectedElements);
        }

        @Test
        @DisplayName("List of the target field should have expected size and be fully populated")
        void listShouldHaveExpectedSize() {
            final TwoListsOfItemString result = Instancio.of(TwoListsOfItemString.class)
                    .generate(field("list1"), gen -> gen.collection().size(EXPECTED_SIZE))
                    .create();

            assertList(result.getList1(), EXPECTED_SIZE, EXPECTED_SIZE);
            assertList(result.getList2(), Constants.MIN_SIZE, Constants.MAX_SIZE);
        }

        @Test
        @DisplayName("All lists should have expected size and be fully populated")
        void allListsShouldHaveExpectedSize() {
            final TwoListsOfItemString result = Instancio.of(TwoListsOfItemString.class)
                    .generate(all(List.class), gen -> gen.collection().size(EXPECTED_SIZE))
                    .create();

            assertList(result.getList1(), EXPECTED_SIZE, EXPECTED_SIZE);
            assertList(result.getList2(), EXPECTED_SIZE, EXPECTED_SIZE);
        }

        private void assertList(final List<Item<String>> list, final int minSize, final int maxSize) {
            assertThat(list)
                    .hasSizeBetween(minSize, maxSize)
                    .allSatisfy(item -> assertThat(item.getValue()).isInstanceOf(String.class));
        }
    }

    @Nested
    class MapTest {

        @Test
        @DisplayName("Map of the target field should have expected size and be fully populated")
        void mapShouldHaveExpectedSize() {
            final TwoMapsOfIntegerItemString result = Instancio.of(TwoMapsOfIntegerItemString.class)
                    .generate(field("map1"), gen -> gen.map().size(EXPECTED_SIZE))
                    .create();

            assertEntries(result.getMap1(), EXPECTED_SIZE, EXPECTED_SIZE);
            assertEntries(result.getMap2(), Constants.MIN_SIZE, Constants.MAX_SIZE);
        }

        @Test
        @DisplayName("All maps should have expected size and be fully populated")
        void allMapsShouldHaveExpectedSize() {
            final TwoMapsOfIntegerItemString result = Instancio.of(TwoMapsOfIntegerItemString.class)
                    .generate(all(Map.class), gen -> gen.map().size(EXPECTED_SIZE))
                    .create();

            assertEntries(result.getMap1(), EXPECTED_SIZE, EXPECTED_SIZE);
            assertEntries(result.getMap2(), EXPECTED_SIZE, EXPECTED_SIZE);
        }

        private void assertEntries(final Map<Integer, Item<String>> map, final int minSize, final int maxSize) {
            assertThat(map).hasSizeBetween(minSize, maxSize);
            assertThat(map.entrySet()).allSatisfy(entry -> {
                assertThat(entry.getKey()).isInstanceOf(Integer.class);
                assertThat(entry.getValue().getValue()).isInstanceOf(String.class); // item.value
            });
        }
    }

}
