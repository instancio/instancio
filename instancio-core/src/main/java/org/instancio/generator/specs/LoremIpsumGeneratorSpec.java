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
package org.instancio.generator.specs;

import org.instancio.generator.GeneratorSpec;

/**
 * Generator spec for producing "Lorem ipsum..." text.
 *
 * @since 1.5.3
 */
public interface LoremIpsumGeneratorSpec extends GeneratorSpec<String> {

    /**
     * Default number of words.
     */
    int DEFAULT_WORDS = 50;

    /**
     * Default number of paragraphs.
     */
    int DEFAULT_PARAGRAPHS = 1;

    /**
     * Number of words to generate. Default is {@link #DEFAULT_WORDS}.
     *
     * @return spec builder
     * @since 1.5.3
     */
    LoremIpsumGeneratorSpec words(int words);

    /**
     * Number of paragraphs to generate. Default is {@link #DEFAULT_PARAGRAPHS}.
     *
     * @return spec builder
     * @since 1.5.3
     */
    LoremIpsumGeneratorSpec paragraphs(int paragraphs);

}
