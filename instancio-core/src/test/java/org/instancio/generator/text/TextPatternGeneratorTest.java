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
package org.instancio.generator.text;

import org.instancio.exception.InstancioApiException;
import org.instancio.internal.random.RandomProvider;
import org.instancio.internal.random.RandomProviderImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TextPatternGeneratorTest {
    private static final String ALLOWED_HASHTAGS_MESSAGE = String.format("%nAllowed hashtags:"
            + "%n\t#c - lower case character [a-z]"
            + "%n\t#C - upper case character [A-Z]"
            + "%n\t#d - digit [0-9]"
            + "%n\t## - hash symbol escape%n");

    private static final RandomProvider random = new RandomProviderImpl();

    @ValueSource(strings = {"", " \n \t \r\n ", "abc", "123", "a1b2", "`~!@$%^&*()_-+={[}]|\\:;\"'<,>.?/"})
    @ParameterizedTest
    void literal(final String pattern) {
        assertThat(generate(pattern)).isEqualTo(pattern);
    }

    @Test
    void lowercase() {
        assertThat(generate("#c")).matches("^[a-z]$");
        assertThat(generate("#c#c")).matches("^[a-z]{2}$");
    }

    @Test
    void uppercase() {
        assertThat(generate("#C")).matches("^[A-Z]$");
        assertThat(generate("#C#C")).matches("^[A-Z]{2}$");
    }

    @Test
    void digits() {
        assertThat(generate("#d")).matches("^[0-9]$");
        assertThat(generate("#d#d")).matches("^[0-9]{2}$");
    }

    @Test
    void escapedHash() {
        assertThat(generate("##")).matches("^#$");
        assertThat(generate("####")).matches("^##$");
    }

    @Test
    void mixed() {
        assertThat(generate("#c#C#d###c#C#d")).matches("^[a-z][A-Z][0-9]#[a-z][A-Z][0-9]$");
    }

    @ValueSource(strings = {"#", "###", "#c#"})
    @ParameterizedTest
    void unterminatedHash(final String pattern) {
        assertThatThrownBy(() -> generate(pattern))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Invalid text pattern '%s'. Expected a character after the last '#'", pattern);
    }

    @Test
    void errorMessage() {
        final String pattern = "b#ad";
        assertThatThrownBy(() -> generate(pattern))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Text pattern '%s' contains an invalid hashtag '#a'", pattern)
                .hasMessageContaining(ALLOWED_HASHTAGS_MESSAGE);
    }

    private String generate(final String pattern) {
        TextPatternGenerator generator = new TextPatternGenerator(pattern);
        return generator.generate(random);
    }
}
