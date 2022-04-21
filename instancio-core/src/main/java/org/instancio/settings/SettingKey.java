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
 */
public interface SettingKey extends Comparable<SettingKey> {

    /**
     * A property key used in the properties file.
     *
     * @return property key
     */
    String propertyKey();

    /**
     * Type of the property value.
     *
     * @return value class
     */
    <T> Class<T> type();

    /**
     * Default value for this key.
     *
     * @param <T> type of the value
     * @return default value
     */
    <T> T defaultValue();

    /**
     * Auto-adjusts the {@link Settings} value for this key based on the other setting value.
     *
     * @param settings   to adjust
     * @param otherValue value of the other setting to base the adjustment off
     */
    default void autoAdjust(Settings settings, Object otherValue) {
        // no-op by default
    }
}
