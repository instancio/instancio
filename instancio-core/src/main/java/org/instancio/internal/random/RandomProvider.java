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

import java.util.Collection;

/**
 * Provides methods for generating random values such as numbers,
 * booleans, characters, and strings.
 */
public interface RandomProvider {

    /**
     * Returns the seed value used by the random number generator.
     *
     * @return seed value
     */
    int getSeed();

    /**
     * @return a random {@code true} or {@code false} value with a {@code 0.5} probability.
     */
    boolean trueOrFalse();

    /**
     * @param precondition required for returning {@code true} value
     * @return a random {@code true} with {@code 1/6} probability.
     */
    boolean diceRoll(boolean precondition);

    /**
     * @param min lower bound
     * @param max upper bound (exclusive)
     * @return a random byte between the min and max, exclusive
     */
    byte byteBetween(byte min, byte max);

    /**
     * @param min lower bound
     * @param max upper bound (exclusive)
     * @return a random short between the min and max, exclusive
     */
    short shortBetween(short min, short max);

    /**
     * @param min lower bound
     * @param max upper bound (exclusive)
     * @return a random int between the min and max, exclusive
     */
    int intBetween(int min, int max);

    /**
     * @param min lower bound
     * @param max upper bound (exclusive)
     * @return a random long between the min and max, exclusive
     */
    long longBetween(long min, long max);

    /**
     * @param min lower bound
     * @param max upper bound (exclusive)
     * @return a random float between the min and max, exclusive
     */
    float floatBetween(float min, float max);

    /**
     * @param min lower bound
     * @param max upper bound (exclusive)
     * @return a random double between the min and max, exclusive
     */
    double doubleBetween(double min, double max);

    /**
     * @return random uppercase character between A-Z inclusive.
     */
    char character();

    /**
     * @param length of the string to generate
     * @return random uppercase String with given length
     */
    String alphabetic(int length);

    /**
     * Returns a random element from given array.
     *
     * @param array to pick a value from
     * @param <T>   element type
     * @return random element
     */
    <T> T from(T... array);

    /**
     * Returns a random element from given collection.
     *
     * @param collection to pick a value from
     * @param <T>        element type
     * @return random element
     */
    <T> T from(Collection<T> collection);
}
