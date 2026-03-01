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
package org.instancio.settings;

import org.instancio.documentation.ExperimentalApi;
import org.jspecify.annotations.Nullable;

/**
 * A setting key represents a configuration item that can be
 * stored in a {@link Settings} instance.
 *
 * @param <T> type of the value
 * @see Keys
 * @see Settings
 * @since 1.0.1
 */
public interface SettingKey<T extends @Nullable Object> {

    /**
     * The type of value associated with this key.
     *
     * <p>This method is used for converting a value from a string
     * to the specified {@code type()} when retrieving the value
     * from {@link Settings}.
     *
     * @return the type of value associated with this key
     * @since 1.0.1
     */
    Class<T> type();

    /**
     * A unique property key identifying this setting.
     * The property key is used to identify the setting in a properties file.
     *
     * @return a unique property key identifying this setting
     * @since 1.2.0
     */
    String propertyKey();

    /**
     * Default value for this key.
     *
     * @return default value, or {@code null} if not defined
     * @since 1.0.1
     */
    T defaultValue();

    /**
     * Indicates whether the value for this key can be set to {@code null}.
     * Setting a {@code null} for a key that does not accept {@code null}
     * will produce an exception.
     *
     * @return {@code true} if {@code null} is allowed, {@code false} otherwise
     * @since 1.5.1
     */
    default boolean allowsNullValue() {
        return true;
    }

    /**
     * A builder for creating custom keys.
     *
     * @param <T> the type of value associated with this key
     * @since 2.12.0
     */
    @ExperimentalApi
    interface SettingKeyBuilder<T extends @Nullable Object> {

        /**
         * Specifies the value's type.
         *
         * @param type of value
         * @return key builder
         * @since 2.12.0
         */
        SettingKeyBuilder<T> ofType(Class<T> type);

        /**
         * Specifies the property key.
         *
         * <p>When defining custom keys, it is advisable to use
         * a custom prefix to distinguish from and to avoid potential clashes
         * with built-in keys, for example:
         *
         * <pre>{@code
         *   com.acme.string.length=10
         * }</pre>
         *
         * <p>If {@code withPropertyKey()} is not specified,
         * then an auto-generated property key will be assigned.
         *
         * @param propertyKey a unique property key
         * @return key builder
         * @since 2.12.0
         */
        SettingKeyBuilder<T> withPropertyKey(String propertyKey);

        /**
         * Returns the created setting key.
         *
         * @return the setting key
         * @since 2.12.0
         */
        SettingKey<T> create();
    }
}
