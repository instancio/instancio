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
package org.instancio.tests.jpms.guava;

import com.google.common.collect.*;
import com.google.common.net.HostAndPort;
import com.google.common.net.InternetDomainName;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class JpmsGuavaTest {

    private record AllSupportedGuavaTypes(ArrayListMultimap<UUID, String> arrayListMultimap,
                                          BiMap<UUID, String> biMap,
                                          HashBiMap<UUID, String> hashBiMap,
                                          HashMultimap<UUID, String> hashMultimap,
                                          HashMultiset<UUID> hashMultiset,
                                          ImmutableBiMap<UUID, String> immutableBiMap,
                                          ImmutableList<UUID> immutableList,
                                          ImmutableListMultimap<UUID, String> immutableListMultimap,
                                          ImmutableMap<UUID, String> immutableMap,
                                          ImmutableMultimap<UUID, String> immutableMultimap,
                                          ImmutableMultiset<UUID> immutableMultiset,
                                          ImmutableSet<UUID> immutableSet,
                                          ImmutableSetMultimap<UUID, String> immutableSetMultimap,
                                          ImmutableSortedMap<UUID, String> immutableSortedMap,
                                          ImmutableSortedMultiset<UUID> immutableSortedMultiset,
                                          ImmutableSortedSet<UUID> immutableSortedSet,
                                          ImmutableTable<String, Integer, Long> immutableTable,
                                          LinkedHashMultimap<UUID, String> linkedHashMultimap,
                                          LinkedHashMultiset<UUID> linkedHashMultiset,
                                          LinkedListMultimap<UUID, String> linkedListMultimap,
                                          ListMultimap<UUID, String> listMultimap,
                                          Multiset<UUID> multiset,
                                          Range<Integer> range,
                                          SetMultimap<UUID, String> setMultimap,
                                          SortedMultiset<UUID> sortedMultiset,
                                          SortedSetMultimap<UUID, String> sortedSetMultimap,
                                          Table<String, Integer, Long> table,
                                          TreeMultimap<UUID, String> treeMultimap,
                                          TreeMultiset<UUID> treeMultiset,
                                          ConcurrentHashMultiset<UUID> concurrentHashMultiset,
                                          HostAndPort hostAndPort,
                                          InternetDomainName internetDomainName) {
    }

    @Test
    void create() {
        final AllSupportedGuavaTypes result = Instancio.create(AllSupportedGuavaTypes.class);

        assertThat(result).hasNoNullFieldsOrProperties();

        assertThat(result.internetDomainName().toString())
                .matches("[a-z]+\\.[a-z]+");

        assertThat(result.immutableList())
                .isNotEmpty()
                .isInstanceOf(ImmutableList.class)
                .doesNotContainNull();
    }
}
