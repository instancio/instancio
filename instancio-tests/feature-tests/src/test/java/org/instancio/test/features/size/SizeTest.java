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
package org.instancio.test.features.size;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.PredicateSelector;
import org.instancio.Size;
import org.instancio.exception.InstancioApiException;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.arrays.ArrayLong;
import org.instancio.test.support.pojo.collections.lists.ListString;
import org.instancio.test.support.pojo.collections.lists.TwoListsOfInteger;
import org.instancio.test.support.pojo.collections.maps.MapIntegerArrayString;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.root;

@FeatureTag(Feature.SIZE)
@ExtendWith(InstancioExtension.class)
class SizeTest {

    private static final int SIZE = 7;

    @Nested
    class SizeInteger {

        @Test
        void array() {
            final ArrayLong result = Instancio.of(ArrayLong.class)
                    .size(field(ArrayLong::getWrapper), SIZE)
                    .create();

            assertThat(result.getWrapper()).hasSize(SIZE);
        }

        @Test
        void collection() {
            final ListString result = Instancio.of(ListString.class)
                    .size(field(ListString::getList), SIZE)
                    .create();

            assertThat(result.getList()).hasSize(SIZE);
        }

        @Test
        void map() {
            final MapIntegerArrayString result = Instancio.of(MapIntegerArrayString.class)
                    .size(field(MapIntegerArrayString::getMap), SIZE)
                    .create();

            assertThat(result.getMap()).hasSize(SIZE);
        }
    }

    @Nested
    class SizeObjectExact {

        @Test
        void array() {
            final ArrayLong result = Instancio.of(ArrayLong.class)
                    .size(field(ArrayLong::getWrapper), Size.of(SIZE))
                    .create();

            assertThat(result.getWrapper()).hasSize(SIZE);
        }

        @Test
        void collection() {
            final ListString result = Instancio.of(ListString.class)
                    .size(field(ListString::getList), Size.of(SIZE))
                    .create();

            assertThat(result.getList()).hasSize(SIZE);
        }

        @Test
        void map() {
            final MapIntegerArrayString result = Instancio.of(MapIntegerArrayString.class)
                    .size(field(MapIntegerArrayString::getMap), Size.of(SIZE))
                    .create();

            assertThat(result.getMap()).hasSize(SIZE);
        }
    }

    @Nested
    class SizeObjectRange {

        @RepeatedTest(Constants.SAMPLE_SIZE_D)
        void array() {
            final ArrayLong result = Instancio.of(ArrayLong.class)
                    .size(field(ArrayLong::getWrapper), Size.range(0, SIZE))
                    .create();

            assertThat(result.getWrapper()).hasSizeBetween(0, SIZE);
        }

        @RepeatedTest(Constants.SAMPLE_SIZE_D)
        void collection() {
            final ListString result = Instancio.of(ListString.class)
                    .size(field(ListString::getList), Size.range(0, SIZE))
                    .create();

            assertThat(result.getList()).hasSizeBetween(0, SIZE);
        }

        @RepeatedTest(Constants.SAMPLE_SIZE_D)
        void map() {
            final MapIntegerArrayString result = Instancio.of(MapIntegerArrayString.class)
                    .size(field(MapIntegerArrayString::getMap), Size.range(0, SIZE))
                    .create();

            assertThat(result.getMap()).hasSizeBetween(0, SIZE);
        }
    }

    @Test
    void ofListAndAllListSize() {
        final int nestedListSize = SIZE + 5;

        final List<ListString> result = Instancio.ofList(ListString.class)
                .size(SIZE)
                .size(all(List.class), nestedListSize)
                .create();

        assertThat(result)
                .hasSize(SIZE)
                .extracting(ListString::getList)
                .allMatch(list -> list.size() == nestedListSize);
    }

    @Test
    void ofListRootSize() {
        final int sizeOverride = SIZE + 5;

        final List<ListString> result = Instancio.ofList(ListString.class)
                .size(SIZE)
                .size(root(), sizeOverride)
                .create();

        assertThat(result)
                .hasSize(sizeOverride)
                .extracting(ListString::getList)
                // random sizes based on default settings
                .noneMatch(list -> list.size() == sizeOverride);
    }

    @Test
    void allListsInObject() {
        final TwoListsOfInteger result = Instancio.of(TwoListsOfInteger.class)
                .size(all(List.class), SIZE)
                .create();

        assertThat(result.getList1()).hasSize(SIZE);
        assertThat(result.getList2()).hasSize(SIZE);
    }

    @Test
    void enumSet() {
        record Holder(EnumSet<Gender> genders) {}

        final Holder result = Instancio.of(Holder.class)
                .size(field(Holder::genders), 1)
                .create();

        assertThat(result.genders()).hasSize(1);
    }

    @Nested
    class Validation {

        @Test
        void unusedSelectorErrorWhenSizeSelectorHasNoMatch() {
            final InstancioApi<ListString> api = Instancio.of(ListString.class)
                    .size(all(Set.class), 3);

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(UnusedSelectorException.class)
                    .hasMessageContaining("Unused selector in: size()");
        }

        @Test
        void arrayWith_exceedsSize() {
            final InstancioApi<ArrayLong> api = Instancio.of(ArrayLong.class)
                    .size(field(ArrayLong::getWrapper), 2)
                    .generate(field(ArrayLong::getWrapper), gen -> gen.array().with(10L, 20L, 30L));

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("array generator specifies more 'with()' elements (3) than the size() override allows (2)");
        }

        @Test
        void negativeIntThrowsAtCallTime() {
            final InstancioApi<Person> api = Instancio.of(Person.class);
            final PredicateSelector selector = field(Address::getPhoneNumbers);

            assertThatThrownBy(() -> api.size(selector, -1))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("size must not be negative: -1");
        }

        @Test
        void sizeRangeWithMinGreaterThanMaxThrowsAtCallTime() {
            assertThatThrownBy(() -> Size.range(5, 2))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("min (5) must be less than or equal to max (2)");
        }

        @Test
        void sizeRangeWithNegativeMinThrowsAtCallTime() {
            assertThatThrownBy(() -> Size.range(-1, 5))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("size must not be negative: -1");
        }
    }
}
