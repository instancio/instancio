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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.internal.util.Constants;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allLongs;

class TableTest {

    @Test
    void table() {
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
}
