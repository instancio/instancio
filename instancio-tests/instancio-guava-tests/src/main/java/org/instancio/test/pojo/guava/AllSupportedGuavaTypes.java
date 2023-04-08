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
package org.instancio.test.pojo.guava;

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
import com.google.common.collect.Range;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.Table;
import com.google.common.collect.TreeMultimap;
import com.google.common.collect.TreeMultiset;
import com.google.common.net.HostAndPort;
import com.google.common.net.InternetDomainName;
import lombok.Value;

import java.util.UUID;

@Value
public class AllSupportedGuavaTypes {

    // Collect
    ArrayListMultimap<UUID, String> arrayListMultimap;
    BiMap<UUID, String> biMap;
    HashBiMap<UUID, String> hashBiMap;
    HashMultimap<UUID, String> hashMultimap;
    HashMultiset<UUID> hashMultiset;
    ImmutableBiMap<UUID, String> immutableBiMap;
    ImmutableList<UUID> immutableList;
    ImmutableListMultimap<UUID, String> immutableListMultimap;
    ImmutableMap<UUID, String> immutableMap;
    ImmutableMultimap<UUID, String> immutableMultimap;
    ImmutableMultiset<UUID> immutableMultiset;
    ImmutableSet<UUID> immutableSet;
    ImmutableSetMultimap<UUID, String> immutableSetMultimap;
    ImmutableSortedMap<UUID, String> immutableSortedMap;
    ImmutableSortedMultiset<UUID> immutableSortedMultiset;
    ImmutableSortedSet<UUID> immutableSortedSet;
    ImmutableTable<String, Integer, Long> immutableTable;
    LinkedHashMultimap<UUID, String> linkedHashMultimap;
    LinkedHashMultiset<UUID> linkedHashMultiset;
    LinkedListMultimap<UUID, String> linkedListMultimap;
    ListMultimap<UUID, String> listMultimap;
    Multiset<UUID> multiset;
    Range<Integer> range;
    SetMultimap<UUID, String> setMultimap;
    SortedMultiset<UUID> sortedMultiset;
    SortedSetMultimap<UUID, String> sortedSetMultimap;
    Table<String, Integer, Long> table;
    TreeMultimap<UUID, String> treeMultimap;
    TreeMultiset<UUID> treeMultiset;
    ConcurrentHashMultiset<UUID> concurrentHashMultiset;

    // Net
    HostAndPort hostAndPort;
    InternetDomainName internetDomainName;
}
