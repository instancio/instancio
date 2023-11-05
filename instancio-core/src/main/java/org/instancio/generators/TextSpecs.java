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
import org.instancio.generator.specs.CsvSpec;
import org.instancio.generator.specs.LoremIpsumSpec;
import org.instancio.generator.specs.TextPatternSpec;
import org.instancio.generator.specs.UUIDStringSpec;
import org.instancio.internal.generator.text.CsvGenerator;
import org.instancio.internal.generator.text.LoremIpsumGenerator;
import org.instancio.internal.generator.text.TextPatternGenerator;
import org.instancio.internal.generator.text.UUIDStringGenerator;

/**
 * Provides text generators.
 *
 * @since 2.6.0
 */
public final class TextSpecs {

    /**
     * Generates CSV.
     *
     * @return CSV generator
     * @since 2.12.0
     */
    @ExperimentalApi
    public CsvSpec csv() {
        return new CsvGenerator();
    }

    /**
     * Generates "Lorem ipsum" text.
     *
     * @return lorem ipsum text generator
     * @since 2.6.0
     */
    public LoremIpsumSpec loremIpsum() {
        return new LoremIpsumGenerator();
    }

    /**
     * Generates text based on the specified pattern.
     *
     * <p>For pattern documentation, see: {@link TextGenerators#pattern(String)}.
     *
     * @return text pattern generator
     * @see TextGenerators#pattern(String)
     * @since 2.6.0
     */
    public TextPatternSpec pattern(final String pattern) {
        return new TextPatternGenerator(pattern);
    }

    /**
     * Generates a {@code UUID} value as a string. By default, the generated
     * string is formatted as {@link java.util.UUID#toString()}.
     *
     * @return {@code UUID} string generator
     * @since 2.6.0
     */
    public UUIDStringSpec uuid() {
        return new UUIDStringGenerator();
    }
}
