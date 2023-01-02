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
package org.instancio;

import java.util.Collection;

/**
 * Provides methods for generating random values such as numbers,
 * booleans, characters, and strings.
 */
public interface Random {

    /**
     * Returns the seed value used by the random number generator.
     *
     * @return seed value
     */
    long getSeed();

    /**
     * Returns a random boolean with a {@code 0.5} probability.
     *
     * @return a random {@code true} or {@code false}
     */
    boolean trueOrFalse();

    /**
     * Returns {@code true} with a {@code 1/6} probability, but only
     * if the {@code precondition} is true.
     *
     * @param precondition required for returning {@code true} value
     * @return a random {@code true} with {@code 1/6} probability.
     */
    boolean diceRoll(boolean precondition);

    /**
     * Returns a random {@code byte} within the given range.
     *
     * @param min lower bound
     * @param max upper bound (inclusive)
     * @return a random byte between the min and max, inclusive
     */
    byte byteRange(byte min, byte max);

    /**
     * Returns a random {@code short} within the given range.
     *
     * @param min lower bound
     * @param max upper bound (inclusive)
     * @return a random short between the min and max, inclusive
     */
    short shortRange(short min, short max);

    /**
     * Returns a random {@code int} within the given range.
     *
     * @param min lower bound
     * @param max upper bound (inclusive)
     * @return a random int between the min and max, inclusive
     */
    int intRange(int min, int max);

    /**
     * Returns a random {@code long} within the given range.
     *
     * @param min lower bound
     * @param max upper bound (inclusive)
     * @return a random long between the min and max, inclusive
     */
    long longRange(long min, long max);

    /**
     * Returns a random {@code float} within the given range.
     *
     * @param min lower bound
     * @param max upper bound (inclusive)
     * @return a random float between the min and max, inclusive
     */
    float floatRange(float min, float max);

    /**
     * Returns a random {@code double} within the given range.
     *
     * @param min lower bound
     * @param max upper bound (inclusive)
     * @return a random double between the min and max, inclusive
     */
    double doubleRange(double min, double max);

    /**
     * Returns a random alphabetic character, {@code [a-z, A-Z]}.
     *
     * @return random character, either lower or upper case
     */
    char character();

    /**
     * Returns a random alphanumeric character, {@code [a-z, A-Z, 0-9]}.
     *
     * @return an alphanumeric character
     */
    char alphanumericCharacter();

    /**
     * Returns a random lower alphabetic character, {@code [a-z]}.
     *
     * @return random lower character
     */
    char lowerCaseCharacter();

    /**
     * Returns a random upper alphabetic character, {@code [A-Z]}.
     *
     * @return random uppercase character
     */
    char upperCaseCharacter();

    /**
     * Generates a lower case String comprised of {@code [a-z]}.
     *
     * @param length of a string to generate
     * @return random lower case String with given length
     */
    String lowerCaseAlphabetic(int length);

    /**
     * Generates a random upper case String comprised of {@code [A-Z]}.
     *
     * @param length of a string to generate
     * @return random upper case String with given length
     */
    String upperCaseAlphabetic(int length);

    /**
     * Generates a random mixed case String comprised of {@code [a-z, A-Z]}.
     *
     * @param length of a string to generate
     * @return random mixed case String with given length
     */
    String mixedCaseAlphabetic(int length);

    /**
     * Generates a random alphanumeric String comprised of {@code [a-z, A-Z, 0-9]}.
     *
     * @param length of a string to generate
     * @return random alphanumeric String with given length
     */
    String alphanumeric(int length);

    /**
     * Generates a random String comprised of digits {@code [0-9]}.
     *
     * @param length of a string to generate
     * @return random String comprised of digits with given length
     */
    String digits(int length);

    /**
     * Returns a random element from the given array.
     *
     * @param array to pick a value from
     * @param <T>   element type
     * @return random element
     */
    <T> T oneOf(T... array);

    /**
     * Returns a random element from the given collection.
     *
     * @param collection to pick a value from
     * @param <T>        element type
     * @return random element
     */
    <T> T oneOf(Collection<T> collection);
}
