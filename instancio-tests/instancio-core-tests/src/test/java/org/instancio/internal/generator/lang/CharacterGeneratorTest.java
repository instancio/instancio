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
package org.instancio.internal.generator.lang;

import org.instancio.Instancio;
import org.instancio.exception.InstancioApiException;
import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.allChars;

@NonDeterministicTag
class CharacterGeneratorTest extends AbstractGeneratorTestTemplate<Character, CharacterGenerator> {
    private static final int SAMPLE_SIZE = 1000;

    private final CharacterGenerator generator = new CharacterGenerator(getGeneratorContext());

    @Override
    protected String getApiMethod() {
        return "chars()";
    }

    @Override
    protected CharacterGenerator generator() {
        return generator;
    }

    @Test
    void generate() {
        final Set<Character> results = new HashSet<>();
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            results.add(generator.generate(random));
        }

        assertThat(results)
                .as("26 letters")
                .hasSize(26)
                .allSatisfy(c -> assertThat(c).isUpperCase());
    }

    @Test
    void nullableViaGeneratorSpec() {
        final Stream<Character> results = Instancio.of(Character.class)
                .generate(allChars(), gen -> gen.chars().nullable())
                .stream()
                .limit(SAMPLE_SIZE);

        assertThat(results).containsNull();
    }

    @Test
    void range() {
        generator.range('0', '1');

        final Stream<Character> results = Stream.generate(() -> generator.generate(random))
                .limit(SAMPLE_SIZE);

        assertThat(results)
                .hasSize(SAMPLE_SIZE)
                .containsOnly('0', '1');
    }

    @Test
    void rangeValidation() {
        assertThatThrownBy(() -> generator.range('b', 'a'))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("invalid 'range(b, a)': lower bound must be less than or equal to upper bound");
    }
}
