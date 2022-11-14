/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.generator.util;

import org.instancio.Random;
import org.instancio.generator.AbstractGenerator;
import org.instancio.generator.GeneratedHints;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.MapGeneratorSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.settings.Keys;
import org.instancio.util.Constants;
import org.instancio.util.NumberUtils;
import org.instancio.util.Sonar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MapGenerator<K, V> extends AbstractGenerator<Map<K, V>> implements MapGeneratorSpec<K, V> {
    private static final Logger LOG = LoggerFactory.getLogger(MapGenerator.class);

    protected int minSize;
    protected int maxSize;
    protected boolean nullable;
    protected boolean nullableKeys;
    protected boolean nullableValues;
    @SuppressWarnings("PMD.LooseCoupling")
    protected Class<?> type = HashMap.class; // default map type

    public MapGenerator(final GeneratorContext context) {
        super(context);
        this.minSize = context.getSettings().get(Keys.MAP_MIN_SIZE);
        this.maxSize = context.getSettings().get(Keys.MAP_MAX_SIZE);
        this.nullable = context.getSettings().get(Keys.MAP_NULLABLE);
        this.nullableKeys = context.getSettings().get(Keys.MAP_KEYS_NULLABLE);
        this.nullableValues = context.getSettings().get(Keys.MAP_VALUES_NULLABLE);
    }

    @Override
    public MapGeneratorSpec<K, V> subtype(final Class<?> type) {
        this.type = ApiValidator.notNull(type, "Type must not be null");
        return this;
    }

    @Override
    public MapGeneratorSpec<K, V> size(final int size) {
        this.minSize = ApiValidator.validateSize(size);
        this.maxSize = size;
        return this;
    }

    @Override
    public MapGeneratorSpec<K, V> minSize(final int size) {
        this.minSize = ApiValidator.validateSize(size);
        this.maxSize = NumberUtils.calculateNewMax(maxSize, minSize, Constants.RANGE_ADJUSTMENT_PERCENTAGE);
        return this;
    }

    @Override
    public MapGeneratorSpec<K, V> maxSize(final int size) {
        this.maxSize = ApiValidator.validateSize(size);
        this.minSize = NumberUtils.calculateNewMin(minSize, maxSize, Constants.RANGE_ADJUSTMENT_PERCENTAGE);
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
    @SuppressWarnings({"unchecked", Sonar.RETURN_EMPTY_COLLECTION})
    public Map<K, V> generate(final Random random) {
        try {
            return random.diceRoll(nullable) ? null : (Map<K, V>) type.getDeclaredConstructor().newInstance();
        } catch (Exception ex) {
            LOG.debug("Error creating instance of: {}", type, ex);
            return null; // NOPMD
        }
    }

    @Override
    public GeneratedHints getHints() {
        return GeneratedHints.builder()
                .dataStructureSize(getContext().random().intRange(minSize, maxSize))
                .ignoreChildren(false)
                .nullableResult(nullable)
                .nullableKeys(nullableKeys)
                .nullableValues(nullableValues)
                .build();
    }
}
