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
package org.instancio.generator.lang;

import org.instancio.generator.GeneratorSpec;

/**
 * Generator spec for Strings.
 */
public interface StringGeneratorSpec extends GeneratorSpec<String> {

    /**
     * Specifies a String prefix.
     *
     * @param prefix for generated strings
     * @return spec builder
     */
    StringGeneratorSpec prefix(String prefix);

    /**
     * Indicates that {@code null} value can be generated.
     *
     * @return spec builder
     */
    StringGeneratorSpec nullable();

    /**
     * Indicates that an empty string can be generated.
     *
     * @return spec builder
     */
    StringGeneratorSpec allowEmpty();

    /**
     * Length of string to generate.
     *
     * @param length exact length to generate
     * @return spec builder
     */
    StringGeneratorSpec length(int length);

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
}
