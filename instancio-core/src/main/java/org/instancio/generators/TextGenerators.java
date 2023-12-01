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

package org.instancio.generators;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.CsvGeneratorSpec;
import org.instancio.generator.specs.LoremIpsumGeneratorSpec;
import org.instancio.generator.specs.TextPatternAsGeneratorSpec;
import org.instancio.generator.specs.UUIDStringGeneratorSpec;
import org.instancio.internal.generator.text.CsvGenerator;
import org.instancio.internal.generator.text.LoremIpsumGenerator;
import org.instancio.internal.generator.text.TextPatternGenerator;
import org.instancio.internal.generator.text.UUIDStringGenerator;

/**
 * Contains built-in text generators.
 *
 * @since 1.1.9
 */
public class TextGenerators {

    private final GeneratorContext context;

    public TextGenerators(final GeneratorContext context) {
        this.context = context;
    }

    /**
     * Generates CSV.
     *
     * @return CSV generator
     * @since 2.12.0
     */
    @ExperimentalApi
    public CsvGeneratorSpec csv() {
        return new CsvGenerator(context);
    }

    /**
     * Generates "Lorem ipsum" text.
     *
     * @return lorem ipsum text generator
     * @since 1.5.3
     */
    public LoremIpsumGeneratorSpec loremIpsum() {
        return new LoremIpsumGenerator(context);
    }

    /**
     * Generates a random string based on the specified pattern template.
     * The template supports the following hashtags:
     *
     * <ul>
     *   <li>{@code #a} - alphanumeric character {@code [a-z, A-Z, 0-9]}</li>
     *   <li>{@code #c} - lower case character {@code [a-z]}</li>
     *   <li>{@code #C} - upper case character {@code [A-Z]}</li>
     *   <li>{@code #d} - digit {@code [0-9]}</li>
     *   <li>{@code ##} - hash symbol escape</li>
     * </ul>
     * <p>
     * Examples:
     * <pre>{@code
     *   "#a#a#a" -> "k4W"
     *   "#d#d#d-#d#d#d-#d#d#d" -> "123-45-67"
     *   "Foo: #C#c#c" -> "Foo: Xyz"
     *   "###d#d#d" -> "#123"
     * }</pre>
     *
     * @param pattern to generate
     * @return string pattern generator
     * @since 1.1.9
     */
    public TextPatternAsGeneratorSpec pattern(final String pattern) {
        return new TextPatternGenerator(context, pattern);
    }

    /**
     * Generates a {@code UUID} value as a string. By default, the generated
     * string is formatted as {@link java.util.UUID#toString()}.
     *
     * @return {@code UUID} string generator
     * @since 1.5.0
     */
    public UUIDStringGeneratorSpec uuid() {
        return new UUIDStringGenerator(context);
    }
}
