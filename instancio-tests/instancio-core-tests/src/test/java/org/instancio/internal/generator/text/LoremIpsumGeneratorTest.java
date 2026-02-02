/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.internal.generator.text;

import org.instancio.exception.InstancioApiException;
import org.instancio.generator.specs.LoremIpsumGeneratorSpec;
import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoremIpsumGeneratorTest extends AbstractGeneratorTestTemplate<String, LoremIpsumGenerator> {

    private static final String PARAGRAPH_SEPARATOR = System.lineSeparator() + System.lineSeparator();

    private final LoremIpsumGenerator generator = new LoremIpsumGenerator(getGeneratorContext());

    @Override
    protected String getApiMethod() {
        return "loremIpsum()";
    }

    @Override
    protected LoremIpsumGenerator generator() {
        return generator;
    }

    @Test
    void defaultLoremIpsum() {
        final String result = generator.generate(random);
        assertWords(result, LoremIpsumGeneratorSpec.DEFAULT_WORDS);
        assertParagraphs(result, LoremIpsumGeneratorSpec.DEFAULT_PARAGRAPHS);
    }

    @ValueSource(ints = {1, 2, 3, 10001})
    @ParameterizedTest
    void words(final int expectedWords) {
        generator.words(expectedWords);
        final String result = generator.generate(random);
        assertWords(result, expectedWords);
        assertParagraphs(result, LoremIpsumGeneratorSpec.DEFAULT_PARAGRAPHS);
    }

    @ValueSource(ints = {1, 2, 3, 4, 5, 50})
    @ParameterizedTest
    void paragraphs(final int expectedParagraphs) {
        generator.paragraphs(expectedParagraphs);
        final String result = generator.generate(random);
        assertWords(result, LoremIpsumGeneratorSpec.DEFAULT_WORDS);
        assertParagraphs(result, expectedParagraphs);
    }

    @CsvSource({"1,1", "31,9", "50,3", "50,49", "50,50"})
    @ParameterizedTest
    void wordsAndParagraphs(final int expectedWords, final int expectedParagraphs) {
        generator.words(expectedWords).paragraphs(expectedParagraphs);

        final String result = generator.generate(random);
        assertWords(result, expectedWords);
        assertParagraphs(result, expectedParagraphs);
    }

    @Test
    void validationErrorIsThrownIfParagraphsExceedWords() {
        generator.words(5).paragraphs(6);
        assertThatThrownBy(() -> generator.generate(random))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("the number of paragraphs (6) is greater than the number of words (5)");
    }

    @Test
    void validationErrorWhenGivenNegativeInput() {
        assertThatThrownBy(() -> generator.words(0))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("number of words must be greater than zero: 0");

        assertThatThrownBy(() -> generator.paragraphs(0))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("number of paragraphs must be greater than zero: 0");
    }

    private static void assertParagraphs(final String result, final int expectedParagraphCount) {
        assertThat(result.split(PARAGRAPH_SEPARATOR)).hasSize(expectedParagraphCount);
    }

    private static void assertWords(final String result, final int expectedWordCount) {
        final String regex = String.format(" |%s%s", System.lineSeparator(), System.lineSeparator());
        assertThat(result.split(regex)).hasSize(expectedWordCount);
    }

}
