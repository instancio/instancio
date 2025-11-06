/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.internal.util;

import org.instancio.Random;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class CollectionUtils {

    private static final int DEFAULT_INITIAL_CAPACITY = 4;

    private CollectionUtils() {
        // non-instantiable
    }

    public static boolean isNullOrEmpty(@Nullable final Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNullOrEmpty(@Nullable final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    @SafeVarargs
    public static <T> List<T> asUnmodifiableList(final T... values) {
        return Collections.unmodifiableList(asArrayList(values));
    }

    public static <T> List<T> asUnmodifiableList(final List<T> list) {
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }

    public static <T> Set<T> asUnmodifiableSet(final Set<T> set) {
        return set == null ? Collections.emptySet() : Collections.unmodifiableSet(set);
    }

    public static <K, V> Map<K, V> asUnmodifiableMap(final Map<K, V> map) {
        return map == null ? Collections.emptyMap() : Collections.unmodifiableMap(map);
    }

    public static <K, E> Map<K, List<E>> asUnmodifiableLinkedHashMapOfLists(final Map<K, List<E>> map) {
        if (map == null) {
            return Collections.emptyMap();
        }
        final Map<K, List<E>> copy = new LinkedHashMap<>(map.size());
        for (Map.Entry<K, List<E>> entry : map.entrySet()) {
            copy.put(entry.getKey(), asUnmodifiableList(entry.getValue()));
        }
        return Collections.unmodifiableMap(copy);
    }

    public static <K, E> Map<K, List<E>> copyAsLinkedHashMap(final Map<K, List<E>> map) {
        if (map == null) {
            return new LinkedHashMap<>(0);
        }
        final Map<K, List<E>> copy = new LinkedHashMap<>(map.size());
        for (Map.Entry<K, List<E>> entry : map.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }

    public static <K, V> Map<K, V> newLinkedHashMapIfNull(final Map<K, V> map) {
        return map != null ? map : new LinkedHashMap<>(DEFAULT_INITIAL_CAPACITY);
    }

    public static <T> Set<T> newLinkedHashSetIfNull(final Set<T> set) {
        return set != null ? set : new LinkedHashSet<>(DEFAULT_INITIAL_CAPACITY);
    }

    @SafeVarargs
    public static <T> List<T> asArrayList(final T... values) {
        return values == null
                ? new ArrayList<>(DEFAULT_INITIAL_CAPACITY)
                : new ArrayList<>(Arrays.asList(values));
    }

    @SafeVarargs
    public static <T> Set<T> asSet(final T... values) {
        if (values == null) {
            return Collections.emptySet();
        }
        final Set<T> set = new HashSet<>(values.length);
        Collections.addAll(set, values);
        return set;
    }

    @SafeVarargs
    public static <K, V> Map<K, V> asLinkedHashMap(final Function<V, K> keyFn, final V... values) {
        if (values == null) return Collections.emptyMap();
        final Map<K, V> map = new LinkedHashMap<>(values.length);
        for (V value : values) {
            final K key = keyFn.apply(value);
            map.put(key, value);
        }
        return map;
    }

    @SafeVarargs
    public static <T> List<T> combine(final List<T> list, final T... values) {
        final List<T> result = new ArrayList<>(list);
        Collections.addAll(result, values);
        return Collections.unmodifiableList(result);
    }

    public static <T> List<T> flatMap(List<List<T>> lists) {
        if (lists.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> results = new ArrayList<>();
        for (List<T> list : lists) {
            results.addAll(list);
        }
        return results;
    }

    // same as List.indexOf() but using '==' instead of equals()
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public static int identityIndexOf(final Object obj, @NotNull final List<?> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == obj) {
                return i;
            }
        }
        return -1;
    }

    public static <T> void shuffle(final Collection<T> collection, final Random random) {
        if (collection.isEmpty()) {
            return;
        } else if (collection instanceof List<T> list) {
            shuffleList(list, random);
            return;
        }

        final List<T> list = new ArrayList<>(collection);
        shuffleList(list, random);
        collection.clear();
        collection.addAll(list);
    }

    private static <T> void shuffleList(final List<T> list, final Random random) {
        for (int i = 0; i < list.size(); i++) {
            final int r = random.intRange(0, i);
            final T tmp = list.get(i);
            list.set(i, list.get(r));
            list.set(r, tmp);
        }
    }
}
