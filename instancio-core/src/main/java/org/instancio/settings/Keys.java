/*
 * Copyright 2022-2023 the original author or authors.
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

import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.AfterGenerate;
import org.instancio.internal.settings.InternalKey;
import org.instancio.internal.settings.RangeAdjuster;
import org.instancio.settings.SettingKey.SettingKeyBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.instancio.internal.util.Constants.MAX_SIZE;
import static org.instancio.internal.util.Constants.MIN_SIZE;
import static org.instancio.internal.util.Constants.NUMERIC_MAX;
import static org.instancio.internal.util.Constants.NUMERIC_MIN;

/**
 * Defines all keys supported by Instancio.
 *
 * @see SettingKey
 * @see Settings
 * @since 1.1.10
 */
public final class Keys {

    private static final RangeAdjuster MIN_ADJUSTER = RangeAdjuster.MIN_ADJUSTER;
    private static final RangeAdjuster MAX_ADJUSTER = RangeAdjuster.MAX_ADJUSTER;
    private static final List<SettingKey<Object>> ALL_KEYS = new ArrayList<>();

    /**
     * Specifies how to assign values via reflection, using fields or methods;
     * default is {@link AssignmentType#FIELD}; property name {@code assignment.type}.
     *
     * @see AssignmentType
     * @see #ON_SET_METHOD_ERROR
     * @see #ON_SET_METHOD_NOT_FOUND
     * @see #SETTER_EXCLUDE_MODIFIER
     * @see #SETTER_STYLE
     * @since 2.1.0
     */
    @ExperimentalApi
    public static final SettingKey<AssignmentType> ASSIGNMENT_TYPE = register(
            "assignment.type", AssignmentType.class, AssignmentType.FIELD);

    /**
     * Specifies the default value of the {@link AfterGenerate} hint
     * supplied from custom generators to the engine;
     * default is {@link AfterGenerate#POPULATE_NULLS_AND_DEFAULT_PRIMITIVES};
     * property name {@code hint.after.generate}.
     *
     * @see AfterGenerate
     * @since 2.0.0
     */
    public static final SettingKey<AfterGenerate> AFTER_GENERATE_HINT = register(
            "hint.after.generate", AfterGenerate.class, AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES);

    /**
     * Specifies whether a {@code null} can be generated for array elements;
     * default is {@code false}; property name {@code array.elements.nullable}.
     */
    public static final SettingKey<Boolean> ARRAY_ELEMENTS_NULLABLE = register(
            "array.elements.nullable", Boolean.class, false);

    /**
     * Specifies minimum length for arrays;
     * default is 2; property name {@code array.min.length}.
     */
    public static final SettingKey<Integer> ARRAY_MIN_LENGTH = register(
            "array.min.length", Integer.class, MIN_SIZE, MIN_ADJUSTER);

    /**
     * Specifies maximum length for arrays;
     * default is 6; property name {@code array.max.length}.
     */
    public static final SettingKey<Integer> ARRAY_MAX_LENGTH = register(
            "array.max.length", Integer.class, MAX_SIZE, MAX_ADJUSTER);

    /**
     * Specifies whether a null can be generated for arrays;
     * default is {@code false}; property name {@code array.nullable}.
     */
    public static final SettingKey<Boolean> ARRAY_NULLABLE = register(
            "array.nullable", Boolean.class, false);

    /**
     * Specifies whether values should be generated based on
     * <a href="https://beanvalidation.org/3.0/">Jakarta Bean Validation 3.0</a>
     * annotations, if present;
     * default is {@code false}; property name {@code bean.validation.api.enabled}.
     *
     * @since 2.7.0
     */
    @ExperimentalApi
    public static final SettingKey<Boolean> BEAN_VALIDATION_ENABLED = register(
            "bean.validation.enabled", Boolean.class, false);

    /**
     * Specifies whether a {@code null} can be generated for Boolean type;
     * default is {@code false}; property name {@code boolean.nullable}.
     */
    public static final SettingKey<Boolean> BOOLEAN_NULLABLE = register(
            "boolean.nullable", Boolean.class, false);

    /**
     * Specifies minimum value for bytes;
     * default is 1; property name {@code byte.min}.
     */
    public static final SettingKey<Byte> BYTE_MIN = register(
            "byte.min", Byte.class, (byte) NUMERIC_MIN, MIN_ADJUSTER);

    /**
     * Specifies maximum value for bytes;
     * default is 127; property name {@code byte.max}.
     */
    public static final SettingKey<Byte> BYTE_MAX = register(
            "byte.max", Byte.class, (byte) 127, MAX_ADJUSTER);

    /**
     * Specifies whether a {@code null} can be generated for Byte type;
     * default is {@code false}; property name {@code byte.nullable}.
     */
    public static final SettingKey<Boolean> BYTE_NULLABLE = register(
            "byte.nullable", Boolean.class, false);

    /**
     * Specifies whether a {@code null} can be generated for Character type;
     * default is {@code false}; property name {@code character.nullable}.
     */
    public static final SettingKey<Boolean> CHARACTER_NULLABLE = register(
            "character.nullable", Boolean.class, false);

    /**
     * Specifies whether a {@code null} can be generated for collection elements;
     * default is {@code false}; property name {@code collection.elements.nullable}.
     */
    public static final SettingKey<Boolean> COLLECTION_ELEMENTS_NULLABLE = register(
            "collection.elements.nullable", Boolean.class, false);

    /**
     * Specifies minimum size for collections;
     * default is 2; property name {@code collection.min.size}.
     */
    public static final SettingKey<Integer> COLLECTION_MIN_SIZE = register(
            "collection.min.size", Integer.class, MIN_SIZE, MIN_ADJUSTER);

    /**
     * Specifies maximum size for collections;
     * default is 6; property name {@code collection.max.size}.
     */
    public static final SettingKey<Integer> COLLECTION_MAX_SIZE = register(
            "collection.max.size", Integer.class, MAX_SIZE, MAX_ADJUSTER);

    /**
     * Specifies whether a {@code null} can be generated for collections;
     * default is {@code false}; property name {@code collection.nullable}.
     */
    public static final SettingKey<Boolean> COLLECTION_NULLABLE = register(
            "collection.nullable", Boolean.class, false);

    /**
     * Specifies minimum value for doubles;
     * default is 1; property name {@code double.min}.
     */
    public static final SettingKey<Double> DOUBLE_MIN = register(
            "double.min", Double.class, (double) NUMERIC_MIN, MIN_ADJUSTER);

    /**
     * Specifies maximum value for doubles;
     * default is 10000; property name {@code double.max}.
     */
    public static final SettingKey<Double> DOUBLE_MAX = register(
            "double.max", Double.class, (double) NUMERIC_MAX, MAX_ADJUSTER);

    /**
     * Specifies whether a {@code null} can be generated for Double type;
     * default is {@code false}; property name {@code double.nullable}.
     */
    public static final SettingKey<Boolean> DOUBLE_NULLABLE = register(
            "double.nullable", Boolean.class, false);

    /**
     * Specifies minimum value for floats;
     * default is 1; property name {@code float.min}.
     */
    public static final SettingKey<Float> FLOAT_MIN = register(
            "float.min", Float.class, (float) NUMERIC_MIN, MIN_ADJUSTER);

    /**
     * Specifies maximum value for floats;
     * default is 10000; property name {@code float.max}.
     */
    public static final SettingKey<Float> FLOAT_MAX = register(
            "float.max", Float.class, (float) NUMERIC_MAX, MAX_ADJUSTER);

    /**
     * Specifies whether a {@code null} can be generated for Float type;
     * default is {@code false}; property name {@code float.nullable}.
     */
    public static final SettingKey<Boolean> FLOAT_NULLABLE = register(
            "float.nullable", Boolean.class, false);

    /**
     * Specifies minimum value for integers;
     * default is 1; property name {@code integer.min}.
     */
    public static final SettingKey<Integer> INTEGER_MIN = register(
            "integer.min", Integer.class, NUMERIC_MIN, MIN_ADJUSTER);

    /**
     * Specifies maximum value for integers;
     * default is 10000; property name {@code integer.max}.
     */
    public static final SettingKey<Integer> INTEGER_MAX = register(
            "integer.max", Integer.class, NUMERIC_MAX, MAX_ADJUSTER);

    /**
     * Specifies whether a {@code null} can be generated for Integer type;
     * default is {@code false}; property name {@code integer.nullable}.
     */
    public static final SettingKey<Boolean> INTEGER_NULLABLE = register(
            "integer.nullable", Boolean.class, false);

    /**
     * Specifies minimum value for longs;
     * default is 1; property name {@code long.min}.
     */
    public static final SettingKey<Long> LONG_MIN = register(
            "long.min", Long.class, (long) NUMERIC_MIN, MIN_ADJUSTER);

    /**
     * Specifies maximum value for longs;
     * default is 10000; property name {@code long.max}.
     */
    public static final SettingKey<Long> LONG_MAX = register(
            "long.max", Long.class, (long) NUMERIC_MAX, MAX_ADJUSTER);

    /**
     * Specifies whether a {@code null} can be generated for Long type;
     * default is {@code false}; property name {@code long.nullable}.
     */
    public static final SettingKey<Boolean> LONG_NULLABLE = register(
            "long.nullable", Boolean.class, false);

    /**
     * Specifies whether a {@code null} can be generated for map keys;
     * default is {@code false}; property name {@code map.keys.nullable}.
     */
    public static final SettingKey<Boolean> MAP_KEYS_NULLABLE = register(
            "map.keys.nullable", Boolean.class, false);

    /**
     * Specifies minimum size for maps;
     * default is 2; property name {@code map.min.size}.
     */
    public static final SettingKey<Integer> MAP_MIN_SIZE = register(
            "map.min.size", Integer.class, MIN_SIZE, MIN_ADJUSTER);

    /**
     * Specifies maximum size for maps;
     * default is 6; property name {@code map.max.size}.
     */
    public static final SettingKey<Integer> MAP_MAX_SIZE = register(
            "map.max.size", Integer.class, MAX_SIZE, MAX_ADJUSTER);

    /**
     * Specifies whether a {@code null} can be generated for maps;
     * default is {@code false}; property name {@code map.nullable}.
     */
    public static final SettingKey<Boolean> MAP_NULLABLE = register(
            "map.nullable", Boolean.class, false);

    /**
     * Specifies whether a {@code null} can be generated for map values;
     * default is {@code false}; property name {@code map.values.nullable}.
     */
    public static final SettingKey<Boolean> MAP_VALUES_NULLABLE = register(
            "map.values.nullable", Boolean.class, false);

    /**
     * Specifies the maximum depth of the generated object tree;
     * default is {@code 8}; property name {@code max.depth}.
     *
     * @since 2.7.0
     */
    public static final SettingKey<Integer> MAX_DEPTH = register(
            "max.depth", Integer.class, 8);

    /**
     * Specifies the mode: strict (unused selectors will trigger an exception) or lenient;
     * default is {@link Mode#STRICT}; property name {@code mode}.
     *
     * @see Mode
     * @since 1.3.3
     */
    public static final SettingKey<Mode> MODE = register("mode", Mode.class, Mode.STRICT);

    /**
     * Specifies what should happen if an error occurs setting a field's value;
     * default is {@link OnSetFieldError#IGNORE}; property name {@code on.set.field.error}.
     *
     * <p><b>Warning:</b> an error caused by assigning an incompatible type is
     * considered a user error and is never ignored, despite this setting.
     *
     * @see OnSetFieldError
     * @since 2.1.0
     */
    @ExperimentalApi
    public static final SettingKey<OnSetFieldError> ON_SET_FIELD_ERROR = register(
            "on.set.field.error", OnSetFieldError.class, OnSetFieldError.IGNORE);

    /**
     * Specifies what should happen if an error occurs invoking a setter;
     * default is {@link OnSetMethodError#ASSIGN_FIELD}; property name {@code on.set.method.error}.
     *
     * @see OnSetMethodError
     * @since 2.1.0
     */
    @ExperimentalApi
    public static final SettingKey<OnSetMethodError> ON_SET_METHOD_ERROR = register(
            "on.set.method.error", OnSetMethodError.class, OnSetMethodError.ASSIGN_FIELD);

    /**
     * Specifies what should happen if a setter method for a field cannot be resolved;
     * default is {@link OnSetMethodNotFound#ASSIGN_FIELD}; property name {@code on.set.method.not.found}.
     *
     * @see OnSetMethodNotFound
     * @since 2.1.0
     */
    @ExperimentalApi
    public static final SettingKey<OnSetMethodNotFound> ON_SET_METHOD_NOT_FOUND = register(
            "on.set.method.not.found", OnSetMethodNotFound.class, OnSetMethodNotFound.ASSIGN_FIELD);

    /**
     * Specifies whether initialised fields are allowed to be overwritten;
     * default is {@code true}; property name {@code overwrite.existing.values}.
     *
     * @since 2.0.0
     */
    public static final SettingKey<Boolean> OVERWRITE_EXISTING_VALUES = register(
            "overwrite.existing.values", Boolean.class, true);

    /**
     * Specifies the seed value;
     * default is {@code null}; property name {@code seed}.
     *
     * @since 1.5.1
     */
    public static final SettingKey<Long> SEED = register(
            "seed", Long.class, null, null, true);

    /**
     * Specifies modifier exclusions for setter-methods;
     * default is {@code 0} (no exclusions);
     * property name {@code setter.exclude.modifier}.
     *
     * <p>This setting can be used to control which setter methods are allowed
     * to be invoked (based on method modifiers) when {@link #ASSIGNMENT_TYPE}
     * is set to {@link AssignmentType#METHOD}). For instance, using this
     * setting, it is possible to restrict method assignment to {@code public}
     * setters only (by default, a setter is invoked even if it is {@code private}).
     *
     * <p>Multiple modifiers can be specified using logical {@code OR}
     * operator. For example, the following allows only {@code public} methods:
     *
     * <pre>{@code
     *   int exclusions = MethodModifier.PACKAGE_PRIVATE
     *                  | MethodModifier.PROTECTED
     *                  | MethodModifier.PRIVATE;
     *
     *   Settings.create().set(Keys.SETTER_EXCLUDE_MODIFIER, exclusions);
     * }</pre>
     *
     * @see #ASSIGNMENT_TYPE
     * @see MethodModifier
     * @since 2.16.0
     */
    @ExperimentalApi
    public static final SettingKey<Integer> SETTER_EXCLUDE_MODIFIER = register(
            "setter.exclude.modifier", Integer.class, 0);

    /**
     * Indicates the naming convention of setter methods to use;
     * default is {@link SetterStyle#SET}; property name {@code setter.style}.
     *
     * @see SetterStyle
     * @since 2.1.0
     */
    @ExperimentalApi
    public static final SettingKey<SetterStyle> SETTER_STYLE = register(
            "setter.style", SetterStyle.class, SetterStyle.SET);

    /**
     * Specifies minimum value for shorts;
     * default is 1; property name {@code short.min}.
     */
    public static final SettingKey<Short> SHORT_MIN = register(
            "short.min", Short.class, (short) 1, MIN_ADJUSTER);

    /**
     * Specifies maximum value for shorts;
     * default is 10000; property name {@code short.max}.
     */
    public static final SettingKey<Short> SHORT_MAX = register(
            "short.max", Short.class, (short) NUMERIC_MAX, MAX_ADJUSTER);

    /**
     * Specifies whether a {@code null} can be generated for Short type;
     * default is {@code false}; property name {@code short.nullable}.
     */
    public static final SettingKey<Boolean> SHORT_NULLABLE = register(
            "short.nullable", Boolean.class, false);

    /**
     * Specifies whether an empty string can be generated;
     * default is {@code false}; property name {@code string.allow.empty}.
     */
    public static final SettingKey<Boolean> STRING_ALLOW_EMPTY = register(
            "string.allow.empty", Boolean.class, false);

    /**
     * Specifies whether generated Strings should be prefixed with field names;
     * default is {@code false}; property name {@code string.field.prefix.enabled}.
     *
     * @since 2.4.0
     */
    public static final SettingKey<Boolean> STRING_FIELD_PREFIX_ENABLED = register(
            "string.field.prefix.enabled", Boolean.class, false);

    /**
     * Specifies minimum length of strings;
     * default is 3; property name {@code string.min.length}.
     */
    public static final SettingKey<Integer> STRING_MIN_LENGTH = register(
            "string.min.length", Integer.class, 3, MIN_ADJUSTER);

    /**
     * Specifies maximum length of strings;
     * default is 10; property name {@code string.max.length}.
     */
    public static final SettingKey<Integer> STRING_MAX_LENGTH = register(
            "string.max.length", Integer.class, 10, MAX_ADJUSTER);

    /**
     * Specifies whether a {@code null} can be generated for String type;
     * default is {@code false}; property name {@code string.nullable}.
     */
    public static final SettingKey<Boolean> STRING_NULLABLE = register(
            "string.nullable", Boolean.class, false);

    // Note: keys must be collected after all keys have been initialised
    private static final Map<String, SettingKey<?>> SETTING_KEY_MAP = Collections.unmodifiableMap(settingKeyMap());

    private static Map<String, SettingKey<?>> settingKeyMap() {
        final Map<String, SettingKey<?>> map = new HashMap<>();
        for (SettingKey<?> key : ALL_KEYS) {
            map.put(key.propertyKey(), key);
        }
        return map;
    }

    /**
     * Returns all keys supported by Instancio.
     *
     * @return all keys
     */
    public static List<SettingKey<Object>> all() {
        return Collections.unmodifiableList(ALL_KEYS);
    }

    /**
     * Returns a {@link SettingKey} instance with the given property key.
     *
     * @param key to lookup
     * @return the setting key, or {@code null} if none found
     */
    @SuppressWarnings("unchecked")
    public static <T> SettingKey<T> get(@NotNull final String key) {
        return (SettingKey<T>) SETTING_KEY_MAP.get(key);
    }

    /**
     * A builder for creating custom setting keys.
     *
     * <p>When defining custom keys, specifying
     * {@link SettingKeyBuilder#withPropertyKey(String)} is optional since
     * not all settings will be defined in a properties file.
     * If {@code withPropertyKey()} is not specified, then a random
     * property key will be assigned.
     *
     * @param type of the value the key is associated with, not {@code null}
     * @param <T>  the value type
     * @return key builder
     * @since 2.12.0
     */
    @ExperimentalApi
    public static <T> SettingKeyBuilder<T> ofType(final Class<T> type) {
        return InternalKey.builder(type);
    }

    private static <T> SettingKey<T> register(
            @NotNull final String propertyKey,
            @NotNull final Class<T> type,
            @Nullable final Object defaultValue,
            @Nullable final RangeAdjuster rangeAdjuster,
            final boolean allowsNullValue) {

        final SettingKey<T> settingKey = new InternalKey<>(
                propertyKey, type, defaultValue, rangeAdjuster, allowsNullValue);

        ALL_KEYS.add((SettingKey<Object>) settingKey);
        return settingKey;
    }

    private static <T> SettingKey<T> register(
            @NotNull final String propertyKey,
            @NotNull final Class<T> type,
            @Nullable final Object defaultValue,
            @Nullable final RangeAdjuster rangeAdjuster) {

        return register(propertyKey, type, defaultValue, rangeAdjuster, false);
    }

    private static <T> SettingKey<T> register(
            @NotNull final String key,
            @NotNull final Class<T> type,
            @NotNull final Object defaultValue) {

        return register(key, type, defaultValue, null, false);
    }

    private Keys() {
        // non-instantiable
    }
}
