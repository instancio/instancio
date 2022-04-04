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
package org.instancio.internal.random;

import org.instancio.util.SeedUtil;
import org.instancio.util.Verify;

import java.util.Collection;
import java.util.Random;

public class RandomProvider {

    private final int seed;
    private final Random random;

    /**
     * Create an instance with a random seed value.
     */
    public RandomProvider() {
        this(SeedUtil.randomSeed());
    }

    /**
     * Create an instance with the given seed value.
     *
     * @param seed for the random generator
     */
    public RandomProvider(final int seed) {
        this.seed = seed;
        this.random = new Random(seed);
    }

    public int getSeed() {
        return seed;
    }

    /**
     * @return a random {@code true} or {@code false} value with a {@code 0.5} probability.
     */
    public boolean trueOrFalse() {
        return intBetween(0, 2) == 1;
    }

    /**
     * @param precondition required for returning {@code true} value
     * @return a random {@code true} with {@code 1/6} probability.
     */
    public boolean diceRoll(final boolean precondition) {
        return precondition && intBetween(0, 6) == 1;
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
        Verify.notEmpty(array, "Array must have at least one element");
        return array[intBetween(0, array.length)];
    }

    public <T> T from(final Collection<T> collection) {
        Verify.notEmpty(collection, "Collection must have at least one element");
        return collection.stream()
                .skip(intBetween(0, collection.size()))
                .findFirst()
                .orElse(null);
    }
}
