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
package org.instancio.generators.collections;

import org.instancio.exception.InstancioException;
import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.GeneratedHints;
import org.instancio.internal.model.ModelContext;
import org.instancio.settings.Setting;
import org.instancio.util.Verify;

import java.util.HashMap;
import java.util.Map;

public class MapGenerator<K, V> extends AbstractRandomGenerator<Map<K, V>> implements MapGeneratorSpec<K, V> {

    private int minSize;
    private int maxSize;
    private boolean nullable;
    private boolean nullableKeys;
    private boolean nullableValues;
    private Class<?> type = HashMap.class;

    public MapGenerator(final ModelContext<?> context) {
        super(context);
        this.minSize = context.getSettings().get(Setting.MAP_MIN_SIZE);
        this.maxSize = context.getSettings().get(Setting.MAP_MAX_SIZE);
        this.nullable = context.getSettings().get(Setting.MAP_NULLABLE);
        this.nullableKeys = context.getSettings().get(Setting.MAP_KEYS_NULLABLE);
        this.nullableValues = context.getSettings().get(Setting.MAP_VALUES_NULLABLE);
    }

    public MapGeneratorSpec<K, V> type(final Class<?> type) {
        this.type = Verify.notNull(type, "Type must not be null");
        return this;
    }

    @Override
    public MapGeneratorSpec<K, V> minSize(final int size) {
        Verify.isTrue(size >= 0, "Size cannot be negative: " + size);
        this.minSize = size;
        this.maxSize = Math.max(maxSize, minSize);
        return this;
    }

    @Override
    public MapGeneratorSpec<K, V> maxSize(final int size) {
        Verify.isTrue(size >= 0, "Size cannot be negative: " + size);
        this.maxSize = size;
        this.minSize = Math.min(minSize, maxSize);
        return this;
    }

    @Override
    public MapGeneratorSpec<K, V> nullable() {
        this.nullable = true;
        return this;
    }

    @Override
    public MapGeneratorSpec<K, V> nullableKeys() {
        this.nullableKeys = true;
        return this;
    }

    @Override
    public MapGeneratorSpec<K, V> nullableValues() {
        this.nullableValues = true;
        return this;
    }

    @Override
    public Map<K, V> generate() {
        try {
            return nullable && random().oneInTenTrue() ? null : (Map<K, V>) type.newInstance();
        } catch (Exception ex) {
            throw new InstancioException(String.format("Error creating instance of: %s", type), ex);
        }
    }

    @Override
    public GeneratedHints getHints() {
        return GeneratedHints.builder()
                .dataStructureSize(random().intBetween(minSize, maxSize + 1))
                .ignoreChildren(false)
                .nullableResult(nullable)
                .nullableKeys(nullableKeys)
                .nullableValues(nullableValues)
                .build();
    }
}
