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
package org.instancio.internal.util;

import org.instancio.Random;
import org.instancio.documentation.Contract;
import org.jspecify.annotations.Nullable;

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

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class CollectionUtils {

    private static final int DEFAULT_INITIAL_CAPACITY = 4;

    private CollectionUtils() {
        // non-instantiable
    }

    @Contract("null -> true")
    public static boolean isNullOrEmpty(@Nullable final Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    @Contract("null -> true")
    public static boolean isNullOrEmpty(@Nullable final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    @Contract("null -> !null")
    @SafeVarargs
    public static <T extends @Nullable Object> List<T> asUnmodifiableList(@Nullable final T @Nullable ... values) {
        return Collections.unmodifiableList(asArrayList(values));
    }

    @Contract("null -> !null")
    public static <T> List<T> asUnmodifiableList(@Nullable final List<T> list) {
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }

    @Contract("null -> !null")
    public static <T> Set<T> asUnmodifiableSet(@Nullable final Set<T> set) {
        return set == null ? Collections.emptySet() : Collections.unmodifiableSet(set);
    }

    @Contract("null -> !null")
    public static <K, V> Map<K, V> asUnmodifiableMap(@Nullable final Map<K, V> map) {
        return map == null ? Collections.emptyMap() : Collections.unmodifiableMap(map);
    }

    @Contract("null -> !null")
    public static <K, E> Map<K, List<E>> asUnmodifiableLinkedHashMapOfLists(@Nullable final Map<K, List<E>> map) {
        if (map == null) {
            return Collections.emptyMap();
        }
        final Map<K, List<E>> copy = new LinkedHashMap<>(map.size());
        for (Map.Entry<K, List<E>> entry : map.entrySet()) {
            copy.put(entry.getKey(), asUnmodifiableList(entry.getValue()));
        }
        return Collections.unmodifiableMap(copy);
    }

    @Contract("null -> !null")
    public static <K, E> Map<K, List<E>> copyAsLinkedHashMap(@Nullable final Map<K, List<E>> map) {
        if (map == null) {
            return new LinkedHashMap<>(0);
        }
        final Map<K, List<E>> copy = new LinkedHashMap<>(map.size());
        for (Map.Entry<K, List<E>> entry : map.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }

    @Contract("!null -> param1; null -> !null")
    public static <K, V> Map<K, V> newLinkedHashMapIfNull(@Nullable final Map<K, V> map) {
        return map != null ? map : new LinkedHashMap<>(DEFAULT_INITIAL_CAPACITY);
    }

    @Contract("!null -> param1; null -> !null")
    public static <T> Set<T> newLinkedHashSetIfNull(@Nullable final Set<T> set) {
        return set != null ? set : new LinkedHashSet<>(DEFAULT_INITIAL_CAPACITY);
    }

    @Contract("null -> !null")
    @SafeVarargs
    public static <T> List<T> asArrayList(@Nullable final T @Nullable ... values) {
        return values == null
                ? new ArrayList<>(DEFAULT_INITIAL_CAPACITY)
                : new ArrayList<>(Arrays.asList(values));
    }

    @Contract("null -> !null")
    @SafeVarargs
    public static <T> Set<T> asSet(@Nullable final T @Nullable ... values) {
        if (values == null) {
            return Collections.emptySet();
        }
        final Set<T> set = new HashSet<>(values.length);
        Collections.addAll(set, values);
        return set;
    }

    @Contract("_, null -> !null")
    @SafeVarargs
    public static <K, V extends @Nullable Object> Map<K, V> asLinkedHashMap(
            final Function<V, K> keyFn,
            final V @Nullable ... values) {

        if (values == null) return Collections.emptyMap();
        final Map<K, V> map = new LinkedHashMap<>(values.length);
        for (V value : values) {
            final K key = keyFn.apply(value);
            map.put(key, value);
        }
        return map;
    }

    @Contract("_, _ -> !null")
    @SafeVarargs
    public static <T> List<T> combine(final List<T> list, final T... values) {
        final List<T> result = new ArrayList<>(list);
        Collections.addAll(result, values);
        return Collections.unmodifiableList(result);
    }

    @Contract("_ -> !null")
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
    public static int identityIndexOf(final Object obj, final List<?> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == obj) {
                return i;
            }
        }
        return -1;
    }

    public static <T extends @Nullable Object> void shuffle(final Collection<T> collection, final Random random) {
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

    private static <T extends @Nullable Object> void shuffleList(final List<T> list, final Random random) {
        for (int i = 0; i < list.size(); i++) {
            final int r = random.intRange(0, i);
            final T tmp = list.get(i);
            list.set(i, list.get(r));
            list.set(r, tmp);
        }
    }
}
