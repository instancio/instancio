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
package org.instancio.internal.schema.order;

import org.instancio.internal.util.ErrorMessageUtils;
import org.instancio.internal.util.Fail;
import org.instancio.settings.Keys;
import org.instancio.settings.SchemaDataEndStrategy;
import org.instancio.settings.Settings;

import java.util.concurrent.atomic.AtomicInteger;

public final class SequentialDataAccessStrategy implements DataAccessStrategy {

    private final AtomicInteger index = new AtomicInteger();
    private final Class<?> schemaClass;
    private final int dataSize;
    private final Settings settings;
    private final SchemaDataEndStrategy schemaDataEndStrategy;

    public SequentialDataAccessStrategy(
            final Class<?> schemaClass,
            final int dataSize,
            final Settings settings) {

        this.schemaClass = schemaClass;
        this.dataSize = dataSize;
        this.settings = settings;
        this.schemaDataEndStrategy = settings.get(Keys.SCHEMA_DATA_END_STRATEGY);
    }

    @Override
    public int nextIndex() {
        int next = index.getAndIncrement();

        if (next == dataSize) {
            if (schemaDataEndStrategy == SchemaDataEndStrategy.FAIL) {
                throw Fail.withUsageError(ErrorMessageUtils.schemaDataEnd(schemaClass, settings));
            } else {
                index.set(0);
                next = index.getAndIncrement();
            }
        }
        return next;
    }
}
