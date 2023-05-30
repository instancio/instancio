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
package org.instancio.internal.generator.checksum;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.util.NumberUtils;

public abstract class BaseModCheckGenerator extends AbstractGenerator<String> {

    protected BaseModCheckGenerator(final GeneratorContext context) {
        super(context);
    }

    protected abstract int payloadLength();

    @Override
    protected String tryGenerateNonNull(final Random random) {
        final String prefix = random.digits(prefixLength());
        final String payload = payload(random);
        final char checkDigit = getCheckDigit(payload);
        final String suffix = random.digits(suffixLength());
        final StringBuilder result = new StringBuilder(prefix).append(payload).append(suffix);
        result.setCharAt(checkPosition(), checkDigit);
        return result.toString();
    }

    protected String payload(final Random random) {
        final int length = payloadLength();
        final char[] res = new char[length];

        // Avoid generating numbers that start with zero to prevent the loss of
        // the leading digit if the generated string is converted to an int/long
        res[0] = random.characterRange('1', '9');
        for (int i = 1; i < length; i++) {
            res[i] = random.characterRange('0', '9');
        }
        return new String(res);
    }

    private char getCheckDigit(final String payload) {
        int result = base() - modulo(payload);
        if (result == 10) {
            return treat10As();
        } else if (result == 11) {
            return treat11As();
        } else {
            return (char) (result + '0');
        }
    }

    private int modulo(final String payload) {
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

    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    protected int prefixLength() {
        return 0;
    }

    protected int suffixLength() {
        return 1;
    }

    protected int checkPosition() {
        return prefixLength() + payloadLength();
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

    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    protected int base() {
        return 10;
    }

    protected char treat10As() {
        return '0';
    }

    protected char treat11As() {
        return '0';
    }

    enum Direction {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT
    }
}