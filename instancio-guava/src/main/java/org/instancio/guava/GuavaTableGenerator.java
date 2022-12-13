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
import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.Hints;
import org.instancio.internal.generator.InternalContainerHint;
import org.instancio.internal.util.Constants;

public class GuavaTableGenerator<R, C, V> implements Generator<Table<R, C, V>> {

    private int generateEntries;

    @Override
    public void init(final GeneratorContext context) {
        generateEntries = context.random().intRange(Constants.MIN_SIZE, Constants.MAX_SIZE);
    }

    @Override
    public Table<R, C, V> generate(final Random random) {
        return HashBasedTable.create();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Hints hints() {
        return Hints.builder()
                .with(InternalContainerHint.builder()
                        .generateEntries(generateEntries)
                        .addFunction((Table<R, C, V> table, Object... args) ->
                                table.put(
                                        (R) args[0],
                                        (C) args[1],
                                        (V) args[2]))
                        .build())
                .build();
    }
}
