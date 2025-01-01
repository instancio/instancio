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
package org.instancio.test.features.generator.enumset;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
class EnumSetGeneratorTest {

    private static final int SAMPLE_SIZE = 500;

    private enum Letter {
        A, B, C, D, E, F, G
    }

    private static final TypeToken<EnumSet<Letter>> ENUM_SET_LETTER = new TypeToken<EnumSet<Letter>>() {};

    @Test
    void enumSetWithoutGeneratorSpec() {
        assertThat(Instancio.create(ENUM_SET_LETTER)).isNotEmpty();
    }

    @Test
    void nullableEnumSet() {
        final Stream<EnumSet<Letter>> results = Instancio.of(ENUM_SET_LETTER)
                .withNullable(all(EnumSet.class))
                .stream()
                .limit(SAMPLE_SIZE);

        assertThat(results).containsNull();
    }

    @Test
    void enumSetSpecWithoutParams() {
        final Set<EnumSet<Gender>> result = Instancio.of(new TypeToken<EnumSet<Gender>>() {})
                .generate(all(EnumSet.class), gen -> gen.enumSet(Gender.class))
                .stream()
                .limit(SAMPLE_SIZE)
                .collect(Collectors.toSet());

        assertThat(result)
                .as("Should generate all combinations of Gender")
                .hasSize(7);
    }

    @Test
    void of() {
        final Set<EnumSet<Letter>> result = Instancio.of(ENUM_SET_LETTER)
                .generate(all(EnumSet.class), gen -> gen.enumSet(Letter.class).of(Letter.A, Letter.B))
                .stream()
                .limit(SAMPLE_SIZE)
                .collect(Collectors.toSet());

        assertThat(result)
                .as("Expected EnumSets: [A], [B], and [A,B]")
                .hasSize(3)
                .allSatisfy(enumSet -> {
                    assertThat(enumSet)
                            .isNotEmpty()
                            .doesNotContain(Letter.D, Letter.E, Letter.F, Letter.G);
                });
    }

    @Test
    void excluding() {
        final EnumSet<Letter> result = Instancio.of(ENUM_SET_LETTER)
                .generate(all(EnumSet.class), gen -> gen.enumSet(Letter.class).excluding(Letter.A, Letter.B, Letter.C))
                .create();

        assertThat(result)
                .doesNotContain(Letter.A, Letter.B, Letter.C)
                .contains(Letter.D, Letter.E, Letter.F, Letter.G);
    }

    @Nested
    class EnumSetSizeTest {

        @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
        @ParameterizedTest
        void size(final int size) {
            final EnumSet<Letter> result = Instancio.of(ENUM_SET_LETTER)
                    .generate(all(EnumSet.class), gen -> gen.enumSet(Letter.class).size(size))
                    .create();

            assertThat(result).hasSize(size);
        }

        @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
        @ParameterizedTest
        void minSize(final int size) {
            final EnumSet<Letter> result = Instancio.of(ENUM_SET_LETTER)
                    .generate(all(EnumSet.class), gen -> gen.enumSet(Letter.class).minSize(size))
                    .create();

            assertThat(result).hasSizeGreaterThanOrEqualTo(size);
        }

        @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
        @ParameterizedTest
        void maxSize(final int size) {
            final EnumSet<Letter> result = Instancio.of(ENUM_SET_LETTER)
                    .generate(all(EnumSet.class), gen -> gen.enumSet(Letter.class).maxSize(size))
                    .create();

            assertThat(result).hasSizeLessThanOrEqualTo(size);
        }

        @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
        @ParameterizedTest
        void sameMinAndMax(final int size) {
            final EnumSet<Letter> result = Instancio.of(ENUM_SET_LETTER)
                    .generate(all(EnumSet.class), gen -> gen.enumSet(Letter.class).minSize(size).maxSize(size))
                    .create();

            assertThat(result).hasSize(size);
        }

        @Test
        void ofWithMaxSize() {
            final Set<EnumSet<Letter>> result = Instancio.of(ENUM_SET_LETTER)
                    .generate(all(EnumSet.class), gen -> gen.enumSet(Letter.class).of(Letter.A, Letter.B, Letter.C).maxSize(2))
                    .stream()
                    .limit(SAMPLE_SIZE)
                    .collect(Collectors.toSet());

            assertThat(result)
                    .as("Expected EnumSets: [A], [B], [C], [A,B], [A,C], and [B,C]")
                    .hasSize(6)
                    .allSatisfy(enumSet -> {
                        assertThat(enumSet)
                                .isNotEmpty()
                                .doesNotContain(Letter.D, Letter.E, Letter.F, Letter.G);
                    });
        }

        @Test
        void requestedSizeIsGreaterThanTheNumberOfEnumValues() {
            final int numOfEnumValues = Letter.values().length;
            final EnumSet<Letter> result = Instancio.of(ENUM_SET_LETTER)
                    .generate(all(EnumSet.class), gen -> gen.enumSet(Letter.class).minSize(numOfEnumValues * 2))
                    .create();

            assertThat(result).hasSize(numOfEnumValues);
        }
    }
}
