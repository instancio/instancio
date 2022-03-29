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
import java.util.function.Function;

import static org.instancio.internal.InstancioValidator.validateSettingKey;
import static org.instancio.internal.InstancioValidator.validateSubtypeMapping;

/**
 * Instancio settings API.
 */
public class Settings {
    private static final String TYPE_MAPPING_PREFIX = "type.mapping.";

    private boolean isLockedForModifications;
    private Map<Object, Object> settingsMap;
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
        for (Setting setting : Setting.values()) {
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
    public static Settings from(final Map<?, ?> map) {
        final Settings settings = new Settings();

        map.forEach((k, v) -> {
            final String key = k.toString();
            if (key.startsWith(TYPE_MAPPING_PREFIX)) {
                final String fromClass = key.replace(TYPE_MAPPING_PREFIX, "");
                settings.mapType(ReflectionUtils.getClass(fromClass), ReflectionUtils.getClass(v.toString()));
            } else {
                final SettingKey settingKey = Setting.getByKey(key);
                final Function<String, Object> fn = ValueOfFunctions.getFunction(settingKey.type());
                final Object val = fn.apply(v.toString());
                settings.set(settingKey, val);
            }
        });

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
     * Set setting with given key to the specified value.
     *
     * @param key   to set
     * @param value to set
     * @return updated settings
     */
    public Settings set(final SettingKey key, final Object value) {
        checkLockedForModifications();
        validateSettingKey(key, value);
        settingsMap.put(key, value);
        return this;
    }

    /**
     * Map 'from' supertype to 'to' subtype.
     *
     * @param from supertype class
     * @param to   subtype class
     * @return updated settings
     */
    public Settings mapType(final Class<?> from, Class<?> to) {
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
        isLockedForModifications = true;
        return this;
    }

    private void checkLockedForModifications() {
        if (isLockedForModifications) {
            throw new UnsupportedOperationException("Settings are read-only");
        }
    }
}
