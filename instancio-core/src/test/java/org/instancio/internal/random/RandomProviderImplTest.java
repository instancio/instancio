/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;

@NonDeterministicTag
class RandomProviderImplTest {
    private static final int SAMPLE_SIZE = 50_000;
    private static final int PERCENTAGE_THRESHOLD = 20;
    private static final Pattern ALPHABETIC_PATTERN = Pattern.compile("^[A-Z]*$");

    private final RandomProviderImpl random = new RandomProviderImpl();
    private Set<Object> results;

    @BeforeEach
    void setUp() {
        results = new HashSet<>();
    }

    @Test
    void trueOrFalse() {
        int[] counts = new int[2];
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final boolean value = random.trueOrFalse();
            counts[value ? 1 : 0]++;
            results.add(value);
        }
        assertThat(results).hasSize(2);
        assertThat(counts[1])
                .as("Expecting 'true' with 1/2 probability")
                .isCloseTo(SAMPLE_SIZE / 2, withPercentage(PERCENTAGE_THRESHOLD));
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
            final double value = random.doubleBetween(Double.MIN_VALUE, Double.MAX_VALUE);
            results.add(value);
        }

        assertThat(results).hasSize(SAMPLE_SIZE);
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
        void alphabetic() {
            for (int i = 0; i < SAMPLE_SIZE; i++) {
                final int length = random.intBetween(0, 5);

                assertThat(random.alphabetic(length))
                        .hasSize(length)
                        .containsPattern(ALPHABETIC_PATTERN);
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
                final String alphabetic = random.alphabetic(length);
                assertThat(alphabetic)
                        .hasSize(length)
                        .containsPattern(ALPHABETIC_PATTERN);

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
            final int totalResults = max - min;
            final Integer[] counts = new Integer[totalResults];
            Arrays.fill(counts, 0);

            for (int i = 0; i < SAMPLE_SIZE; i++) {
                final int value = random.intBetween(min, max);
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
                final int value = random.intBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
                results.add(value);
            }

            assertThat(results).hasSizeGreaterThan(SAMPLE_SIZE * 99 / 100);
        }

        private Stream<Arguments> intRanges() {
            return Stream.of(
                    Arguments.of(Integer.MIN_VALUE, Integer.MIN_VALUE + 1),
                    Arguments.of(-10, 1),
                    Arguments.of(-10, 11),
                    Arguments.of(0, 11),
                    Arguments.of(Integer.MAX_VALUE - 1, Integer.MAX_VALUE));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GenerateLongTest {

        @MethodSource("longRanges")
        @ParameterizedTest
        void longBetween(final long min, final long max) {
            final int totalResults = (int) (max - min);
            final Integer[] counts = new Integer[totalResults];
            Arrays.fill(counts, 0);

            for (int i = 0; i < SAMPLE_SIZE; i++) {
                final long value = random.longBetween(min, max);
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

        private Stream<Arguments> longRanges() {
            return Stream.of(
                    Arguments.of(Long.MIN_VALUE, Long.MIN_VALUE + 1),
                    Arguments.of(-10, 1),
                    Arguments.of(-10, 11),
                    Arguments.of(0, 11),
                    Arguments.of(Long.MAX_VALUE - 1, Long.MAX_VALUE));
        }
    }

}
