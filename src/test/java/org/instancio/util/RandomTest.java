package org.instancio.util;

import org.instancio.testsupport.tags.NonDeterministicTag;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;

@NonDeterministicTag
class RandomTest {

    private static final int SAMPLE_SIZE = 10_000;
    private static final int PERCENTAGE_THRESHOLD = 20;
    private static final Pattern ALPHABETIC_PATTERN = Pattern.compile("^[A-Z]*$");

    @Test
    void trueOrFalse() {
        int[] counts = new int[2];
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            counts[Random.trueOrFalse() ? 0 : 1]++;
        }

        assertThat(counts[0]).isCloseTo(SAMPLE_SIZE / 2, withPercentage(PERCENTAGE_THRESHOLD));
    }

    @Test
    void alphabetic() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final int length = Random.intBetween(0, 5);

            assertThat(Random.alphabetic(length))
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
            final String alphabetic = Random.alphabetic(length);
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

    @Test
    void fromArray() {
        final Integer[] array = {0, 1};
        final int[] counts = new int[array.length];

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            counts[Random.from(array)]++;
        }
        assertThat(counts[0]).isCloseTo(SAMPLE_SIZE / 2, withPercentage(PERCENTAGE_THRESHOLD));
    }

    @Test
    void intBetween() {
        final int[] counts = new int[2];

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            counts[Random.intBetween(0, counts.length)]++;
        }
        assertThat(counts[0]).isCloseTo(SAMPLE_SIZE / 2, withPercentage(PERCENTAGE_THRESHOLD));
    }

    @Test
    void intBetweenZeroAndTenInclusive() {
        final int[] counts = new int[11];

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            counts[Random.intBetween(0, counts.length)]++;
        }
        for (int count : counts) {
            assertThat(count)
                    .isCloseTo(SAMPLE_SIZE / (counts.length - 1), withPercentage(PERCENTAGE_THRESHOLD));
        }
    }

    @Test
    void intBetweenMaxValue() {
        int maxValue = Integer.MIN_VALUE;
        for (int i = 0; i < SAMPLE_SIZE && maxValue != Integer.MAX_VALUE; i++) {
            maxValue = Math.max(maxValue, Random.intBetween(Integer.MAX_VALUE - 1, Integer.MAX_VALUE));
        }
        assertThat(maxValue)
                .as("Range excludes upper bound")
                .isEqualTo(Integer.MAX_VALUE - 1);
    }
}
