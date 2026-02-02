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
package org.instancio.test.guava.collect;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.guava.GenGuava;
import org.instancio.internal.util.Constants;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.types;

@ExtendWith(InstancioExtension.class)
class GuavaMultimapTest {

    @Test
    void createViaTypeToken() {
        final Multimap<String, Long> result = Instancio.create(
                new TypeToken<ImmutableListMultimap<String, Long>>() {});

        assertThat(result).isExactlyInstanceOf(ImmutableListMultimap.class);
        assertThat(result.size()).isBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);
        assertThat(result.keySet()).doesNotContainNull();
        assertThat(result.values()).doesNotContainNull();
    }

    @Test
    void createViaTypeTokenWithNestedTable() {
        final Multimap<String, Table<Integer, String, Long>> result =
                Instancio.create(new TypeToken<ListMultimap<String, Table<Integer, String, Long>>>() {});

        assertThat(result).isExactlyInstanceOf(ArrayListMultimap.class);
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.values()).doesNotContainNull()
                .allSatisfy(table -> {
                    assertThat(table).isInstanceOf(Table.class);
                    assertThat(table.size()).isBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);
                    assertThat(table.values()).doesNotContainNull();
                });
    }

    @Test
    void generatorSpecMinMaxSize() {
        final Multimap<String, Long> result = Instancio.of(new TypeToken<ImmutableListMultimap<String, Long>>() {})
                .generate(types().of(Multimap.class), GenGuava.multimap().minSize(7).maxSize(8))
                .create();

        assertThat(result.size()).isBetween(7, 8);
    }
}
