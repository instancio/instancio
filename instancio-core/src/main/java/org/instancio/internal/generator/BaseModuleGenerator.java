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
package org.instancio.internal.generator;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.util.NumberUtils;
import org.instancio.support.Global;

import java.util.stream.IntStream;

public abstract class BaseModuleGenerator extends AbstractGenerator<String> {

    public BaseModuleGenerator() {
        super(Global.generatorContext());
    }

    public BaseModuleGenerator(final GeneratorContext context) {
        super(context);
    }

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
        return random.digits(payloadLength());
    }

    private char getCheckDigit(final String payload) {
        int result = base() - module(payload);
        if (result == 10) {
            return treat10As();
        } else if (result == 11) {
            return treat11As();
        } else {
            return (char) (result + '0');
        }
    }

    private int module(final String payload) {
        final String newPayload = reverse() ? new StringBuilder(payload).reverse().toString() : payload;
        int sum = IntStream.range(0, payload.length())
                .map(i -> extractDigit(i, newPayload))
                .map(n -> sumDigits() ? NumberUtils.sumDigits(n) : n)
                .map(n -> sumDigits() ? NumberUtils.sumDigits(n) : n)
                .sum();
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

    protected abstract int payloadLength();

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

    protected boolean reverse() {
        return true;
    }

    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract") // Why PMD is flagging this method empty?
    protected int base() {
        return 10;
    }

    protected char treat10As() {
        return '0';
    }

    protected char treat11As() {
        return '0';
    }
}