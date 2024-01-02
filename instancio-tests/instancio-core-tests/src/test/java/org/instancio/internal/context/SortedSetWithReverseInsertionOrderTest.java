/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.internal.context;

import org.instancio.test.support.pojo.generics.basic.Item;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SortedSetWithReverseInsertionOrderTest {

    private static final Item<Integer> ITEM = new Item<>(1);
    private static final Set<Item<Integer>> ITEM_SET = Collections.singleton(ITEM);

    private final Set<Item<Integer>> set = new SortedSetWithReverseInsertionOrder<>(
            Comparator.comparingInt(Item::getValue));

    @Test
    void iterationOrder() {
        final Item<Integer> item4 = new Item<>(4);
        final Item<Integer> item2b = new Item<>(2);
        final Item<Integer> item1a = new Item<>(1);
        final Item<Integer> item2a = new Item<>(2);
        final Item<Integer> item3 = new Item<>(3);
        final Item<Integer> item1b = new Item<>(1);
        final Item<Integer> item2c = new Item<>(2);

        set.add(item4);
        set.add(item2b);
        set.add(item1a);
        set.add(item2a);
        set.add(item3);
        set.add(item1b);
        set.add(item2c);

        final Iterator<Item<Integer>> iterator = set.iterator();

        assertThat(iterator.next()).isSameAs(item1b);
        assertThat(iterator.next()).isSameAs(item1a);
        assertThat(iterator.next()).isSameAs(item2c);
        assertThat(iterator.next()).isSameAs(item2a);
        assertThat(iterator.next()).isSameAs(item2b);
        assertThat(iterator.next()).isSameAs(item3);
        assertThat(iterator.next()).isSameAs(item4);
        assertThat(iterator).isExhausted();
    }

    @Test
    void sizeAndIsEmpty() {
        assertThat(set)
                .hasSize(0) // NOSONAR
                .isEmpty();

        set.add(ITEM);

        assertThat(set)
                .isNotEmpty()
                .hasSize(1);
    }

    @Nested
    class UnsupportedMethodsTest {
        @Test
        void contains() {
            assertThatThrownBy(() -> set.contains(ITEM))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        void toObjectArray() {
            assertThatThrownBy(() -> set.toArray())
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        void toArray() {
            assertThatThrownBy(() -> set.toArray(new Item[0]))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        void remove() {
            assertThatThrownBy(() -> set.remove(ITEM))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        void containsAll() {
            assertThatThrownBy(() -> set.containsAll(ITEM_SET))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        void addAll() {
            assertThatThrownBy(() -> set.addAll(ITEM_SET))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        void retainAll() {
            assertThatThrownBy(() -> set.retainAll(ITEM_SET))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        void removeAll() {
            assertThatThrownBy(() -> set.removeAll(ITEM_SET))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        void clear() {
            assertThatThrownBy(set::clear)
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        void verifyEquals() {
            assertThatThrownBy(() -> set.equals(ITEM))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        void verifyHashCode() {
            assertThatThrownBy(set::hashCode)
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
