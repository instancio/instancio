/*
 * Copyright 2022-2024 the original author or authors.
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

/**
 * Provides an API for specifying custom {@link Settings}.
 *
 * @since 5.0.0
 */
interface InstancioSettingsApi {

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
    <V> InstancioSettingsApi withSetting(SettingKey<V> key, V value);

    /**
     * Overrides default {@link Settings} for generating values.
     * The {@link Settings} class supports various parameters, such as
     * collection sizes, string lengths, numeric ranges, and more.
     * For a list of overridable settings, refer to the {@link Keys} class.
     *
     * @param settings the settings to use
     * @return API builder reference
     * @see Keys
     * @see #withSetting(SettingKey, Object)
     * @since 5.0.0
     */
    InstancioSettingsApi withSettings(Settings settings);
}
