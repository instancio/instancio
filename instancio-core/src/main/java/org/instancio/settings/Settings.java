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

import org.instancio.util.ReflectionUtils;
import org.instancio.util.Verify;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;
import static org.instancio.internal.ApiValidator.validateNotNullAndType;
import static org.instancio.internal.ApiValidator.validateSubtypeMapping;

/**
 * Instancio settings API.
 *
 * @since 1.0.1
 */
public class Settings {
    private static final String TYPE_MAPPING_PREFIX = "type.mapping.";
    private static final boolean AUTO_ADJUST_ENABLED = true;

    private boolean isLockedForModifications;
    private Map<SettingKey, Object> settingsMap;
    private Map<Class<?>, Class<?>> subtypeMap;

    private Settings() {
        this.settingsMap = new HashMap<>();
        this.subtypeMap = new HashMap<>();
    }

    /**
     * Create a new instance of empty settings.
     *
     * @return empty settings
     */
    public static Settings create() {
        return new Settings();
    }

    /**
     * Create default settings.
     *
     * @return settings containing defaults
     */
    public static Settings defaults() {
        Settings settings = new Settings();
        for (SettingKey setting : Keys.all()) {
            settings.set(setting, setting.defaultValue());
        }
        return settings;
    }

    /**
     * Create settings from the given map.
     *
     * @param map to create settings from
     * @return settings
     */
    public static Settings from(final Map<Object, Object> map) {
        final Settings settings = new Settings();

        map.forEach((k, v) -> {
            final String key = k.toString();
            if (key.startsWith(TYPE_MAPPING_PREFIX)) {
                final String fromClass = key.replace(TYPE_MAPPING_PREFIX, "");
                settings.mapType(ReflectionUtils.getClass(fromClass), ReflectionUtils.getClass(v.toString()));
            } else {
                final SettingKey settingKey = Keys.get(key);
                final Function<String, Object> fn = ValueOfFunctions.getFunction(settingKey.type());
                final Object val = fn.apply(v.toString());
                settings.set(settingKey, val);
            }
        });

        return settings;
    }

    /**
     * Create settings from the given settings.
     *
     * @param other settings to create settings from
     * @return a new instance of settings
     */
    public static Settings from(final Settings other) {
        final Settings settings = new Settings();
        settings.settingsMap.putAll(other.settingsMap);
        settings.subtypeMap.putAll(other.subtypeMap);
        return settings;
    }

    /**
     * Creates a new instance of settings by merging given settings with these settings.
     *
     * @param other settings to merge
     * @return new instance of merged settings
     */
    public Settings merge(@Nullable final Settings other) {
        final Settings merged = Settings.create();
        merged.settingsMap.putAll(settingsMap);
        merged.subtypeMap.putAll(subtypeMap);

        if (other != null) {
            merged.settingsMap.putAll(other.settingsMap);
            merged.subtypeMap.putAll(other.subtypeMap);
        }
        return merged;
    }

    /**
     * Get setting value for given key.
     *
     * @param key setting key
     * @param <T> setting value type
     * @return value for given key, or {@code null} if none.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final SettingKey key) {
        return (T) settingsMap.get(Verify.notNull(key, "Key must not be null"));
    }

    /**
     * Set the setting with the given key to the specified value.
     * <p>
     * Note: when updating range settings (such as {@link Keys#COLLECTION_MIN_SIZE}
     * and {@link Keys#COLLECTION_MAX_SIZE}), range bounds are auto-adjusted by
     * {@link org.instancio.util.Constants#RANGE_ADJUSTMENT_PERCENTAGE} if the new minimum
     * is higher than the current maximum, and vice versa.
     *
     * @param key   to set
     * @param value to set
     * @return updated settings
     */
    public Settings set(final SettingKey key, final Object value) {
        return set(key, value, AUTO_ADJUST_ENABLED);
    }

    /**
     * Set the setting with the given key to the specified value.
     * <p>
     * If {@code autoAdjust} parameter is {@code true}, then updating
     * a range setting (such numeric range) will automatically adjust the
     * opposite bound (for example, min is set to higher than max, then
     * max will be auto-adjusted).
     *
     * @param key        to set
     * @param value      to set
     * @param autoAdjust whether to auto-adjust related
     * @return updated setting
     */
    Settings set(final SettingKey key, final Object value, boolean autoAdjust) {
        checkLockedForModifications();
        validateNotNullAndType(key, value);
        settingsMap.put(key, value);

        if (autoAdjust) {
            Keys.getAutoAdjustable(key).ifPresent(k -> k.autoAdjust(this, new NumberCaster<>().cast(value)));
        }

        return this;
    }

    /**
     * Map 'from' supertype to 'to' subtype.
     *
     * @param from supertype class
     * @param to   subtype class
     * @return updated settings
     */
    public Settings mapType(final Class<?> from, final Class<?> to) {
        checkLockedForModifications();
        validateSubtypeMapping(from, to);
        subtypeMap.put(from, to);
        return this;
    }

    /**
     * Returns a read-only view of the subtype map.
     *
     * @return subtype map
     */
    public Map<Class<?>, Class<?>> getSubtypeMap() {
        return Collections.unmodifiableMap(subtypeMap);
    }

    /**
     * Locks these settings for further modifications,
     * making this instance immutable.
     *
     * @return read-only settings
     */
    public Settings lock() {
        settingsMap = Collections.unmodifiableMap(settingsMap);
        subtypeMap = Collections.unmodifiableMap(subtypeMap);
        isLockedForModifications = AUTO_ADJUST_ENABLED;
        return this;
    }

    private void checkLockedForModifications() {
        if (isLockedForModifications) {
            throw new UnsupportedOperationException("This instance of Settings has been locked and is read-only");
        }
    }

    @Override
    public String toString() {
        return String.format("Settings[%nisLockedForModifications: %s" +
                        "%nsettingsMap:%s" +
                        "%nsubtypeMap:%s",
                isLockedForModifications,
                mapToString(new TreeMap<>(settingsMap)), mapToString(subtypeMap));
    }

    private static String mapToString(final Map<?, ?> map) {
        if (map.isEmpty()) return " {}";
        return "\n" + map.entrySet().stream()
                .map(e -> String.format("\t'%s': %s", e.getKey(), e.getValue()))
                .collect(joining("\n"));
    }

    // a hack to workaround generics... we know the type is valid since it's a numeric settings
    private static class NumberCaster<T extends Number & Comparable<T>> {
        @SuppressWarnings("unchecked")
        private T cast(final Object obj) {
            return (T) obj;
        }
    }
}
