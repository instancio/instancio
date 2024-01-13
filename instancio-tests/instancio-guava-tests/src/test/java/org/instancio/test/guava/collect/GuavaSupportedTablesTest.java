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
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.guava.GenGuava;
import org.instancio.internal.util.Constants;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@ExtendWith(InstancioExtension.class)
class GuavaSupportedTablesTest {

    private static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(new TypeToken<Table<UUID, String, Long>>() {}, HashBasedTable.class),
                Arguments.of(new TypeToken<ImmutableTable<UUID, String, Long>>() {}, ImmutableTable.class));
    }

    @ParameterizedTest
    @MethodSource("args")
    <T extends Table<UUID, String, Long>> void verifyCreate(final TypeToken<T> type, final Class<?> expectedSubtype) {
        final Table<UUID, String, Long> result = Instancio.create(type);

        assertThat(result)
                .as("Failed type: %s, expected subtype: %s", type.get(), expectedSubtype)
                .isInstanceOf(expectedSubtype);

        assertThat(result.size()).isBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);
    }

    @ParameterizedTest
    @MethodSource("args")
    <T extends Table<UUID, String, Long>> void verifyCreateWithSize(final TypeToken<T> type, final Class<?> expectedSubtype) {
        final int size = 5;
        final Table<UUID, String, Long> result = Instancio.of(type)
                .generate(root(), GenGuava.table().size(size))
                .create();

        assertThat(result)
                .as("Failed type: %s, expected subtype: %s", type.get(), expectedSubtype)
                .isInstanceOf(expectedSubtype);

        assertThat(result.size()).isEqualTo(size);
    }
}
