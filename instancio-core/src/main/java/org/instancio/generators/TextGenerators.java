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

package org.instancio.generators;

import org.instancio.generator.GeneratorSpec;
import org.instancio.generator.text.TextPatternGenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains built-in text generators.
 */
public class TextGenerators {

    static Map<Class<?>, String> getApiMethods() {
        Map<Class<?>, String> map = new HashMap<>();
        map.put(TextPatternGenerator.class, "pattern()");
        return map;
    }

    /**
     * Generates a random string based on the specified pattern template.
     * The template supports the following hashtags:
     *
     * <ul>
     *   <li>{@code #c} - lower case character {@code [a-z]}</li>
     *   <li>{@code #C} - upper case character {@code [A-Z]}</li>
     *   <li>{@code #d} - digit {@code [0-9]}</li>
     *   <li>{@code ##} - hash symbol escape</li>
     * </ul>
     * <p>
     * Examples:
     * <pre>{@code
     *   "#d#d#d-#d#d#d-#d#d#d" -> "123-45-67"
     *   "Foo: #C#c#c" -> "Foo: Xyz"
     *   "###d#d#d" -> "#123"
     * }</pre>
     *
     * @param pattern to generate
     * @return string pattern generator
     */
    public GeneratorSpec<String> pattern(final String pattern) {
        return new TextPatternGenerator(pattern);
    }

}
