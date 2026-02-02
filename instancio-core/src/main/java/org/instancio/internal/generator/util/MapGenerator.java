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
package org.instancio.internal.generator.util;

import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.Hints;
import org.instancio.generator.hints.MapHint;
import org.instancio.generator.specs.MapGeneratorSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.ErrorHandler;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.InternalGeneratorHint;
import org.instancio.internal.util.ExceptionUtils;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.NumberUtils;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.Sonar;
import org.instancio.settings.Keys;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapGenerator<K, V> extends AbstractGenerator<Map<K, V>> implements MapGeneratorSpec<K, V> {
    private static final Class<?> DEFAULT_MAP_TYPE = HashMap.class; // NOPMD

    protected int minSize;
    protected int maxSize;
    private boolean nullableKeys;
    private boolean nullableValues;
    protected Class<?> mapType;
    private Map<K, V> withEntries;
    private List<K> withKeys;

    public MapGenerator(final GeneratorContext context) {
        super(context);
        this.minSize = context.getSettings().get(Keys.MAP_MIN_SIZE);
        this.maxSize = context.getSettings().get(Keys.MAP_MAX_SIZE);
        super.nullable(context.getSettings().get(Keys.MAP_NULLABLE));
        this.nullableKeys = context.getSettings().get(Keys.MAP_KEYS_NULLABLE);
        this.nullableValues = context.getSettings().get(Keys.MAP_VALUES_NULLABLE);
        this.mapType = DEFAULT_MAP_TYPE;
    }

    @Override
    public String apiMethod() {
        return "map()";
    }

    @Override
    public MapGenerator<K, V> subtype(final Class<?> type) {
        this.mapType = ApiValidator.notNull(type, "type must not be null");
        return this;
    }

    @Override
    public MapGenerator<K, V> size(final int size) {
        this.minSize = ApiValidator.validateSize(size);
        this.maxSize = size;
        return this;
    }

    @Override
    public MapGenerator<K, V> minSize(final int size) {
        this.minSize = ApiValidator.validateSize(size);
        this.maxSize = NumberUtils.calculateNewMaxSize(maxSize, minSize);
        return this;
    }

    @Override
    public MapGenerator<K, V> maxSize(final int size) {
        this.maxSize = ApiValidator.validateSize(size);
        this.minSize = NumberUtils.calculateNewMinSize(minSize, maxSize);
        return this;
    }

    @Override
    public MapGenerator<K, V> nullable() {
        super.nullable();
        return this;
    }

    @Override
    public MapGenerator<K, V> nullable(final boolean isNullable) {
        super.nullable(isNullable);
        return this;
    }

    @Override
    public MapGenerator<K, V> nullableKeys() {
        this.nullableKeys = true;
        return this;
    }

    @Override
    public MapGenerator<K, V> nullableValues() {
        this.nullableValues = true;
        return this;
    }

    @Override
    public MapGenerator<K, V> with(final K key, final V value) {
        if (withEntries == null) {
            withEntries = new HashMap<>();
        }
        withEntries.put(key, value);
        return this;
    }

    @SafeVarargs
    @Override
    public final MapGenerator<K, V> withKeys(final K... keys) {
        ApiValidator.notEmpty(keys, "'map().withKeys(...)' must contain at least one key");
        if (withKeys == null) {
            withKeys = new ArrayList<>();
        }
        withKeys.addAll(Arrays.asList(keys));
        return this;
    }

    @Override
    @SuppressWarnings({"unchecked", Sonar.RETURN_EMPTY_COLLECTION})
    public Map<K, V> tryGenerateNonNull(final Random random) {
        try {
            Constructor<?> ctor = ReflectionUtils.setAccessible(mapType.getDeclaredConstructor());
            return (Map<K, V>) ctor.newInstance();
        } catch (Exception ex) {
            final String msg = String.format("Error creating instance of: %s", mapType);

            if (ErrorHandler.shouldFailOnError(getContext().getSettings())) {
                throw Fail.withFataInternalError(msg, ex);
            }
            ExceptionUtils.logException(msg, ex);
            return null; //NOPMD
        }
    }

    @Override
    public Hints hints() {
        return Hints.builder()
                .afterGenerate(AfterGenerate.POPULATE_ALL)
                .with(MapHint.builder()
                        .generateEntries(getContext().random().intRange(minSize, maxSize))
                        .nullableMapKeys(nullableKeys)
                        .nullableMapValues(nullableValues)
                        .withEntries(withEntries)
                        .withKeys(withKeys)
                        .build())
                .with(InternalGeneratorHint.builder()
                        .targetClass(mapType)
                        .nullableResult(isNullable())
                        .build())
                .build();
    }
}
