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
package org.instancio.settings;

import org.instancio.internal.ApiValidator;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Keys {
    private static final int MIN_SIZE = 2;
    private static final int MAX_SIZE = 6;
    private static final int NUMERIC_MAX = 10000;
    private static final int RANGE_BOUNDS_PERCENTAGE = 20;

    private static final RangeAdjuster minAdjuster = new RangeAdjuster.ForMin(RANGE_BOUNDS_PERCENTAGE);
    private static final RangeAdjuster maxAdjuster = new RangeAdjuster.ForMax(RANGE_BOUNDS_PERCENTAGE);
    private static final List<SettingKey> ALL_KEYS = new ArrayList<>();

    public static final SettingKey ARRAY_ELEMENTS_NULLABLE = register("array.elements.nullable", Boolean.class, false);
    public static final SettingKey ARRAY_MAX_LENGTH = register("array.max.length", Integer.class, MAX_SIZE, maxAdjuster);
    public static final SettingKey ARRAY_MIN_LENGTH = register("array.min.length", Integer.class, MIN_SIZE, minAdjuster);
    public static final SettingKey ARRAY_NULLABLE = register("array.nullable", Boolean.class, false);
    public static final SettingKey BOOLEAN_NULLABLE = register("boolean.nullable", Boolean.class, false);
    public static final SettingKey BYTE_MAX = register("byte.max", Byte.class, (byte) 127, maxAdjuster);
    public static final SettingKey BYTE_MIN = register("byte.min", Byte.class, (byte) 1, minAdjuster);
    public static final SettingKey BYTE_NULLABLE = register("byte.nullable", Boolean.class, false);
    public static final SettingKey CHARACTER_NULLABLE = register("character.nullable", Boolean.class, false);
    public static final SettingKey COLLECTION_ELEMENTS_NULLABLE = register("collection.elements.nullable", Boolean.class, false);
    public static final SettingKey COLLECTION_MAX_SIZE = register("collection.max.size", Integer.class, MAX_SIZE, maxAdjuster);
    public static final SettingKey COLLECTION_MIN_SIZE = register("collection.min.size", Integer.class, MIN_SIZE, minAdjuster);
    public static final SettingKey COLLECTION_NULLABLE = register("collection.nullable", Boolean.class, false);
    public static final SettingKey DOUBLE_MAX = register("double.max", Double.class, (double) NUMERIC_MAX, maxAdjuster);
    public static final SettingKey DOUBLE_MIN = register("double.min", Double.class, 1d, minAdjuster);
    public static final SettingKey DOUBLE_NULLABLE = register("double.nullable", Boolean.class, false);
    public static final SettingKey FLOAT_MAX = register("float.max", Float.class, (float) NUMERIC_MAX, maxAdjuster);
    public static final SettingKey FLOAT_MIN = register("float.min", Float.class, 1f, minAdjuster);
    public static final SettingKey FLOAT_NULLABLE = register("float.nullable", Boolean.class, false);
    public static final SettingKey INTEGER_MAX = register("integer.max", Integer.class, NUMERIC_MAX, maxAdjuster);
    public static final SettingKey INTEGER_MIN = register("integer.min", Integer.class, 1, minAdjuster);
    public static final SettingKey INTEGER_NULLABLE = register("integer.nullable", Boolean.class, false);
    public static final SettingKey LONG_MAX = register("long.max", Long.class, (long) NUMERIC_MAX, maxAdjuster);
    public static final SettingKey LONG_MIN = register("long.min", Long.class, 1L, minAdjuster);
    public static final SettingKey LONG_NULLABLE = register("long.nullable", Boolean.class, false);
    public static final SettingKey MAP_KEYS_NULLABLE = register("map.keys.nullable", Boolean.class, false);
    public static final SettingKey MAP_MAX_SIZE = register("map.max.size", Integer.class, MAX_SIZE, maxAdjuster);
    public static final SettingKey MAP_MIN_SIZE = register("map.min.size", Integer.class, MIN_SIZE, minAdjuster);
    public static final SettingKey MAP_NULLABLE = register("map.nullable", Boolean.class, false);
    public static final SettingKey MAP_VALUES_NULLABLE = register("map.values.nullable", Boolean.class, false);
    public static final SettingKey SHORT_MAX = register("short.max", Short.class, (short) NUMERIC_MAX, maxAdjuster);
    public static final SettingKey SHORT_MIN = register("short.min", Short.class, (short) 1, minAdjuster);
    public static final SettingKey SHORT_NULLABLE = register("short.nullable", Boolean.class, false);
    public static final SettingKey STRING_ALLOW_EMPTY = register("string.allow.empty", Boolean.class, false);
    public static final SettingKey STRING_MAX_LENGTH = register("string.max.length", Integer.class, 10, maxAdjuster);
    public static final SettingKey STRING_MIN_LENGTH = register("string.min.length", Integer.class, 3, minAdjuster);
    public static final SettingKey STRING_NULLABLE = register("string.nullable", Boolean.class, false);

    // Note: keys must be collected after all keys have been initialised
    private static final Map<String, SettingKey> SETTING_KEY_MAP = Collections.unmodifiableMap(settingKeyMap());
    private static final Map<SettingKey, SettingKey> AUTO_ADJUSTABLE_MAP = Collections.unmodifiableMap(getAutoAdjustableKeys());

    public static List<SettingKey> all() {
        return ALL_KEYS;
    }

    public static SettingKey get(final String key) {
        final SettingKey settingKey = SETTING_KEY_MAP.get(key);
        ApiValidator.isTrue(settingKey != null, "Invalid instancio property key: '%s'", key);
        return settingKey;
    }

    static Optional<SettingKey> getAutoAdjustable(final SettingKey key) {
        return Optional.ofNullable(AUTO_ADJUSTABLE_MAP.get(key));
    }

    private static SettingKey register(final String propertyKey,
                                       final Class<?> type,
                                       final Object defaultValue,
                                       @Nullable final RangeAdjuster rangeAdjuster) {

        final SettingKey settingKey = new Key(propertyKey, type, defaultValue, rangeAdjuster);
        ALL_KEYS.add(settingKey);
        return settingKey;
    }

    private static SettingKey register(final String key, final Class<?> type, final Object defaultValue) {
        return register(key, type, defaultValue, null);
    }

    private static Map<String, SettingKey> settingKeyMap() {
        final Map<String, SettingKey> map = new HashMap<>();
        for (SettingKey key : ALL_KEYS) {
            map.put(key.propertyKey(), key);
        }
        return map;
    }

    private static Map<SettingKey, SettingKey> getAutoAdjustableKeys() {
        final Map<SettingKey, SettingKey> map = new HashMap<>();
        map.put(Keys.ARRAY_MAX_LENGTH, Keys.ARRAY_MIN_LENGTH);
        map.put(Keys.ARRAY_MIN_LENGTH, Keys.ARRAY_MAX_LENGTH);
        map.put(Keys.BYTE_MAX, Keys.BYTE_MIN);
        map.put(Keys.BYTE_MIN, Keys.BYTE_MAX);
        map.put(Keys.COLLECTION_MAX_SIZE, Keys.COLLECTION_MIN_SIZE);
        map.put(Keys.COLLECTION_MIN_SIZE, Keys.COLLECTION_MAX_SIZE);
        map.put(Keys.DOUBLE_MAX, Keys.DOUBLE_MIN);
        map.put(Keys.DOUBLE_MIN, Keys.DOUBLE_MAX);
        map.put(Keys.FLOAT_MAX, Keys.FLOAT_MIN);
        map.put(Keys.FLOAT_MIN, Keys.FLOAT_MAX);
        map.put(Keys.INTEGER_MAX, Keys.INTEGER_MIN);
        map.put(Keys.INTEGER_MIN, Keys.INTEGER_MAX);
        map.put(Keys.LONG_MAX, Keys.LONG_MIN);
        map.put(Keys.LONG_MIN, Keys.LONG_MAX);
        map.put(Keys.MAP_MAX_SIZE, Keys.MAP_MIN_SIZE);
        map.put(Keys.MAP_MIN_SIZE, Keys.MAP_MAX_SIZE);
        map.put(Keys.SHORT_MAX, Keys.SHORT_MIN);
        map.put(Keys.SHORT_MIN, Keys.SHORT_MAX);
        map.put(Keys.STRING_MAX_LENGTH, Keys.STRING_MIN_LENGTH);
        map.put(Keys.STRING_MIN_LENGTH, Keys.STRING_MAX_LENGTH);
        return map;
    }

    private Keys() {
        // non-instantiable
    }
}
