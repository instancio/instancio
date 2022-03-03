package org.instancio.util;

import java.util.concurrent.ThreadLocalRandom;

public final class Random {

    private Random() {
        // non-instantiable
    }

    public static <T> T from(final T[] array) {
        Verify.isTrue(array.length > 0, "Array is empty");
        return array[intBetween(0, array.length)];
    }

    public static boolean trueOrFalse() {
        return intBetween(0, 2) == 1;
    }

    /**
     * @param min lower bound
     * @param max upper bound (exclusive)
     * @return a random int between the min and max, exclusive
     */
    public static int intBetween(final int min, final int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    /**
     * @param min lower bound
     * @param max upper bound (exclusive)
     * @return a random long between the min and max, exclusive
     */
    public static long longBetween(final long min, final long max) {
        return ThreadLocalRandom.current().nextLong(min, max);
    }

    public static int positiveInt() {
        return intBetween(0, Integer.MAX_VALUE);
    }

    public static long positiveLong() {
        return longBetween(0, Long.MAX_VALUE);
    }

    public static String alphabetic(final int length) {
        char[] s = new char[length];
        for (int i = 0; i < length; i++) {
            s[i] = (char) (intBetween(0, 26) + 'A');
        }

        return new String(s);
    }
}
