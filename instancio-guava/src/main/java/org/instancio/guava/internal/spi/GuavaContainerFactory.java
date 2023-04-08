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
package org.instancio.guava.internal.spi;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.Table;
import com.google.common.collect.TreeMultimap;
import com.google.common.collect.TreeMultiset;
import org.instancio.internal.spi.InternalContainerFactoryProvider;
import org.instancio.internal.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static org.instancio.guava.internal.util.GuavaFunctions.fromCollection;
import static org.instancio.guava.internal.util.GuavaFunctions.fromMap;
import static org.instancio.guava.internal.util.GuavaFunctions.fromMultimap;
import static org.instancio.guava.internal.util.GuavaFunctions.fromTable;

@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.ExcessiveImports"})
public class GuavaContainerFactory implements InternalContainerFactoryProvider {
    private static final Map<Class<?>, Function<?, ?>> MAPPING_FUNCTIONS = getMappingFunctions();
    private static final Set<Class<?>> CONTAINER_CLASSES = getContainerClasses();

    private static Map<Class<?>, Function<?, ?>> getMappingFunctions() {
        final Map<Class<?>, Function<?, ?>> map = new HashMap<>();

        // Collections
        map.put(ConcurrentHashMultiset.class, fromCollection(ConcurrentHashMultiset::create));
        map.put(HashMultiset.class, fromCollection(HashMultiset::create));
        map.put(ImmutableList.class, fromCollection(ImmutableList::copyOf));
        map.put(ImmutableMultiset.class, fromCollection(ImmutableMultiset::copyOf));
        map.put(ImmutableSet.class, fromCollection(ImmutableSet::copyOf));
        map.put(ImmutableSortedMultiset.class, fromCollection(ImmutableSortedMultiset::copyOf));
        map.put(ImmutableSortedSet.class, fromCollection(ImmutableSortedSet::copyOf));
        map.put(LinkedHashMultiset.class, fromCollection(LinkedHashMultiset::create));
        map.put(Multiset.class, fromCollection(HashMultiset::create));
        map.put(SortedMultiset.class, fromCollection(TreeMultiset::create));
        map.put(TreeMultiset.class, fromCollection(TreeMultiset::create));

        // Maps
        map.put(BiMap.class, fromMap(HashBiMap::create));
        map.put(HashBiMap.class, fromMap(HashBiMap::create));
        map.put(ImmutableBiMap.class, fromMap(ImmutableBiMap::copyOf));
        map.put(ImmutableMap.class, fromMap(ImmutableMap::copyOf));
        map.put(ImmutableSortedMap.class, fromMap(ImmutableSortedMap::copyOf));

        // Multimaps
        map.put(HashMultimap.class, fromMultimap(HashMultimap::create));
        map.put(ImmutableListMultimap.class, fromMultimap(ImmutableListMultimap::copyOf));
        map.put(ImmutableMultimap.class, fromMultimap(ImmutableMultimap::copyOf));
        map.put(ImmutableSetMultimap.class, fromMultimap(ImmutableSetMultimap::copyOf));
        map.put(LinkedHashMultimap.class, fromMultimap(LinkedHashMultimap::create));
        map.put(LinkedListMultimap.class, fromMultimap(LinkedListMultimap::create));
        map.put(SetMultimap.class, fromMultimap(HashMultimap::create));
        map.put(SortedSetMultimap.class, fromMultimap(TreeMultimap::create));
        map.put(TreeMultimap.class, fromMultimap(TreeMultimap::create));

        // Tables
        map.put(ImmutableTable.class, fromTable(ImmutableTable::copyOf));

        return Collections.unmodifiableMap(map);
    }

    private static Set<Class<?>> getContainerClasses() {
        return Collections.unmodifiableSet(CollectionUtils.asSet(
                ArrayListMultimap.class,
                HashMultimap.class,
                ImmutableListMultimap.class,
                ImmutableMultimap.class,
                ImmutableSetMultimap.class,
                ImmutableTable.class,
                LinkedHashMultimap.class,
                LinkedListMultimap.class,
                ListMultimap.class,
                SetMultimap.class,
                SortedSetMultimap.class,
                Table.class,
                TreeMultimap.class
        ));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, R> Function<T, R> getMappingFunction(Class<R> type, List<Class<?>> typeArguments) {
        return (Function<T, R>) MAPPING_FUNCTIONS.get(type);
    }

    @Override
    public boolean isContainer(final Class<?> type) {
        return CONTAINER_CLASSES.contains(type);
    }
}
