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
package org.instancio.generator.specs;

/**
 * Generator spec for Strings.
 *
 * @since 1.0.1
 */
public interface StringGeneratorSpec extends NullableGeneratorSpec<String> {

    /**
     * Specifies the prefix to prepend to generated strings.
     *
     * @param prefix for generated strings
     * @return spec builder
     */
    StringGeneratorSpec prefix(String prefix);

    /**
     * Specifies the suffix to append to generated strings.
     *
     * @param suffix for generated strings
     * @return spec builder
     * @since 2.7.0
     */
    StringGeneratorSpec suffix(String suffix);

    /**
     * Indicates that an empty string can be generated.
     *
     * @return spec builder
     */
    StringGeneratorSpec allowEmpty();

    /**
     * Indicates if empty string can be generated.
     *
     * @param isAllowed if {@code true}, empty strings can be generated
     * @return spec builder
     * @since 2.7.0
     */
    StringGeneratorSpec allowEmpty(boolean isAllowed);

    /**
     * Length of string to generate.
     *
     * @param length exact length to generate
     * @return spec builder
     */
    StringGeneratorSpec length(int length);

    /**
     * Length of string to generate.
     *
     * @param minLength minimum length (inclusive)
     * @param maxLength maximum length (inclusive)
     * @return spec builder
     */
    StringGeneratorSpec length(int minLength, int maxLength);

    /**
     * Minimum length of string to generate.
     *
     * @param length minimum length (inclusive)
     * @return spec builder
     */
    StringGeneratorSpec minLength(int length);

    /**
     * Maximum length of string to generate.
     *
     * @param length maximum length (inclusive)
     * @return spec builder
     */
    StringGeneratorSpec maxLength(int length);

    /**
     * Generates a lower case string.
     *
     * @return spec builder
     */
    StringGeneratorSpec lowerCase();

    /**
     * Generates an upper case string.
     *
     * @return spec builder
     */
    StringGeneratorSpec upperCase();

    /**
     * Generates a mixed case string.
     *
     * @return spec builder
     */
    StringGeneratorSpec mixedCase();

    /**
     * Generates an alphanumeric string, upper case by default.
     *
     * @return spec builder
     */
    StringGeneratorSpec alphaNumeric();

    /**
     * Generates a string comprised of only digits.
     *
     * @return spec builder
     */
    StringGeneratorSpec digits();

    /**
     * Generates a hexadecimal string, upper case by default.
     *
     * @return spec builder
     * @since 2.11.0
     */
    StringGeneratorSpec hex();

    @Override
    StringGeneratorSpec nullable();
}
