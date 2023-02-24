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
package org.instancio.internal.random;

import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Percentage.withPercentage;

@NonDeterministicTag
class DefaultRandomTest {
    private static final int SAMPLE_SIZE = 50_000;
    private static final int PERCENTAGE_THRESHOLD = 15;
    private static final Pattern UPPER_CASE_ALPHABETIC_PATTERN = Pattern.compile("^[A-Z]*$");
    private static final Pattern LOWER_CASE_ALPHABETIC_PATTERN = Pattern.compile("^[a-z]*$");
    private static final Pattern MIXED_CASE_ALPHABETIC_PATTERN = Pattern.compile("^[a-zA-Z]*$");
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9]*$");
    private static final Pattern DIGITS_PATTERN = Pattern.compile("^[0-9]*$");

    private final DefaultRandom random = new DefaultRandom();
    private Set<Object> results;

    @BeforeEach
    void setUp() {
        results = new HashSet<>();
    }

    @Test
    void bounds() {
        assertThat(random.byteRange((byte) 1, (byte) 1)).isEqualTo((byte) 1);
        assertThat(random.shortRange((short) 1, (short) 1)).isEqualTo((short) 1);
        assertThat(random.intRange(1, 1)).isEqualTo(1);
        assertThat(random.longRange(1, 1)).isEqualTo(1);
        assertThat(random.floatRange(1, 1)).isEqualTo(1);
        assertThat(random.doubleRange(1, 1)).isEqualTo(1);
        assertThat(random.characterRange('1', '1')).isEqualTo('1');
        assertThat(String.valueOf(random.digits(1))).hasSize(1);
        assertThat(random.lowerCaseAlphabetic(1)).hasSize(1);
        assertThat(random.upperCaseAlphabetic(1)).hasSize(1);
        assertThat(random.mixedCaseAlphabetic(1)).hasSize(1);
        assertThat(random.alphanumeric(1)).hasSize(1);
    }

    @Test
    void diceRoll() {
        final boolean preCondition = true;
        int[] counts = new int[2];
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final boolean value = random.diceRoll(preCondition);
            counts[value ? 1 : 0]++;
            results.add(value);
        }
        assertThat(results).hasSize(2);
        assertThat(counts[1])
                .as("Expecting 'true' with 1/6 probability")
                .isCloseTo(SAMPLE_SIZE / 6, withPercentage(PERCENTAGE_THRESHOLD));
    }

    @Test
    void doubleBetweenMinAndMax() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final double value = random.doubleRange(Double.MIN_VALUE, Double.MAX_VALUE);
            results.add(value);
        }

        assertThat(results).hasSize(SAMPLE_SIZE);
    }

    @Nested
    class TrueOrFalseTest {
        @Test
        void trueOrFalse() {
            assertFiftyFiftyProbability(random::trueOrFalse);
        }

        @Test
        void trueOrFalseWithProbability0_5() {
            assertFiftyFiftyProbability(() -> random.trueOrFalse(0.5d));
        }

        private void assertFiftyFiftyProbability(final Supplier<Boolean> supplier) {
            int[] counts = new int[2];
            for (int i = 0; i < SAMPLE_SIZE; i++) {
                final boolean value = supplier.get();
                counts[value ? 1 : 0]++;
                results.add(value);
            }
            assertThat(results).hasSize(2);
            assertThat(counts[1])
                    .as("Expecting 'true' with 1/2 probability")
                    .isCloseTo(SAMPLE_SIZE / 2, withPercentage(PERCENTAGE_THRESHOLD));
        }

        @Test
        void trueOrFalseWithProbability1() {
            for (int i = 0; i < SAMPLE_SIZE; i++) {
                assertThat(random.trueOrFalse(1d)).isTrue();
                assertThat(random.trueOrFalse(0d)).isFalse();
            }
        }
    }

    @Nested
    class OneOfArrayTest {

        @Test
        void oneOfArray() {
            final Integer[] array = {0, 1, 2};
            final int[] counts = new int[array.length];

            for (int i = 0; i < SAMPLE_SIZE; i++) {
                final Integer value = random.oneOf(array);
                counts[value]++;
                results.add(value);
            }

            assertThat(results).hasSameSizeAs(array);
            assertThat(counts[0]).isCloseTo(SAMPLE_SIZE / array.length, withPercentage(PERCENTAGE_THRESHOLD));
        }

        @Test
        void oneOfSingleElementArray() {
            final String[] array = {"foo"};
            assertThat(random.oneOf(array)).isEqualTo(array[0]);
        }
    }

    @Nested
    class OneOfCollectionTest {

        @Test
        void oneOfCollection() {
            final List<Integer> list = Arrays.asList(0, 1, 2);
            final int[] counts = new int[list.size()];

            for (int i = 0; i < SAMPLE_SIZE; i++) {
                final Integer value = random.oneOf(list);
                counts[value]++;
                results.add(value);
            }

            assertThat(results).hasSameSizeAs(list);
            assertThat(counts[0]).isCloseTo(SAMPLE_SIZE / list.size(), withPercentage(PERCENTAGE_THRESHOLD));
        }

        @Test
        void oneOfSingleElementCollection() {
            final Set<String> set = Collections.singleton("foo");
            assertThat(random.oneOf(set)).isEqualTo(set.iterator().next());
        }
    }

    @Nested
    class AlphabeticTest {

        @Test
        void lowerCaseAlphabetic() {
            for (int i = 0; i < SAMPLE_SIZE; i++) {
                final int length = random.intRange(0, 5);

                assertThat(random.lowerCaseAlphabetic(length))
                        .hasSize(length)
                        .containsPattern(LOWER_CASE_ALPHABETIC_PATTERN);
            }
        }

        @Test
        void upperCaseAlphabetic() {
            for (int i = 0; i < SAMPLE_SIZE; i++) {
                final int length = random.intRange(0, 5);

                assertThat(random.upperCaseAlphabetic(length))
                        .hasSize(length)
                        .containsPattern(UPPER_CASE_ALPHABETIC_PATTERN);
            }
        }

        @Test
        void mixedCaseAlphabetic() {
            for (int i = 0; i < SAMPLE_SIZE; i++) {
                final int length = random.intRange(0, 5);

                assertThat(random.mixedCaseAlphabetic(length))
                        .hasSize(length)
                        .containsPattern(MIXED_CASE_ALPHABETIC_PATTERN);
            }
        }

        @Test
        void alphaNumeric() {
            for (int i = 0; i < SAMPLE_SIZE; i++) {
                final int length = random.intRange(0, 5);

                assertThat(random.alphanumeric(length))
                        .hasSize(length)
                        .containsPattern(ALPHANUMERIC_PATTERN);
            }
        }

        @Test
        void digits() {
            for (int i = 0; i < SAMPLE_SIZE; i++) {
                final int length = random.intRange(0, 5);

                assertThat(random.digits(length))
                        .hasSize(length)
                        .containsPattern(DIGITS_PATTERN);
            }
        }

        /**
         * Generate {@code #SAMPLE_SIZE} strings (A-Z) with length 26
         * and verify each letter shows up expected number of times within
         * the {@code #PERCENTAGE_THRESHOLD}.
         */
        @Test
        void alphabeticWithAllCharactersIncluded() {
            final int length = 26;
            int[] counts = new int[26];
            for (int i = 0; i < SAMPLE_SIZE; i++) {
                final String alphabetic = random.upperCaseAlphabetic(length);
                assertThat(alphabetic)
                        .hasSize(length)
                        .containsPattern(UPPER_CASE_ALPHABETIC_PATTERN);

                for (char c : alphabetic.toCharArray())
                    counts[c - 'A']++;
            }

            for (int count : counts) {
                assertThat(count).isCloseTo(SAMPLE_SIZE, withPercentage(PERCENTAGE_THRESHOLD));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GenerateIntTest {

        @MethodSource("intRanges")
        @ParameterizedTest
        void intBetween(final int min, final int max) {
            final int totalResults = max - min + 1;
            final Integer[] counts = new Integer[totalResults];
            Arrays.fill(counts, 0);

            for (int i = 0; i < SAMPLE_SIZE; i++) {
                final int value = random.intRange(min, max);
                final int index = value + -min;
                counts[index]++;
                results.add(value);
            }

            assertThat(results).hasSize(totalResults);

            final int expectedAvgFrequency = SAMPLE_SIZE / totalResults;
            assertThat(counts).allSatisfy(count ->
                    assertThat(count).isCloseTo(expectedAvgFrequency, withPercentage(PERCENTAGE_THRESHOLD))
            );
        }

        @Test
        void intBetweenMinAndMax() {
            for (int i = 0; i < SAMPLE_SIZE; i++) {
                final int value = random.intRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
                results.add(value);
            }

            assertThat(results).hasSizeGreaterThan(SAMPLE_SIZE * 99 / 100);
        }

        @Test
        void maxValue() {
            assertThat(random.intRange(Integer.MAX_VALUE, Integer.MAX_VALUE)).isEqualTo(Integer.MAX_VALUE);
        }

        private Stream<Arguments> intRanges() {
            return Stream.of(
                    Arguments.of(Integer.MIN_VALUE, Integer.MIN_VALUE + 1),
                    Arguments.of(-10, 1),
                    Arguments.of(-10, 11),
                    Arguments.of(0, 11),
                    Arguments.of(Integer.MAX_VALUE - 1, Integer.MAX_VALUE),
                    Arguments.of(Integer.MAX_VALUE, Integer.MAX_VALUE));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GenerateLongTest {

        @MethodSource("longRanges")
        @ParameterizedTest
        void longBetween(final long min, final long max) {
            final int totalResults = (int) (max - min) + 1;
            final Integer[] counts = new Integer[totalResults];
            Arrays.fill(counts, 0);

            for (int i = 0; i < SAMPLE_SIZE; i++) {
                final long value = random.longRange(min, max);
                final int index = (int) (value + -min);
                counts[index]++;
                results.add(value);
            }

            assertThat(results).hasSize(totalResults);

            final int expectedAvgFrequency = SAMPLE_SIZE / totalResults;
            assertThat(counts).allSatisfy(count ->
                    assertThat(count).isCloseTo(expectedAvgFrequency, withPercentage(PERCENTAGE_THRESHOLD))
            );
        }

        @Test
        void maxValue() {
            assertThat(random.longRange(Long.MAX_VALUE, Long.MAX_VALUE)).isEqualTo(Long.MAX_VALUE);
        }

        private Stream<Arguments> longRanges() {
            return Stream.of(
                    Arguments.of(Long.MIN_VALUE, Long.MIN_VALUE + 1),
                    Arguments.of(-10, 1),
                    Arguments.of(-10, 11),
                    Arguments.of(0, 11),
                    Arguments.of(Long.MAX_VALUE - 1, Long.MAX_VALUE),
                    Arguments.of(Long.MAX_VALUE, Long.MAX_VALUE));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GenerateCharacterTest {

        @MethodSource("characterRanges")
        @ParameterizedTest
        void characterBetween(final char min, final char max) {
            final int totalResults = (max - min) + 1;
            final Integer[] counts = new Integer[totalResults];
            Arrays.fill(counts, 0);

            for (int i = 0; i < SAMPLE_SIZE; i++) {
                final char value = random.characterRange(min, max);
                final int index = value + -min;
                counts[index]++;
                results.add(value);
            }

            assertThat(results).hasSize(totalResults);

            final int expectedAvgFrequency = SAMPLE_SIZE / totalResults;
            assertThat(counts).allSatisfy(count ->
                    assertThat(count).isCloseTo(expectedAvgFrequency, withPercentage(PERCENTAGE_THRESHOLD))
            );
        }

        @Test
        void maxValue() {
            assertThat(random.characterRange(Character.MAX_VALUE, Character.MAX_VALUE)).isEqualTo(Character.MAX_VALUE);
        }

        private Stream<Arguments> characterRanges() {
            return Stream.of(
                    Arguments.of(Character.MIN_VALUE, (char) (Character.MIN_VALUE + 1)),
                    Arguments.of('a', 'c'),
                    Arguments.of('M', 'N'),
                    Arguments.of('t', 'y'),
                    Arguments.of((char) (Character.MAX_VALUE - 1), Character.MAX_VALUE),
                    Arguments.of(Character.MAX_VALUE, Character.MAX_VALUE));
        }
    }

    @Nested
    class StringOfTest {

        @Test
        void stringOf() {
            assertThat(random.stringOf(0, 'a')).isEmpty();
            assertThat(random.stringOf(1, 'a')).isEqualTo("a");
            assertThat(random.stringOf(3, 'a')).isEqualTo("aaa");
            assertThat(random.stringOf(500, 'a', 'b', 'c').toCharArray()).contains('a', 'b', 'c');
        }

        @Test
        void negativeLength() {
            assertThatThrownBy(() -> random.stringOf(-1, 'a'))
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Length must not be negative");
        }

        @Test
        void nullOrEmptyArray() {
            final String expectedErrorMsg = "Character array must have at least one element";

            final char[] nullArray = null;
            assertThatThrownBy(() -> random.stringOf(1, nullArray))
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage(expectedErrorMsg);

            assertThatThrownBy(() -> random.stringOf(1, new char[0]))
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage(expectedErrorMsg);
        }
    }
}
