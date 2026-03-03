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
package org.instancio.internal.context;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * A bare-bones sorted set that preserves the insertion order of the elements.
 *
 * @param <E> the element type
 */
class SortedSetWithReverseInsertionOrder<E> implements Set<E> {

    private final Set<Entry<E>> set;

    /**
     * The elements are ordered using the specified comparator,
     * and <b>reverse</b> insertion order.
     *
     * @param comparator for comparing the elements
     */
    SortedSetWithReverseInsertionOrder(final Comparator<E> comparator) {
        set = new TreeSet<>((o1, o2) -> {
            final int c = comparator.compare(o1.element, o2.element);
            if (c == 0) {
                return o2.insertionOrder - o1.insertionOrder;
            }
            return c;
        });
    }

    @Override
    public boolean add(final E e) {
        return set.add(new Entry<>(size(), e));
    }

    @Override
    public Iterator<E> iterator() {
        return new IteratorImpl<>(set.iterator());
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    private static final class Entry<E> {
        private final int insertionOrder;
        private final E element;

        private Entry(final int insertionOrder, final E element) {
            this.insertionOrder = insertionOrder;
            this.element = element;
        }
    }

    private static final class IteratorImpl<E> implements Iterator<E> {
        private final Iterator<Entry<E>> delegate;

        private IteratorImpl(final Iterator<Entry<E>> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }

        @Override
        public E next() {
            return delegate.next().element;
        }
    }
}
