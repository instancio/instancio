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
package org.instancio.test.features.generator;

import org.apache.commons.lang3.RandomUtils;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.collections.TwoStringCollections;
import org.instancio.test.support.pojo.collections.lists.ListString;
import org.instancio.test.support.pojo.collections.lists.TwoListsOfItemString;
import org.instancio.test.support.pojo.collections.maps.SortedMapIntegerString;
import org.instancio.test.support.pojo.collections.maps.TwoMapsOfIntegerItemString;
import org.instancio.test.support.pojo.collections.sets.HashSetLong;
import org.instancio.test.support.pojo.collections.sets.SetLong;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.Vector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

// TODO test nested lists/maps
@FeatureTag(Feature.GENERATE)
class BuiltInCollectionGeneratorTest {
    private static final int EXPECTED_SIZE = RandomUtils.nextInt(90, 100);

    @Nested
    @DisplayName("Tests with unspecified collection().subtype()")
    @ExtendWith(InstancioExtension.class)
    class UnspecifiedCollectionSubtypeTest {

        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.INTEGER_MIN, Integer.MIN_VALUE)
                .set(Keys.INTEGER_MAX, Integer.MAX_VALUE)
                .set(Keys.LONG_MIN, Long.MIN_VALUE)
                .set(Keys.LONG_MAX, Long.MAX_VALUE);

        @Test
        @DisplayName("Selecting supertype (Set) does not apply to subtype (HashSet)")
        void selectSupertypeDoesNotIncludeSubtype() {
            final int size = 100;
            final HashSetLong result = Instancio.of(HashSetLong.class)
                    .lenient()
                    .generate(all(Set.class), gen -> gen.collection().size(size))
                    .create();

            assertThatObject(result).isFullyPopulated();
            assertThat(result.getSet())
                    .as("Selecting 'Set' should not include 'HashSet'")
                    .hasSizeLessThan(size);
        }

        @Test
        @DisplayName("When collection subtype not specified: field type HashSet")
        void collectionTypeNotSpecifiedWithHashSetField() {
            final int minSize = 100;

            final HashSetLong result = Instancio.of(HashSetLong.class)
                    .generate(all(HashSet.class), gen -> gen.collection().minSize(minSize)) //.subtype() not specified
                    .create();

            assertThat(result.getSet()).hasSizeGreaterThanOrEqualTo(minSize);
        }

        @Test
        @DisplayName("When collection subtype not specified: field type Set")
        void collectionTypeNotSpecifiedWithSetField() {
            final int minSize = 100;

            final SetLong result = Instancio.of(SetLong.class)
                    .generate(all(Set.class), gen -> gen.collection().minSize(minSize)) //.subtype() not specified
                    .create();

            assertThat(result.getSet()).hasSizeGreaterThanOrEqualTo(minSize);
        }

        @Test
        @DisplayName("When collection subtype not specified: field type Collection")
        void collectionTypeNotSpecifiedWithCollectionField() {
            final int minSize = 100;

            final TwoStringCollections result = Instancio.of(TwoStringCollections.class)
                    .generate(all(Collection.class), gen -> gen.collection().minSize(minSize)) //.subtype() not specified
                    .create();

            assertThat(result.getOne()).hasSizeGreaterThanOrEqualTo(minSize);
            assertThat(result.getTwo()).hasSizeGreaterThanOrEqualTo(minSize);
        }

        @Test
        @DisplayName("When map subtype not specified: field type SortedMap")
        void mapTypeNotSpecifiedWithSortedMapField() {
            final SortedMapIntegerString result = Instancio.of(SortedMapIntegerString.class)
                    .generate(all(SortedMap.class), gen -> gen.map()
                            .size(EXPECTED_SIZE)) // .subtype() not specified
                    .create();

            assertThat(result.getMap()).hasSize(EXPECTED_SIZE);
        }
    }

    @Nested
    class ListTest {

        @Test
        @DisplayName("List should be of expected type")
        void listWithSubType() {
            final TwoListsOfItemString result = Instancio.of(TwoListsOfItemString.class)
                    .generate(field("list1"), gen -> gen.collection().subtype(Vector.class))
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
    @ExtendWith(InstancioExtension.class)
    class MapTest {

        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.INTEGER_MIN, Integer.MIN_VALUE)
                .set(Keys.INTEGER_MAX, Integer.MAX_VALUE);

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