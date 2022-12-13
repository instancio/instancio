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
package org.instancio.guava;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Table;
import org.instancio.internal.spi.InternalContainerFactoryProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class GuavaContainerFactory implements InternalContainerFactoryProvider {
    private static final Map<Class<?>, Function<?, ?>> FACTORY_FN_MAP = getContainerFactoryFunctions();
    private static final Set<Class<?>> CONTAINER_CLASSES = getContainerClasses();

    private static Map<Class<?>, Function<?, ?>> getContainerFactoryFunctions() {
        final Map<Class<?>, Function<?, ?>> map = new HashMap<>();
        map.put(ImmutableList.class, (Function<List<?>, ImmutableList<?>>) ImmutableList::copyOf);
        map.put(ImmutableMap.class, (Function<Map<?, ?>, ImmutableMap<?, ?>>) ImmutableMap::copyOf);
        map.put(ImmutableListMultimap.class, (Function<ListMultimap<?, ?>, ImmutableListMultimap<?, ?>>) ImmutableListMultimap::copyOf);
        return Collections.unmodifiableMap(map);
    }

    private static Set<Class<?>> getContainerClasses() {
        final Set<Class<?>> classes = new HashSet<>();
        classes.add(Table.class);
        classes.add(ListMultimap.class);
        classes.add(ImmutableListMultimap.class);
        return Collections.unmodifiableSet(classes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, R> Function<T, R> createFromOtherFunction(Class<R> type) {
        return (Function<T, R>) FACTORY_FN_MAP.get(type);
    }

    @Override
    public boolean isContainerClass(final Class<?> type) {
        return CONTAINER_CLASSES.contains(type);
    }
}
