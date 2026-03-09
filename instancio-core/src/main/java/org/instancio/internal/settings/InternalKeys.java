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

import org.instancio.settings.Keys;
import org.instancio.settings.SettingKey;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class InternalKeys {

    private static final InternalKeys INSTANCE = new InternalKeys();
    // Ensures Keys classes is loaded before InternalSettings is used, so all SettingKeys are registered
    @SuppressWarnings("unused")
    private static final SettingKey<?> KEYS_INIT_GUARD = Keys.ASSIGNMENT_TYPE;
    private static final int INITIAL_CAPACITY = 100;

    private final List<SettingKey<Object>> allKeys = new ArrayList<>(INITIAL_CAPACITY);
    private final Map<String, SettingKey<?>> stringSettingKeyMap = new HashMap<>(INITIAL_CAPACITY);

    private InternalKeys() {
        // non-instantiable
    }

    public static InternalKeys getInstance() {
        return INSTANCE;
    }

    List<SettingKey<Object>> all() {
        return Collections.unmodifiableList(INSTANCE.allKeys);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends @Nullable Object> SettingKey<T> get(final String key) {
        return (SettingKey<T>) stringSettingKeyMap.get(key);
    }

    public <T extends @Nullable Object> SettingKey<T> register(
            final String propertyKey,
            final Class<?> type,
            final T defaultValue,
            @Nullable final RangeAdjuster rangeAdjuster,
            final boolean allowsNullValue,
            final boolean allowsNegative) {

        final SettingKey<T> settingKey = new InternalKey<>(
                propertyKey, type, defaultValue, rangeAdjuster, allowsNullValue, allowsNegative);

        allKeys.add((SettingKey<Object>) settingKey);
        stringSettingKeyMap.put(settingKey.propertyKey(), settingKey);
        return settingKey;
    }

    public <T extends @Nullable Object> SettingKey<T> registerRequiredAdjustable(
            final String propertyKey,
            final Class<T> type,
            final T defaultValue,
            @Nullable final RangeAdjuster rangeAdjuster,
            final boolean allowsNegative) {

        return register(propertyKey, type, defaultValue, rangeAdjuster, false, allowsNegative);
    }

    public <T extends @Nullable Object> SettingKey<T> registerRequiredNonAdjustable(
            final String key,
            final Class<T> type,
            final T defaultValue) {

        return register(key, type, defaultValue, null, false, false);
    }
}
