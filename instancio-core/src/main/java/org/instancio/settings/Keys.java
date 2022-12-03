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

import org.instancio.Mode;
import org.instancio.generator.PopulateAction;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.settings.InternalKey;
import org.instancio.internal.settings.RangeAdjuster;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines all keys supported by Instancio.
 *
 * @see SettingKey
 * @see Settings
 */
public final class Keys {
    private static final int MIN_SIZE = 2;
    private static final int MAX_SIZE = 6;
    private static final int NUMERIC_MAX = 10_000;

    private static final RangeAdjuster MIN_ADJUSTER = RangeAdjuster.MIN_ADJUSTER;
    private static final RangeAdjuster MAX_ADJUSTER = RangeAdjuster.MAX_ADJUSTER;
    private static final List<SettingKey> ALL_KEYS = new ArrayList<>();

    /**
     * Specifies whether a {@code null} can be generated for array elements;
     * default is {@code false}; property name {@code array.elements.nullable}.
     */
    public static final SettingKey ARRAY_ELEMENTS_NULLABLE = register("array.elements.nullable", Boolean.class, false);
    /**
     * Specifies maximum length for arrays;
     * default is 6; property name {@code array.max.length}.
     */
    public static final SettingKey ARRAY_MAX_LENGTH = register("array.max.length", Integer.class, MAX_SIZE, MAX_ADJUSTER);
    /**
     * Specifies minimum length for arrays;
     * default is 2; property name {@code array.min.length}.
     */
    public static final SettingKey ARRAY_MIN_LENGTH = register("array.min.length", Integer.class, MIN_SIZE, MIN_ADJUSTER);
    /**
     * Specifies whether a null can be generated for arrays;
     * default is {@code false}; property name {@code array.nullable}.
     */
    public static final SettingKey ARRAY_NULLABLE = register("array.nullable", Boolean.class, false);
    /**
     * Specifies whether a {@code null} can be generated for Boolean type;
     * default is {@code false}; property name {@code boolean.nullable}.
     */
    public static final SettingKey BOOLEAN_NULLABLE = register("boolean.nullable", Boolean.class, false);
    /**
     * Specifies maximum value for bytes;
     * default is 127; property name {@code byte.max}.
     */
    public static final SettingKey BYTE_MAX = register("byte.max", Byte.class, (byte) 127, MAX_ADJUSTER);
    /**
     * Specifies minimum value for bytes;
     * default is 1; property name {@code byte.min}.
     */
    public static final SettingKey BYTE_MIN = register("byte.min", Byte.class, (byte) 1, MIN_ADJUSTER);
    /**
     * Specifies whether a {@code null} can be generated for Byte type;
     * default is {@code false}; property name {@code byte.nullable}.
     */
    public static final SettingKey BYTE_NULLABLE = register("byte.nullable", Boolean.class, false);
    /**
     * Specifies whether a {@code null} can be generated for Character type;
     * default is {@code false}; property name {@code character.nullable}.
     */
    public static final SettingKey CHARACTER_NULLABLE = register("character.nullable", Boolean.class, false);
    /**
     * Specifies whether a {@code null} can be generated for collection elements;
     * default is {@code false}; property name {@code collection.elements.nullable}.
     */
    public static final SettingKey COLLECTION_ELEMENTS_NULLABLE = register("collection.elements.nullable", Boolean.class, false);
    /**
     * Specifies maximum size for collections;
     * default is 6; property name {@code collection.max.size}.
     */
    public static final SettingKey COLLECTION_MAX_SIZE = register("collection.max.size", Integer.class, MAX_SIZE, MAX_ADJUSTER);
    /**
     * Specifies minimum size for collections;
     * default is 2; property name {@code collection.min.size}.
     */
    public static final SettingKey COLLECTION_MIN_SIZE = register("collection.min.size", Integer.class, MIN_SIZE, MIN_ADJUSTER);
    /**
     * Specifies whether a {@code null} can be generated for collections;
     * default is {@code false}; property name {@code collection.nullable}.
     */
    public static final SettingKey COLLECTION_NULLABLE = register("collection.nullable", Boolean.class, false);
    /**
     * Specifies maximum value for doubles;
     * default is 10000; property name {@code double.max}.
     */
    public static final SettingKey DOUBLE_MAX = register("double.max", Double.class, (double) NUMERIC_MAX, MAX_ADJUSTER);
    /**
     * Specifies minimum value for doubles;
     * default is 1; property name {@code double.min}.
     */
    public static final SettingKey DOUBLE_MIN = register("double.min", Double.class, 1d, MIN_ADJUSTER);
    /**
     * Specifies whether a {@code null} can be generated for Double type;
     * default is {@code false}; property name {@code double.nullable}.
     */
    public static final SettingKey DOUBLE_NULLABLE = register("double.nullable", Boolean.class, false);
    /**
     * Specifies maximum value for floats;
     * default is 10000; property name {@code float.max}.
     */
    public static final SettingKey FLOAT_MAX = register("float.max", Float.class, (float) NUMERIC_MAX, MAX_ADJUSTER);
    /**
     * Specifies minimum value for floats;
     * default is 1; property name {@code float.min}.
     */
    public static final SettingKey FLOAT_MIN = register("float.min", Float.class, 1f, MIN_ADJUSTER);
    /**
     * Specifies whether a {@code null} can be generated for Float type;
     * default is {@code false}; property name {@code float.nullable}.
     */
    public static final SettingKey FLOAT_NULLABLE = register("float.nullable", Boolean.class, false);
    /**
     * Specifies maximum value for integers;
     * default is 10000; property name {@code integer.max}.
     */
    public static final SettingKey INTEGER_MAX = register("integer.max", Integer.class, NUMERIC_MAX, MAX_ADJUSTER);
    /**
     * Specifies minimum value for integers;
     * default is 1; property name {@code integer.min}.
     */
    public static final SettingKey INTEGER_MIN = register("integer.min", Integer.class, 1, MIN_ADJUSTER);
    /**
     * Specifies whether a {@code null} can be generated for Integer type;
     * default is {@code false}; property name {@code integer.nullable}.
     */
    public static final SettingKey INTEGER_NULLABLE = register("integer.nullable", Boolean.class, false);
    /**
     * Specifies maximum value for longs;
     * default is 10000; property name {@code long.max}.
     */
    public static final SettingKey LONG_MAX = register("long.max", Long.class, (long) NUMERIC_MAX, MAX_ADJUSTER);
    /**
     * Specifies minimum value for longs;
     * default is 1; property name {@code long.min}.
     */
    public static final SettingKey LONG_MIN = register("long.min", Long.class, 1L, MIN_ADJUSTER);
    /**
     * Specifies whether a {@code null} can be generated for Long type;
     * default is {@code false}; property name {@code long.nullable}.
     */
    public static final SettingKey LONG_NULLABLE = register("long.nullable", Boolean.class, false);
    /**
     * Specifies whether a {@code null} can be generated for map keys;
     * default is {@code false}; property name {@code map.keys.nullable}.
     */
    public static final SettingKey MAP_KEYS_NULLABLE = register("map.keys.nullable", Boolean.class, false);
    /**
     * Specifies maximum size for maps;
     * default is 6; property name {@code map.max.size}.
     */
    public static final SettingKey MAP_MAX_SIZE = register("map.max.size", Integer.class, MAX_SIZE, MAX_ADJUSTER);
    /**
     * Specifies minimum size for maps;
     * default is 2; property name {@code map.min.size}.
     */
    public static final SettingKey MAP_MIN_SIZE = register("map.min.size", Integer.class, MIN_SIZE, MIN_ADJUSTER);
    /**
     * Specifies whether a {@code null} can be generated for maps;
     * default is {@code false}; property name {@code map.nullable}.
     */
    public static final SettingKey MAP_NULLABLE = register("map.nullable", Boolean.class, false);
    /**
     * Specifies whether a {@code null} can be generated for map values;
     * default is {@code false}; property name {@code map.values.nullable}.
     */
    public static final SettingKey MAP_VALUES_NULLABLE = register("map.values.nullable", Boolean.class, false);
    /**
     * Specifies the mode: strict (unused selectors will trigger an exception) or lenient;
     * default is strict; property name {@code mode}.
     *
     * @since 1.3.3
     */
    public static final SettingKey MODE = register("mode", Mode.class, Mode.STRICT);
    /**
     * Specifies the {@link PopulateAction}.
     *
     * @since 1.7.0
     */
    public static final SettingKey POPULATE_ACTION = register("populate.action", PopulateAction.class, PopulateAction.APPLY_SELECTORS);
    /**
     * Specifies the seed value;
     * default is {@code null}; property name {@code seed}.
     *
     * @since 1.5.1
     */
    public static final SettingKey SEED = registerWithNullDefault("seed", Long.class);
    /**
     * Specifies maximum value for shorts;
     * default is 10000; property name {@code short.max}.
     */
    public static final SettingKey SHORT_MAX = register("short.max", Short.class, (short) NUMERIC_MAX, MAX_ADJUSTER);
    /**
     * Specifies minimum value for shorts;
     * default is 1; property name {@code short.min}.
     */
    public static final SettingKey SHORT_MIN = register("short.min", Short.class, (short) 1, MIN_ADJUSTER);
    /**
     * Specifies whether a {@code null} can be generated for Short type;
     * default is {@code false}; property name {@code short.nullable}.
     */
    public static final SettingKey SHORT_NULLABLE = register("short.nullable", Boolean.class, false);
    /**
     * Specifies whether an empty string can be generated;
     * default is {@code false}; property name {@code string.allow.empty}.
     */
    public static final SettingKey STRING_ALLOW_EMPTY = register("string.allow.empty", Boolean.class, false);
    /**
     * Specifies maximum length of strings;
     * default is 10; property name {@code string.max.length}.
     */
    public static final SettingKey STRING_MAX_LENGTH = register("string.max.length", Integer.class, 10, MAX_ADJUSTER);
    /**
     * Specifies minimum length of strings;
     * default is 3; property name {@code string.min.length}.
     */
    public static final SettingKey STRING_MIN_LENGTH = register("string.min.length", Integer.class, 3, MIN_ADJUSTER);
    /**
     * Specifies whether a {@code null} can be generated for String type;
     * default is {@code false}; property name {@code string.nullable}.
     */
    public static final SettingKey STRING_NULLABLE = register("string.nullable", Boolean.class, false);

    // Note: keys must be collected after all keys have been initialised
    private static final Map<String, SettingKey> SETTING_KEY_MAP = Collections.unmodifiableMap(settingKeyMap());

    /**
     * Returns all keys supported by Instancio.
     *
     * @return all keys
     */
    public static List<SettingKey> all() {
        return Collections.unmodifiableList(ALL_KEYS);
    }

    /**
     * Returns a {@link SettingKey} instance with the given property key.
     *
     * @param key to lookup
     * @return the setting key; an exception is thrown if the key is not found
     */
    public static SettingKey get(final String key) {
        final SettingKey settingKey = SETTING_KEY_MAP.get(key);
        ApiValidator.isTrue(settingKey != null, "Invalid instancio property key: '%s'", key);
        return settingKey;
    }


    private static SettingKey register(final String propertyKey,
                                       final Class<?> type,
                                       @Nullable final Object defaultValue,
                                       @Nullable final RangeAdjuster rangeAdjuster,
                                       final boolean allowsNullValue) {

        final SettingKey settingKey = new InternalKey(propertyKey, type, defaultValue, rangeAdjuster, allowsNullValue);
        ALL_KEYS.add(settingKey);
        return settingKey;
    }

    private static SettingKey register(final String propertyKey,
                                       final Class<?> type,
                                       @Nullable final Object defaultValue,
                                       @Nullable final RangeAdjuster rangeAdjuster) {

        return register(propertyKey, type, defaultValue, rangeAdjuster, false);
    }

    private static SettingKey register(final String key, final Class<?> type, final Object defaultValue) {
        return register(key, type, defaultValue, null, false);
    }

    private static SettingKey registerWithNullDefault(final String key, final Class<?> type) {
        return register(key, type, null, null, true);
    }

    private static Map<String, SettingKey> settingKeyMap() {
        final Map<String, SettingKey> map = new HashMap<>();
        for (SettingKey key : ALL_KEYS) {
            map.put(key.propertyKey(), key);
        }
        return map;
    }


    private Keys() {
        // non-instantiable
    }
}
