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
package org.instancio.generators;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.specs.CsvGeneratorSpec;
import org.instancio.generator.specs.LoremIpsumGeneratorSpec;
import org.instancio.generator.specs.TextPatternGeneratorSpec;
import org.instancio.generator.specs.UUIDStringGeneratorSpec;
import org.instancio.generator.specs.WordGeneratorSpec;

/**
 * Contains built-in text generators.
 *
 * @since 1.1.9
 */
public interface TextGenerators {

    /**
     * Generates CSV.
     *
     * @return CSV generator
     * @since 2.12.0
     */
    @ExperimentalApi
    CsvGeneratorSpec csv();

    /**
     * Generates "Lorem ipsum" text.
     *
     * @return lorem ipsum text generator
     * @since 1.5.3
     */
    LoremIpsumGeneratorSpec loremIpsum();

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
    TextPatternGeneratorSpec pattern(String pattern);

    /**
     * Generates a {@code UUID} value as a string. By default, the generated
     * string is formatted as {@link java.util.UUID#toString()}.
     *
     * @return {@code UUID} string generator
     * @since 1.5.0
     */
    UUIDStringGeneratorSpec uuid();

    /**
     * Generates random words. This spec generates various word classes,
     * including nouns, verbs, adjectives, and adverbs.
     *
     * <p>Example:
     * <pre>{@code
     * record Example(String word) {}
     *
     * Example example = Instancio.of(Example.class)
     *     .generate(field(Example::word), gen -> gen.text().word().noun())
     *     .create();
     *
     * // Sample output: Example[word=achievement]
     * }</pre>
     *
     * <p>You can construct phrases or sentences using a {@code Supplier}:
     *
     * <pre>{@code
     * record Company(String website) {}
     *
     * Supplier<String> websiteSupplier = () -> String.format(
     *     "https://www.%s-%s.com",
     *     Instancio.gen().text().word().adjective().get(),
     *     Instancio.gen().text().word().noun().get());
     *
     * List<Company> companies = Instancio.ofList(Company.class)
     *     .size(3)
     *     .supply(field(Company::website), websiteSupplier)
     *     .create();
     *
     * // Sample output:
     * [[Company[website=https://www.global-bidder.com],
     *   Company[website=https://www.independent-beat.com],
     *   Company[website=https://www.promotional-clock.com]]
     * }</pre>
     *
     * @return word generator spec
     * @since 5.1.0
     */
    @ExperimentalApi
    WordGeneratorSpec word();
}
