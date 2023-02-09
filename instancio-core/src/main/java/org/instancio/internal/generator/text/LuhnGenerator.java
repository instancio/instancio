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
package org.instancio.internal.generator.text;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.NullableGeneratorSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.specs.InternalLengthGeneratorSpec;
import org.instancio.internal.util.LuhnUtils;

public class LuhnGenerator extends AbstractGenerator<String>
        implements NullableGeneratorSpec<String>, InternalLengthGeneratorSpec<String> {

    private static final int DEFAULT_SIZE = 16;

    private int minSize = DEFAULT_SIZE;
    private int maxSize = DEFAULT_SIZE;
    private int startIndex = -1;
    private int endIndex = -1;
    private int checkDigitIndex = -1;

    public LuhnGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return null;
    }

    @Override
    public LuhnGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    public LuhnGenerator nullable(final boolean isNullable) {
        super.nullable(isNullable);
        return this;
    }

    public LuhnGenerator startIndex(final int idx) {
        ApiValidator.isTrue(idx >= 0, "Start index must not be negative: %s", idx);
        this.startIndex = idx;
        return this;
    }

    public LuhnGenerator endIndex(final int idx) {
        ApiValidator.isTrue(idx >= 0, "End index must not be negative: %s", idx);
        // Avoid generating large strings
        // The default value of Hibernate @LuhnCheck "endIndex" is Integer.MAX_VALUE
        this.endIndex = idx == Integer.MAX_VALUE ? maxSize - 1 : idx;
        return this;
    }

    public LuhnGenerator checkIndex(final int idx) {
        ApiValidator.isTrue(idx >= 0, "Check digit index must not be negative: %s", idx);
        this.checkDigitIndex = idx;
        return this;
    }

    public LuhnGenerator length(final int length) {
        ApiValidator.isTrue(length > 1,
                "Luhn-valid number length must be greater than 1, but was: %s", length);
        this.minSize = length;
        this.maxSize = length;
        return this;
    }

    @Override
    public LuhnGenerator length(final int min, final int max) {
        ApiValidator.isTrue(min > 0 && max > 1,
                "Luhn-valid number length must be greater than 1, but was: length(%s, %s)", min, max);
        ApiValidator.isTrue(min <= max, "Min must be less than or equal to max");

        this.minSize = min;
        this.maxSize = max;
        return this;
    }

    @Override
    public String generate(final Random random) {
        if (random.diceRoll(isNullable())) {
            return null;
        }
        final int size = random.intRange(minSize, maxSize);
        final int start = Math.max(0, startIndex);
        final int end = endIndex == -1 ? size - 1 : endIndex;
        final int actualSize = Math.max(size, endIndex);
        final int check = checkDigitIndex == -1 ? end : checkDigitIndex;
        final int payloadSize = end - start + 1;
        final String digits = random.digits(payloadSize);
        final char[] payloadChars = digits.toCharArray();
        final int checkDigit = LuhnUtils.getCheckDigit(payloadChars);

        String result = new String(payloadChars);

        if (start > 0) {
            final String prefix = random.digits(start);
            result = prefix + result;
        }
        if (end < actualSize) {
            final String suffix = random.digits(actualSize - end - 1);
            result += suffix;
        }

        final char[] resultChars = result.toCharArray();
        resultChars[check] = (char) (checkDigit + '0');
        return new String(resultChars);
    }
}