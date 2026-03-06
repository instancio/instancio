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

import org.instancio.documentation.ExperimentalApi;
import org.instancio.generators.ValueSpecs;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;
import org.jspecify.annotations.Nullable;

/**
 * A shorthand API for generating value types,
 * such as strings, numbers, dates, etc.
 *
 * @since 5.0.0
 */
@ExperimentalApi
public interface InstancioGenApi extends SettingsApi, ValueSpecs {

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    @ExperimentalApi
    InstancioGenApi withSettings(Settings settings);

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    @ExperimentalApi
    <V extends @Nullable Object> InstancioGenApi withSetting(SettingKey<V> key, V value);
}
