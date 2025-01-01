/*
 * Copyright 2022-2025 the original author or authors.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

final class SettingsSupport {

    private static final Map<SettingKey<?>, SettingKey<?>> AUTO_ADJUSTABLE_MAP = getAutoAdjustableKeys();

    @SuppressWarnings("unchecked")
    static <T> Optional<SettingKey<T>> getAutoAdjustable(final SettingKey<T> key) {
        return Optional.ofNullable((SettingKey<T>) AUTO_ADJUSTABLE_MAP.get(key));
    }

    private static Map<SettingKey<?>, SettingKey<?>> getAutoAdjustableKeys() {
        final Map<SettingKey<?>, SettingKey<?>> map = new HashMap<>();
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
        return Collections.unmodifiableMap(map);
    }

    private SettingsSupport() {
        // non-instantiable
    }
}
