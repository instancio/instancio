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
package org.instancio;

import org.instancio.settings.Keys;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;
import org.jspecify.annotations.Nullable;

/**
 * Provides an API for specifying custom {@link Settings}.
 *
 * @since 5.0.0
 */
public interface SettingsApi {

    /**
     * Overrides the setting for the given {@code key}
     * with the specified {@code value}.
     *
     * @param key   the setting key to override
     * @param value the setting value
     * @param <V>   the setting value type
     * @return API builder reference
     * @see Keys
     * @see #withSettings(Settings)
     * @since 5.0.0
     */
    <V extends @Nullable Object> SettingsApi withSetting(SettingKey<V> key, V value);

    /**
     * Merges the specified {@link Settings} with the current settings,
     * allowing for the addition and update of settings.
     *
     * <p>Use this method to apply custom settings to override the default
     * ones. The provided settings will be combined with the existing settings,
     * updating any overlapping values and adding any new ones.
     *
     * @param settings the custom settings to merge with the current settings
     * @return API builder reference
     * @see Keys
     * @see #withSetting(SettingKey, Object)
     * @since 5.0.0
     */
    SettingsApi withSettings(Settings settings);
}
