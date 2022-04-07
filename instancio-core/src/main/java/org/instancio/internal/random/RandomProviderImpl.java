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

public class RandomProviderImpl implements RandomProvider {

    private final int seed;
    private final Random random;

    /**
     * Create an instance with a random seed value.
     */
    public RandomProviderImpl() {
        this(SeedUtil.randomSeed());
    }

    /**
     * Create an instance with the given seed value.
     *
     * @param seed for the random generator
     */
    public RandomProviderImpl(final int seed) {
        this.seed = seed;
        this.random = new Random(seed);
    }

    @Override
    public int getSeed() {
        return seed;
    }

    @Override
    public boolean trueOrFalse() {
        return intBetween(0, 2) == 1;
    }

    @Override
    public boolean diceRoll(final boolean precondition) {
        return precondition && intBetween(0, 6) == 1;
    }

    @Override
    public byte byteBetween(final byte min, final byte max) {
        return (byte) longBetween(min, max);
    }

    @Override
    public short shortBetween(final short min, final short max) {
        return (short) longBetween(min, max);
    }

    @Override
    public int intBetween(final int min, final int max) {
        Verify.isTrue(min < max, "Min must be less than max");
        return (int) longBetween(min, max);
    }

    @Override
    public long longBetween(final long min, final long max) {
        Verify.isTrue(min < max, "Min must be less than max");
        return RandomDataGenerator.nextLong(random, min, max);
    }

    @Override
    public float floatBetween(final float min, final float max) {
        return (float) doubleBetween(min, max);
    }

    @Override
    public double doubleBetween(final double min, final double max) {
        return RandomDataGenerator.nextDouble(random, min, max);
    }

    @Override
    public char character() {
        return (char) (intBetween(0, 26) + 'A');
    }

    @Override
    public String alphabetic(final int length) {
        char[] s = new char[length];
        for (int i = 0; i < length; i++) {
            s[i] = character();
        }

        return new String(s);
    }

    @Override
    public <T> T from(final T[] array) {
        Verify.notEmpty(array, "Array must have at least one element");
        return array[intBetween(0, array.length)];
    }

    @Override
    public <T> T from(final Collection<T> collection) {
        Verify.notEmpty(collection, "Collection must have at least one element");
        return collection.stream()
                .skip(intBetween(0, collection.size()))
                .findFirst()
                .orElse(null);
    }
}
