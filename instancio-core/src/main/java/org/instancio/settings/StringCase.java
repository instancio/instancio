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
package org.instancio.settings;

import org.instancio.documentation.ExperimentalApi;

/**
 * A setting that specifies the case of generated strings.
 *
 * <p>String case can be set using the {@link Keys#STRING_CASE} setting:
 *
 * <pre>{@code
 * Person person = Instancio.of(Person.class)
 *     .withSetting(Keys.STRING_CASE, StringCase.MIXED) // all strings mixed-case
 *     .create();
 * }</pre>
 *
 * <p>Note that this setting is only applicable to string types listed below,
 * otherwise this setting is ignored:
 *
 * <ul>
 *   <li>{@link StringType#ALPHABETIC}</li>
 *   <li>{@link StringType#ALPHANUMERIC}</li>
 *   <li>{@link StringType#HEX}</li>
 * </ul>
 *
 * @see Keys#STRING_CASE
 * @since 4.8.0
 */
@ExperimentalApi
public enum StringCase {

    /**
     * Represents lowercase strings {@code [a-z]}.
     *
     * @since 4.8.0
     */
    LOWER,

    /**
     * Represents uppercase strings {@code [A-Z]}.
     *
     * @since 4.8.0
     */
    UPPER,

    /**
     * Represents mixed-case strings {@code [A-Z]}.
     *
     * @since 4.8.0
     */
    MIXED
}
