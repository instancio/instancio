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
package org.instancio.settings;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.specs.StringGeneratorSpec;

/**
 * A setting that specifies the type of {@code String} to generate.
 * String type can be set using the {@link Keys#STRING_TYPE} setting:
 *
 * <pre>{@code
 * Person person = Instancio.of(Person.class)
 *     .withSetting(Keys.STRING_TYPE, StringType.ALPHANUMERIC) // all strings alphanumeric
 *     .create();
 * }</pre>
 *
 * @see Keys#STRING_TYPE
 * @since 4.7.0
 */
@ExperimentalApi
public enum StringType {

    /**
     * Represents an alphabetic string, uppercase by default,
     * that consists of characters {@code [A-Z]}.
     *
     * @since 4.7.0
     */
    ALPHABETIC,

    /**
     * Represents an alphanumeric string, uppercase by default,
     * that consists of characters {@code [0-9A-Z]}.
     *
     * <p>The equivalent string generator spec method is
     * {@link StringGeneratorSpec#alphaNumeric()}.
     *
     * @since 4.7.0
     */
    ALPHANUMERIC,

    /**
     * Represents a string that consists of digits {@code [0-9]}.
     *
     * <p>The equivalent string generator spec method is
     * {@link StringGeneratorSpec#digits()}.
     *
     * @since 4.7.0
     */
    DIGITS,

    /**
     * Represents a hexadecimal string, uppercase by default,
     * that consists of characters {@code [0-9A-F]}
     *
     * <p>The equivalent string generator spec method is
     * {@link StringGeneratorSpec#hex()}.
     *
     * @since 4.7.0
     */
    HEX,

    /**
     * Represents a string sequence that starts from {@code "1"}
     * and increments by one.
     *
     * <p>The equivalent string generator spec method is
     * {@link StringGeneratorSpec#numericSequence()}.
     *
     * @since 4.7.0
     */
    NUMERIC_SEQUENCE,

    /**
     * Represents a Unicode string that consists of random
     * code points, excluding the following character types:
     *
     * <ul>
     *   <li>{@link Character#PRIVATE_USE}</li>
     *   <li>{@link Character#SURROGATE}</li>
     *   <li>{@link Character#UNASSIGNED}</li>
     * </ul>
     *
     * <p>The equivalent string generator spec method is
     * {@link StringGeneratorSpec#unicode(Character.UnicodeBlock...)}.
     *
     * @since 4.7.0
     */
    UNICODE
}
