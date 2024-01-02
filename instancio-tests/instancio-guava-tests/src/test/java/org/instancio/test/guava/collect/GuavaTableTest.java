/*
 * Copyright 2022-2024 the original author or authors.
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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.instancio.Assign;
import org.instancio.Gen;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.internal.util.Constants;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allInts;
import static org.instancio.Select.allLongs;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.Select.root;
import static org.instancio.Assign.given;
import static org.instancio.guava.GenGuava.table;

@ExtendWith(InstancioExtension.class)
class GuavaTableTest {

    private static final int SAMPLE_SIZE = 500;

    @Test
    void createViaTypeToken() {
        final long expectedValue = -1L;
        final Table<String, Integer, Long> result = Instancio.of(new TypeToken<Table<String, Integer, Long>>() {})
                .set(allLongs(), expectedValue)
                .create();

        assertThat(result).isExactlyInstanceOf(HashBasedTable.class);
        assertThat(result.size()).isBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);

        assertThat(result.rowKeySet()).hasOnlyElementsOfType(String.class);
        assertThat(result.columnKeySet()).hasOnlyElementsOfType(Integer.class);
        assertThat(result.values()).containsOnly(expectedValue);
    }

    @Test
    void listOfTables() {
        final List<Table<String, Integer, Long>> result = Instancio.of(new TypeToken<List<Table<String, Integer, Long>>>() {})
                .generate(root(), gen -> gen.collection().size(SAMPLE_SIZE))
                .create();

        final Set<Integer> tableSizes = result.stream().map(Table::size).collect(Collectors.toSet());
        assertThat(tableSizes).hasSize(Constants.MAX_SIZE - Constants.MIN_SIZE + 1);
    }

    @Test
    void generatorSpecSize() {
        final TableHolder result = Instancio.of(TableHolder.class)
                .generate(field("table1"), table().minSize(1).maxSize(2))
                .generate(field("table2"), table().size(3))
                .create();

        assertThat(result.table1.size()).isBetween(1, 2);
        assertThat(result.table2.size()).isEqualTo(3);
    }

    @Test
    void emit() {
        final String[] items = {"foo", "bar", "baz"};

        final TableHolder result = Instancio.of(TableHolder.class)
                .generate(allStrings().within(field("table1").toScope()),
                        gen -> gen.emit().items(items))
                .generate(field("table1"), table().size(3))
                .create();

        assertThat(result.table1.size()).isEqualTo(3);
        assertThat(result.table1.rowKeySet()).containsExactlyInAnyOrder(items);
        assertThat(result.table2.rowKeySet()).doesNotContain(items);
    }

    @RepeatedTest(10)
    void assignmentWithinTable() {
        final List<Table<String, Integer, Long>> result = Instancio.of(new TypeToken<List<Table<String, Integer, Long>>>() {})
                .generate(allStrings(), gen -> gen.oneOf("S1", "S2", "S3"))
                .assign(Assign.given(allStrings()).is("S1")
                        .set(allInts(), -1)
                        .set(allLongs(), -10L))
                .assign(Assign.given(allStrings()).is("S2")
                        .set(allInts(), -2)
                        .set(allLongs(), -20L))
                .create();

        assertThat(result).isNotEmpty();

        result.forEach(table -> {
            assertThat(table.isEmpty()).isFalse();

            final Map<String, Map<Integer, Long>> map = table.rowMap();

            assertThat(map).isNotEmpty().allSatisfy((String key, Map<Integer, Long> rowMap) -> {

                assertThat(rowMap).allSatisfy((Integer k, Long v) -> {

                    if ("S1".equals(key)) {
                        assertThat(k).isEqualTo(-1);
                        assertThat(v).isEqualTo(-10);
                    } else if ("S2".equals(key)) {
                        assertThat(k).isEqualTo(-2);
                        assertThat(v).isEqualTo(-20);
                    } else {
                        assertThat(k).isNotIn(-1, -2);
                        assertThat(v).isNotIn(-10, -20);
                    }
                });
            });
        });
    }

    @RepeatedTest(10)
    void assignmentAcrossTables() {
        final String expected = Gen.oneOf("foo1", "bar1").get();

        final TableHolder result = Instancio.of(TableHolder.class)
                .set(allStrings().within(field("table1").toScope()), expected)
                .assign(Assign.given(field("table1"))
                        .satisfies((Table<String, Character, Integer> t) -> t.containsRow("foo1"))
                        .set(allStrings().within(field("table2").toScope()), "foo2"))
                .assign(Assign.given(field("table1"))
                        .satisfies((Table<String, Character, Integer> t) -> t.containsRow("bar1"))
                        .set(allStrings().within(field("table2").toScope()), "bar2"))
                .create();


        if (result.table1.containsRow("foo1")) {
            assertThat(result.table2.containsRow("foo2")).isTrue();
        } else if (result.table1.containsRow("bar1")) {
            assertThat(result.table2.containsRow("bar2")).isTrue();
        }
    }

    private static class TableHolder {
        private Table<String, Character, Integer> table1;
        private Table<String, Character, Integer> table2;
    }
}
