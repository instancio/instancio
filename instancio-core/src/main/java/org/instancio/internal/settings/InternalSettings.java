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
package org.instancio.internal.settings;

import org.instancio.internal.ApiValidator;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.StringConverters;
import org.instancio.settings.Keys;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;
import static org.instancio.internal.ApiValidator.validateKeyValue;
import static org.instancio.internal.ApiValidator.validateSubtype;

public final class InternalSettings implements Settings {
    private static final String TYPE_MAPPING_PREFIX = "subtype.";
    private static final boolean AUTO_ADJUST_ENABLED = true;
    private static final InternalSettings LOCKED_DEFAULTS = createLockedDefaults();

    private boolean isLockedForModifications;
    private Map<SettingKey<?>, Object> settingsMap;
    private Map<Class<?>, Class<?>> subtypeMap;

    private InternalSettings(
            final Map<SettingKey<?>, Object> settingsMap,
            final Map<Class<?>, Class<?>> subtypeMap) {

        this.settingsMap = new HashMap<>(settingsMap);
        this.subtypeMap = new HashMap<>(subtypeMap);
    }

    InternalSettings() {
        this(new HashMap<>(), new HashMap<>());
    }

    public static InternalSettings getLockedDefaults() {
        return LOCKED_DEFAULTS;
    }

    public static InternalSettings create() {
        return new InternalSettings();
    }

    private static InternalSettings createLockedDefaults() {
        final InternalSettings settings = new InternalSettings();
        for (SettingKey<Object> setting : Keys.all()) {
            settings.set(setting, setting.defaultValue());
        }
        return settings.lock();
    }

    @SuppressWarnings("PMD.UseDiamondOperator")
    public static InternalSettings from(final Map<Object, Object> map) {
        final InternalSettings settings = new InternalSettings();

        map.forEach((k, v) -> {
            final String key = k.toString();
            if (key.startsWith(TYPE_MAPPING_PREFIX)) {
                final String fromClass = key.replace(TYPE_MAPPING_PREFIX, "");
                settings.mapType(ReflectionUtils.getClass(fromClass), ReflectionUtils.getClass(v.toString()));
            } else {
                final SettingKey<Object> settingKey = Keys.get(key);

                if (settingKey == null) {
                    // If not defined in Keys, then this is a user-defined key
                    // Since the type is unknown, default to String type
                    final Class<?> stringClass = String.class;
                    final SettingKey<@Nullable Object> userDefinedSettingKey = new InternalKey<@Nullable Object>(
                            key, stringClass, null, null, true, false);

                    settings.set(userDefinedSettingKey, v);
                } else {
                    settings.set(settingKey, convertValueToKeyType(settingKey, v));
                }
            }
        });
        return settings;
    }

    public static InternalSettings from(final Settings other) {
        final InternalSettings src = (InternalSettings) other;
        return new InternalSettings(src.settingsMap, src.subtypeMap);
    }

    @Override
    public InternalSettings merge(@Nullable final Settings other) {
        final InternalSettings merged = new InternalSettings(settingsMap, subtypeMap);

        if (other != null) {
            final InternalSettings src = (InternalSettings) other;
            merged.settingsMap.putAll(src.settingsMap);
            merged.subtypeMap.putAll(src.subtypeMap);
        }
        return merged;
    }

    /**
     * Copies from {@code other} settings to this instance.
     *
     * @param other the other settings to copy from
     * @return this instance of settings
     */
    public InternalSettings copyFrom(@Nullable final Settings other) {
        if (other instanceof InternalSettings src) {
            checkLockedForModifications();
            settingsMap.putAll(src.settingsMap);
            subtypeMap.putAll(src.subtypeMap);
        }
        return this;
    }

    @NullUnmarked
    @Override
    @SuppressWarnings("unchecked")
    public <T extends @Nullable Object> T get(final SettingKey<T> key) {
        final Object value = settingsMap.get(ApiValidator.notNull(key, "Key must not be null"));

        if (value == null || key.type() == null || key.type().isAssignableFrom(value.getClass())) {
            return (T) value;
        }

        return convertValueToKeyType(key, value);
    }

    @Override
    public <T extends @Nullable Object> InternalSettings set(final SettingKey<T> key, final T value) {
        return set(key, value, AUTO_ADJUST_ENABLED);
    }

    /**
     * Set the setting with the given key to the specified value.
     * <p>
     * If {@code autoAdjust} parameter is {@code true}, then updating
     * range settings (such as a numeric range) will automatically adjust the
     * opposite bound (for example, if {@code min} is set higher than max, then
     * the {@code max} will be auto-adjusted to a higher value).
     *
     * @param key        to set, not {@code null}
     * @param value      to set
     * @param autoAdjust whether to auto-adjust related
     * @return this instance of settings
     */
    <T> InternalSettings set(final SettingKey<T> key, @Nullable final T value, final boolean autoAdjust) {
        checkLockedForModifications();
        validateKeyValue(key, value);
        settingsMap.put(key, value);

        if (autoAdjust && value != null && key instanceof AutoAdjustable) {
            final SettingKey<T> adjustable = SettingsSupport.getAutoAdjustable(key);
            if (adjustable != null) {
                doAutoAdjust((AutoAdjustable) adjustable, value);
            }
        }
        return this;
    }

    @Override
    public InternalSettings mapType(final Class<?> type, final Class<?> subtype) {
        checkLockedForModifications();
        validateSubtype(type, subtype);
        subtypeMap.put(type, subtype);
        return this;
    }

    @Override
    public Map<Class<?>, Class<?>> getSubtypeMap() {
        return Collections.unmodifiableMap(subtypeMap);
    }

    @Override
    public InternalSettings lock() {
        if (!isLockedForModifications) {
            settingsMap = Collections.unmodifiableMap(settingsMap);
            subtypeMap = Collections.unmodifiableMap(subtypeMap);
            isLockedForModifications = true;
        }
        return this;
    }

    @Override
    public boolean isLocked() {
        return isLockedForModifications;
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
                             "%nSettings subtypeMap:%s",
                isLockedForModifications,
                mapToString(new TreeMap<>(settingsMap)),
                mapToString(subtypeMap));
    }

    private static String mapToString(final Map<?, ?> map) {
        if (map.isEmpty()) return " {}";
        return "\n" + map.entrySet().stream()
                .map(e -> {
                    final String key = (e.getKey() instanceof SettingKey<?> settingKey)
                            ? settingKey.propertyKey()
                            : e.getKey().toString();

                    return String.format("\t'%s': %s", key, e.getValue());
                })
                .collect(joining("\n"));
    }

    private static <T> T convertValueToKeyType(final SettingKey<T> key, final Object value) {
        final Function<String, T> fn = StringConverters.getConverter(key.type());
        try {
            return fn.apply(value.toString());
        } catch (NumberFormatException ex) {
            throw Fail.withUsageError("invalid value %s (of type %s) for setting key %s",
                    value, value.getClass().getSimpleName(), key, ex);
        }
    }

    // a hack to workaround generics... we know the type is valid since it's a numeric settings
    @SuppressWarnings("unchecked")
    private <V extends Number & Comparable<V>> void doAutoAdjust(final AutoAdjustable key, final Object value) {
        key.autoAdjust(this, (V) value);
    }
}
