/*
 * Copyright 2022-2023 the original author or authors.
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
 * A setting key represents a configuration item and has the following properties:
 *
 * <ul>
 *   <li>{@link #propertyKey()} - property name that can be used in a configuration file</li>
 *   <li>{@link #defaultValue()} - that will be used if there is no configuration file present</li>
 * </ul>
 *
 * @param <T> type of the value
 * @see Keys
 * @see Settings
 * @since 1.0.1
 */
public interface SettingKey<T> {

    /**
     * A property key that can be used to configure this setting in a properties file.
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
    Class<T> type();

    /**
     * Default value for this key.
     *
     * @return default value
     * @since 1.0.1
     */
    T defaultValue();

    /**
     * Indicates whether the value for this key can be set to {@code null}.
     *
     * @return {@code true} if {@code null} is allowed, {@code false} otherwise
     * @since 1.5.1
     */
    boolean allowsNullValue();

}
