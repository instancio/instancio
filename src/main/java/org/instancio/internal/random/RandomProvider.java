package org.instancio.internal.random;

import org.instancio.util.Verify;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomProvider {

    private final Random random;

    /**
     * Create an instance with a seed value.
     *
     * @param seed for the random generator
     */
    public RandomProvider(int seed) {
        random = new Random(seed);
    }

    /**
     * Create an instance with a random seed value.
     */
    public RandomProvider() {
        this(ThreadLocalRandom.current().nextInt());
    }

    /**
     * @return a random {@code true} or {@code false} value.
     */
    public boolean trueOrFalse() {
        return intBetween(0, 2) == 1;
    }

    /**
     * @param min lower bound
     * @param max upper bound (exclusive)
     * @return a random byte between the min and max, exclusive
     */
    public byte byteBetween(final byte min, final byte max) {
        return (byte) longBetween(min, max);
    }

    /**
     * @param min lower bound
     * @param max upper bound (exclusive)
     * @return a random short between the min and max, exclusive
     */
    public short shortBetween(final short min, final short max) {
        return (short) longBetween(min, max);
    }

    /**
     * @param min lower bound
     * @param max upper bound (exclusive)
     * @return a random int between the min and max, exclusive
     */
    public int intBetween(final int min, final int max) {
        Verify.isTrue(min < max, "Min must be less than max");
        return (int) longBetween(min, max);
    }

    /**
     * @param min lower bound
     * @param max upper bound (exclusive)
     * @return a random long between the min and max, exclusive
     */
    public long longBetween(final long min, final long max) {
        Verify.isTrue(min < max, "Min must be less than max");
        return RandomDataGenerator.nextLong(random, min, max);
    }

    /**
     * @param min lower bound
     * @param max upper bound (exclusive)
     * @return a random float between the min and max, exclusive
     */
    public float floatBetween(final float min, final float max) {
        return (float) doubleBetween(min, max);
    }


    /**
     * @param min lower bound
     * @param max upper bound (exclusive)
     * @return a random double between the min and max, exclusive
     */
    public double doubleBetween(final double min, final double max) {
        return RandomDataGenerator.nextDouble(random, min, max);
    }

    public int positiveInt() {
        return intBetween(0, Integer.MAX_VALUE);
    }

    public long positiveLong() {
        return longBetween(0, Long.MAX_VALUE);
    }

    /**
     * @return random uppercase character between A-Z inclusive.
     */
    public char character() {
        return (char) (intBetween(0, 26) + 'A');
    }

    /**
     * @param length of the string to generate
     * @return random uppercase String with given length
     */
    public String alphabetic(final int length) {
        char[] s = new char[length];
        for (int i = 0; i < length; i++) {
            s[i] = character();
        }

        return new String(s);
    }

    public <T> T from(final T[] array) {
        Verify.isTrue(array.length > 0, "Array is empty");
        return array[intBetween(0, array.length)];
    }

}
