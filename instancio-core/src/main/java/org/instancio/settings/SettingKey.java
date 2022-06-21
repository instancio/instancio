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

/**
 * A setting key for modifying configuration.
 *
 * @since 1.0.1
 */
public interface SettingKey extends Comparable<SettingKey> {

    /**
     * A property key used in the properties file.
     *
     * @return property key
     * @since 1.2.0
     */
    String propertyKey();

    /**
     * Type of the property value.
     *
     * @return value class
     * @since 1.0.1
     */
    <T> Class<T> type();

    /**
     * Default value for this key.
     *
     * @param <T> type of the value
     * @return default value
     * @since 1.0.1
     */
    <T> T defaultValue();

    /**
     * Indicates whether the value for this key can be set to {@code null}.
     *
     * @return {@code true} if {@code null} is allowed, {@code false} otherwise
     * @since 1.5.1
     */
    boolean allowsNullValue();

    /**
     * Auto-adjusts the {@link Settings} value for this key based on the value of another setting key.
     *
     * @param settings   to adjust
     * @param otherValue value of the other setting to base the adjustment off
     * @since 1.2.0
     */
    default <T extends Number & Comparable<T>> void autoAdjust(Settings settings, T otherValue) {
        // no-op by default
    }
}
