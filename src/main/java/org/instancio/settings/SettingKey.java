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
 * <p>
 * Configuration file is read from {@code instancio.properties}
 * at the root of the classpath.
 */
public interface SettingKey {

    /**
     * A key used in the properties file.
     *
     * @return property key
     */
    String key();

    /**
     * Type of the property value.
     *
     * @return value class
     */
    Class<?> type();

    /**
     * Default value for this key.
     *
     * @param <T> type of the value
     * @return default value
     */
    <T> T defaultValue();

}
