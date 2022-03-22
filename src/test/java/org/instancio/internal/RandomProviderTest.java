package org.instancio.internal;

import org.instancio.internal.random.RandomProvider;
import org.instancio.testsupport.tags.NonDeterministicTag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;

@NonDeterministicTag
class RandomProviderTest {
    private static final Logger LOG = LoggerFactory.getLogger(RandomProviderTest.class);

    private static final int SAMPLE_SIZE = 10_000;
    private static final int PERCENTAGE_THRESHOLD = 20;
    private static final Pattern ALPHABETIC_PATTERN = Pattern.compile("^[A-Z]*$");

    private static final int SEED = new Random().nextInt();
    private final RandomProvider random = new RandomProvider(SEED);
    private Set<Object> results;

    @BeforeAll
    static void beforeAll() {
        LOG.debug("Seed: {}", SEED);
    }

    @BeforeEach
    void setUp() {
        results = new HashSet<>();
    }

    @Test
    void trueOrFalse() {
        int[] counts = new int[2];
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final boolean value = random.trueOrFalse();
            counts[value ? 0 : 1]++;
            results.add(value);
        }
        assertThat(results).hasSize(2);
        assertThat(counts[0]).isCloseTo(SAMPLE_SIZE / 2, withPercentage(PERCENTAGE_THRESHOLD));
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
    class FromArrayTest {

        @Test
        void fromArray() {
            final Integer[] array = {0, 1};
            final int[] counts = new int[array.length];

            for (int i = 0; i < SAMPLE_SIZE; i++) {
                counts[random.from(array)]++;
            }
            assertThat(counts[0]).isCloseTo(SAMPLE_SIZE / 2, withPercentage(PERCENTAGE_THRESHOLD));
        }

        @Test
        void fromSingleElementArray() {
            final String[] array = {"foo"};
            assertThat(random.from(array)).isEqualTo(array[0]);
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

            assertThat(results).hasSize(SAMPLE_SIZE);
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
