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

import org.instancio.Random;
import org.instancio.internal.util.Verify;

import java.util.Collection;

public class DefaultRandom implements Random {

    private final long seed;
    private final java.util.Random random;

    /**
     * Create an instance with a random seed value.
     */
    public DefaultRandom() {
        this(Seeds.randomSeed());
    }

    /**
     * Create an instance with the given seed value.
     *
     * @param seed for the random generator
     */
    public DefaultRandom(final long seed) {
        this.seed = seed;
        this.random = new java.util.Random(seed); // NOSONAR
    }

    @Override
    public long getSeed() {
        return seed;
    }

    @Override
    public boolean trueOrFalse() {
        return intRange(0, 1) == 1;
    }

    @Override
    public boolean diceRoll(final boolean precondition) {
        return precondition && intRange(0, 5) == 1;
    }

    @Override
    public byte byteRange(final byte min, final byte max) {
        return (byte) longRange(min, max);
    }

    @Override
    public short shortRange(final short min, final short max) {
        return (short) longRange(min, max);
    }

    @Override
    public int intRange(final int min, final int max) {
        return (int) longRange(min, max);
    }

    @Override
    public long longRange(final long min, final long max) {
        return RandomDataGenerator.nextLong(random, min, max);
    }

    @Override
    public float floatRange(final float min, final float max) {
        return (float) doubleRange(min, max);
    }

    @Override
    public double doubleRange(final double min, final double max) {
        return RandomDataGenerator.nextDouble(random, min, max);
    }

    @Override
    public char character() {
        return trueOrFalse() ? lowerCaseCharacter() : upperCaseCharacter();
    }

    @Override
    public char lowerCaseCharacter() {
        return (char) (intRange(0, 25) + 'a');
    }

    @Override
    public char upperCaseCharacter() {
        return (char) (intRange(0, 25) + 'A');
    }

    @Override
    public char alphanumericCharacter() {
        return intRange(0, 2) == 1 ? digitChar() : character();
    }

    private char digitChar() {
        return (char) (intRange(0, 9) + '0');
    }

    @Override
    public String lowerCaseAlphabetic(final int length) {
        char[] s = new char[length];
        for (int i = 0; i < length; i++) {
            s[i] = lowerCaseCharacter();
        }

        return new String(s);
    }

    @Override
    public String upperCaseAlphabetic(final int length) {
        char[] s = new char[length];
        for (int i = 0; i < length; i++) {
            s[i] = upperCaseCharacter();
        }

        return new String(s);
    }

    @Override
    public String digits(final int length) {
        char[] s = new char[length];
        for (int i = 0; i < length; i++) {
            s[i] = digitChar();
        }

        return new String(s);
    }

    @Override
    public String alphanumeric(final int length) {
        char[] s = new char[length];
        for (int i = 0; i < length; i++) {
            s[i] = alphanumericCharacter();
        }

        return new String(s);
    }

    @Override
    public String mixedCaseAlphabetic(final int length) {
        char[] s = new char[length];
        for (int i = 0; i < length; i++) {
            s[i] = character();
        }

        return new String(s);
    }

    @Override
    public <T> T oneOf(final T[] array) {
        Verify.notEmpty(array, "Array must have at least one element");
        return array[intRange(0, array.length - 1)];
    }

    @Override
    public <T> T oneOf(final Collection<T> collection) {
        Verify.notEmpty(collection, "Collection must have at least one element");
        return collection.stream()
                .skip(intRange(0, collection.size() - 1))
                .findFirst()
                .orElse(null);
    }
}
