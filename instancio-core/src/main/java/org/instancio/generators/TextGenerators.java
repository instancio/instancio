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
package org.instancio.generators;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.specs.CsvGeneratorSpec;
import org.instancio.generator.specs.LoremIpsumGeneratorSpec;
import org.instancio.generator.specs.TextPatternGeneratorSpec;
import org.instancio.generator.specs.UUIDStringGeneratorSpec;
import org.instancio.generator.specs.WordGeneratorSpec;
import org.instancio.generator.specs.WordTemplateGeneratorSpec;

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
     * @see #wordTemplate(String)
     * @since 5.1.0
     */
    @ExperimentalApi
    WordGeneratorSpec word();

    /**
     * Generates strings based on the specified template.
     *
     * <p>The template can include placeholders representing different
     * parts of speech, such as:
     *
     * <ul>
     *   <li>{@code ${adjective}} produces a random adjective</li>
     *   <li>{@code ${adverb}} produces a random adverb</li>
     *   <li>{@code ${noun}} produces a random noun</li>
     *   <li>{@code ${verb}} produces a random verb</li>
     * </ul>
     *
     * <p>These placeholders will be dynamically replaced with randomly generated words
     * matching their respective categories. For example:
     *
     * <pre>{@code
     * record Company(String website) {}
     *
     * List<Company> companies = Instancio.ofList(Company.class)
     *     .size(3)
     *     .generate(field(Company::website), gen -> gen.text().wordTemplate("${adjective}-${noun}.com"))
     *     .create();
     *
     * // Sample output:
     * [[Company[website=global-bidder.com],
     *   Company[website=independent-beat.com],
     *   Company[website=promotional-clock.com]]
     * }</pre>
     *
     * <p>The placeholders are case-sensitive and support three case styles
     * for the output, for example:
     *
     * <ul>
     *   <li>{@code ${noun}} - Outputs a lowercase word (e.g., "cat").</li>
     *   <li>{@code ${Noun}} - Outputs a capitalised word (e.g., "Cat").</li>
     *   <li>{@code ${NOUN}} - Outputs an uppercase word (e.g., "CAT").</li>
     * </ul>
     *
     * <p><b>Note:</b> The behavior for mixed-case placeholders,
     * such as {@code ${NoUn}}, is undefined. Avoid using such placeholders
     * to ensure consistent results.
     *
     * @param template the template from which to generate strings
     * @return word template generator spec
     * @see #word()
     * @since 5.2.0
     */
    @ExperimentalApi
    WordTemplateGeneratorSpec wordTemplate(String template);
}
