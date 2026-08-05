/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.internal.generator.checksum;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.util.NumberUtils;

public abstract class BaseModCheckGenerator extends AbstractGenerator<String> {

    protected BaseModCheckGenerator(final GeneratorContext context) {
        super(context);
    }

    /**
     * The shape of the number to be generated: the length of each segment
     * and the position of the check digit within the assembled number.
     *
     * <p>A layout is computed once per generated value and passed down
     * to the code that assembles the number. Generating a value therefore
     * requires no generator state and modifies none, which makes generators
     * safe to reuse for more than one value.
     *
     * @param prefixLength  number of random digits preceding the payload
     * @param payloadLength number of digits the check digit is calculated from
     * @param suffixLength  number of random digits following the payload
     * @param checkPosition index at which the check digit is placed
     */
    public record Layout(int prefixLength, int payloadLength, int suffixLength, int checkPosition) {

        /**
         * Layout of a fixed-length number that consists of a payload
         * followed by the check digit.
         */
        public static Layout of(final int payloadLength) {
            return new Layout(0, payloadLength, 1, payloadLength);
        }
    }

    /**
     * Returns the layout of the value about to be generated.
     */
    protected abstract Layout layout(Random random);

    @Override
    protected String tryGenerateNonNull(final Random random) {
        final Layout layout = layout(random);
        final String prefix = random.digits(layout.prefixLength());
        final String payload = payload(random, layout.payloadLength());
        final char checkDigit = getCheckDigit(payload);
        final String suffix = random.digits(layout.suffixLength());
        final StringBuilder result = new StringBuilder(prefix).append(payload).append(suffix);
        result.setCharAt(layout.checkPosition(), checkDigit);
        return result.toString();
    }

    protected String payload(final Random random, final int length) {
        final char[] res = new char[length];

        // Avoid generating numbers that start with zero to prevent the loss of
        // the leading digit if the generated string is converted to an int/long
        res[0] = random.characterRange('1', '9');
        for (int i = 1; i < length; i++) {
            res[i] = random.characterRange('0', '9');
        }
        return new String(res);
    }

    protected char getCheckDigit(final String payload) {
        final int checkValue = base() - modulo(payload);
        return checkValue < 10 ? (char) (checkValue + '0') : nonDigitCheckValue(checkValue);
    }

    /**
     * Maps a check value that does not fit into a single digit.
     *
     * <p>Since {@link #modulo(String)} returns a value in {@code [0, base() - 1]},
     * the check value is in {@code [1, base()]}. Therefore, the argument can only
     * be {@code 10} for a base-10 generator, and {@code 10} or {@code 11}
     * for a base-11 generator.
     */
    protected char nonDigitCheckValue(final int checkValue) {
        return '0';
    }

    protected int modulo(final String payload) {
        final String newPayload = direction() == Direction.RIGHT_TO_LEFT
                ? new StringBuilder(payload).reverse().toString()
                : payload;

        int sum = 0;
        int bound = payload.length();
        for (int i = 0; i < bound; i++) {
            int n = extractDigit(i, newPayload);
            if (sumDigits()) {
                n = NumberUtils.sumDigits(n);
                n = NumberUtils.sumDigits(n);
            }
            sum += n;
        }
        return sum % base();
    }

    private int extractDigit(final int position, final String string) {
        int digit = string.charAt(position) - '0';
        return position % 2 == 0 ? digit * even(position) : digit * odd(position);
    }

    protected int even(final int position) {
        return 2;
    }

    protected int odd(final int position) {
        return 1;
    }

    protected boolean sumDigits() {
        return true;
    }

    protected Direction direction() {
        return Direction.RIGHT_TO_LEFT;
    }

    protected int base() {
        return 10;
    }

    enum Direction {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT
    }
}