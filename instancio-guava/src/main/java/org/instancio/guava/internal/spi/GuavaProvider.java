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
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Range;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.Table;
import com.google.common.collect.TreeMultimap;
import com.google.common.collect.TreeMultiset;
import com.google.common.net.HostAndPort;
import com.google.common.net.InternetDomainName;
import org.instancio.Node;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generators.Generators;
import org.instancio.guava.internal.generator.GuavaArrayListMultimapGenerator;
import org.instancio.guava.internal.generator.GuavaHashBasedTableGenerator;
import org.instancio.guava.internal.generator.GuavaHostAndPortGenerator;
import org.instancio.guava.internal.generator.GuavaInternetDomainNameGenerator;
import org.instancio.guava.internal.generator.GuavaRangeGenerator;
import org.instancio.internal.generator.util.CollectionGenerator;
import org.instancio.internal.generator.util.MapGenerator;
import org.instancio.spi.InstancioServiceProvider;
import org.instancio.spi.ServiceProviderContext;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.ExcessiveImports"})
public class GuavaProvider implements InstancioServiceProvider {

    private GeneratorContext generatorContext;

    @Override
    public void init(final ServiceProviderContext providerContext) {
        this.generatorContext = new GeneratorContext(
                providerContext.getSettings(),
                providerContext.random());
    }

    @Override
    public GeneratorProvider getGeneratorProvider() {
        final Map<Class<?>, Generator<?>> generators = new HashMap<>();

        // Collections
        final Generator<?> collectionGenerator = new CollectionGenerator<>(generatorContext);
        generators.put(ConcurrentHashMultiset.class, collectionGenerator);
        generators.put(HashMultiset.class, collectionGenerator);
        generators.put(ImmutableList.class, collectionGenerator);
        generators.put(ImmutableMultiset.class, collectionGenerator);
        generators.put(ImmutableSet.class, collectionGenerator);
        generators.put(ImmutableSortedMultiset.class, collectionGenerator);
        generators.put(ImmutableSortedSet.class, collectionGenerator);
        generators.put(LinkedHashMultiset.class, collectionGenerator);
        generators.put(Multiset.class, collectionGenerator);
        generators.put(SortedMultiset.class, collectionGenerator);
        generators.put(TreeMultiset.class, collectionGenerator);

        // Maps
        final Generator<?> mapGenerator = new MapGenerator<>(generatorContext);
        generators.put(BiMap.class, mapGenerator);
        generators.put(HashBiMap.class, mapGenerator);
        generators.put(ImmutableBiMap.class, mapGenerator);
        generators.put(ImmutableMap.class, mapGenerator);
        generators.put(ImmutableSortedMap.class, mapGenerator);

        // Multimaps
        final Generator<?> multimapGenerator = new GuavaArrayListMultimapGenerator<>();
        generators.put(ArrayListMultimap.class, multimapGenerator);
        generators.put(HashMultimap.class, multimapGenerator);
        generators.put(ImmutableListMultimap.class, multimapGenerator);
        generators.put(ImmutableMultimap.class, multimapGenerator);
        generators.put(ImmutableSetMultimap.class, multimapGenerator);
        generators.put(LinkedHashMultimap.class, multimapGenerator);
        generators.put(LinkedListMultimap.class, multimapGenerator);
        generators.put(ListMultimap.class, multimapGenerator);
        generators.put(Multimap.class, multimapGenerator);
        generators.put(SetMultimap.class, multimapGenerator);
        generators.put(SortedSetMultimap.class, multimapGenerator);
        generators.put(TreeMultimap.class, multimapGenerator);

        // Tables
        final Generator<?> tableGenerator = new GuavaHashBasedTableGenerator<>();
        generators.put(ImmutableTable.class, tableGenerator);
        generators.put(Table.class, tableGenerator);

        // Net
        generators.put(InternetDomainName.class, new GuavaInternetDomainNameGenerator());
        generators.put(HostAndPort.class, new GuavaHostAndPortGenerator());

        // Range
        generators.put(Range.class, new GuavaRangeGenerator<>());

        return (Node node, Generators gen) -> generators.get(node.getTargetClass());
    }
}
