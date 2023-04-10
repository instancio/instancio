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
package org.instancio.internal.util;

import java.util.stream.IntStream;

public final class LuhnUtils {

    private LuhnUtils() {
        // non-instantiable
    }

    public static boolean isLuhnValid(final String s) {
        return isLuhnValid(0, s.length() - 1, s.length() - 1, s);
    }

    /**
     * Check whether the given string conforms to the Luhn Modulo 10 checksum algorithm.
     *
     * <p>See: <a href="https://en.wikipedia.org/wiki/Luhn_algorithm">Luhn algorithm</a>.
     *
     * @param startIdx start index of the payload
     * @param endIdx   end index of the payload
     * @param checkIdx index of the check digit
     * @param s        to check
     * @return {@code true} if the specified range of the string passes the Luhn check
     */
    public static boolean isLuhnValid(final int startIdx, final int endIdx, int checkIdx, final String s) {
        // Note: according to Hibernate Validator, checkIdx must be greater than 0.
        // The validation here allows checkIdx to be zero, because why not???
        Verify.isTrue((checkIdx >= 0 && (checkIdx < startIdx || checkIdx >= endIdx)),
                "Invalid check digit index %s for range (%s, %s)",
                checkIdx, startIdx, endIdx);

        final int actualEnd = endIdx == checkIdx ? endIdx - 1 : endIdx;
        final String payload = s.substring(startIdx, actualEnd + 1);
        return getCheckDigit(payload) == s.charAt(checkIdx) - '0';
    }

    public static int getCheckDigit(final String payload) {
        return (10 - module10(payload)) % 10;
    }

    private static int module10(final String payload) {
        String reverse = new StringBuilder(payload).reverse().toString();
        int sum = IntStream.range(0, payload.length())
                .map(i -> extractDigit(i, reverse))
                .map(NumberUtils::sumDigits)
                .map(NumberUtils::sumDigits)
                .sum();
        return sum % 10;
    }

    private static int extractDigit(final int position, final String string) {
        int digit = string.charAt(position) - '0';
        return position % 2 == 0 ? digit << 1 : digit;
    }
}
