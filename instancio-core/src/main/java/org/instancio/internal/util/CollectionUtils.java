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
package org.instancio.internal.util;

import org.instancio.Random;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class CollectionUtils {
    private CollectionUtils() {
        // non-instantiable
    }

    public static boolean isNullOrEmpty(@Nullable final Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNullOrEmpty(@Nullable final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public static <T> T lastElement(final List<? super T> list) {
        return (T) list.get(list.size() - 1);
    }

    @SafeVarargs
    public static <T> List<T> asUnmodifiableList(final T... values) {
        return Collections.unmodifiableList(asArrayList(values));
    }

    @SafeVarargs
    public static <T> List<T> asArrayList(final T... values) {
        return values == null
                ? new ArrayList<>()
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

    public static void shuffle(final Collection<Object> collection, final Random random) {
        if (collection.isEmpty()) {
            return;
        } else if (collection instanceof List) {
            shuffleList((List<Object>) collection, random);
            return;
        }

        final List<Object> list = new ArrayList<>(collection);
        shuffleList(list, random);
        collection.clear();
        collection.addAll(list);
    }

    private static void shuffleList(final List<Object> list, final Random random) {
        for (int i = 0; i < list.size(); i++) {
            final int r = random.intRange(0, i);
            final Object tmp = list.get(i);
            list.set(i, list.get(r));
            list.set(r, tmp);
        }
    }
}
