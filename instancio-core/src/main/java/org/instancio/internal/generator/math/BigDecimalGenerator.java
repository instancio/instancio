/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.internal.generator.math;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.BigDecimalAsGeneratorSpec;
import org.instancio.generator.specs.BigDecimalSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.lang.AbstractRandomComparableNumberGeneratorSpec;
import org.instancio.settings.Keys;
import org.instancio.support.Global;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class BigDecimalGenerator extends AbstractRandomComparableNumberGeneratorSpec<BigDecimal>
        implements BigDecimalSpec, BigDecimalAsGeneratorSpec {

    private static final BigDecimal DEFAULT_MIN = new BigDecimal("0.01");
    private static final BigDecimal DEFAULT_MAX = new BigDecimal("10000.00");

    private int scale;
    private int precision;

    public BigDecimalGenerator() {
        this(Global.generatorContext());
    }

    public BigDecimalGenerator(final GeneratorContext context) {
        this(context, DEFAULT_MIN, DEFAULT_MAX, false);
    }

    public BigDecimalGenerator(
            final GeneratorContext context, final BigDecimal min, final BigDecimal max, final boolean nullable) {
        super(context, min, max, nullable);

        this.scale = context.getSettings().get(Keys.BIG_DECIMAL_SCALE);
    }

    @Override
    public String apiMethod() {
        return "bigDecimal()";
    }

    @Override
    public BigDecimalGenerator scale(final int scale) {
        this.scale = scale;
        return this;
    }

    @Override
    public BigDecimalGenerator precision(final int precision) {
        ApiValidator.isTrue(precision > 0, "'precision' must be positive: %s", precision);
        this.precision = precision;
        return this;
    }

    @Override
    public BigDecimalGenerator min(final BigDecimal min) {
        super.min(min);
        unsetPrecision();
        return this;
    }

    @Override
    public BigDecimalGenerator max(final BigDecimal max) {
        super.max(max);
        unsetPrecision();
        return this;
    }

    @Override
    public BigDecimalGenerator range(final BigDecimal min, final BigDecimal max) {
        super.range(min, max);
        unsetPrecision();
        return this;
    }

    @Override
    public BigDecimalGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    public BigDecimalGenerator nullable(final boolean isNullable) {
        super.nullable(isNullable);
        return this;
    }

    /**
     * Since precision() is not compatible with min(), max(), and range() methods,
     * if any of those methods are called, unset precision, in case it was set,
     * to revert to range-based generation.
     */
    private void unsetPrecision() {
        precision = 0;
    }

    @Override
    protected BigDecimal tryGenerateNonNull(final Random random) {
        // If precision is not set, generate a value in the [min, max] range
        if (precision == 0) {
            final BigDecimal delta = getMax().subtract(getMin());
            final BigDecimal rndDelta = delta.multiply(BigDecimal.valueOf(random.doubleRange(0.01, 1)));
            return getMin().add(rndDelta).setScale(scale, RoundingMode.HALF_UP);
        }

        // Generate value based on specified precision
        final char[] result;

        if (scale == 0) {
            result = generateInteger(random);
            // result: '[1-9][0-9]{precision - 1}'
        } else if (scale < 0) {
            result = generateWithNegativeScale(random);
            // result: '[1-9][0-9]{precision - 1}0{abs(scale)}'
        } else if (precision == scale) {
            result = generateWithEqualPrecisionAndScale(random);
            // result: '0.[1-9][0-9]{scale}'
        } else if (precision > scale) {
            result = generateFractionalWithPrecisionGreaterThanScale(random);
            // result: '[1-9]{precision - scale}.[0-9]{scale}'
        } else {
            result = generateWithPrecisionLessThanScale(random);
            // result: `0.[0]{scale - precision}[1-9]{precision}`
        }

        return new BigDecimal(
                new String(result),
                new MathContext(precision, RoundingMode.UNNECESSARY));
    }

    private char[] generateFractionalWithPrecisionGreaterThanScale(final Random random) {
        final char[] digits = new char[precision + 1];
        int i = 0;
        digits[i++] = oneToNine(random);

        while (i < precision - scale) {
            digits[i++] = zeroToNine(random);
        }

        digits[i++] = '.';

        while (i < digits.length) {
            digits[i++] = zeroToNine(random);
        }
        return digits;
    }

    private char[] generateWithEqualPrecisionAndScale(final Random random) {
        final char[] digits = new char[1 + scale];
        int i = 0;
        digits[i++] = '.';
        digits[i++] = oneToNine(random);

        while (i < digits.length) {
            digits[i++] = zeroToNine(random);
        }
        return digits;
    }

    private char[] generateWithNegativeScale(final Random random) {
        final char[] digits = new char[precision + Math.abs(scale)];
        int i = 0;
        digits[i++] = oneToNine(random);

        while (i < precision) {
            digits[i++] = zeroToNine(random);
        }
        while (i < digits.length) {
            digits[i++] = '0';
        }
        return digits;
    }

    private char[] generateInteger(final Random random) {
        final char[] digits = new char[precision];
        int i = 0;
        digits[i++] = oneToNine(random);

        while (i < digits.length) {
            digits[i++] = zeroToNine(random);
        }
        return digits;
    }

    private char[] generateWithPrecisionLessThanScale(final Random random) {
        final char[] digits = new char[scale + 1];
        int i = 0;
        digits[i++] = '.';

        while (i <= scale - precision) {
            digits[i++] = '0';
        }
        while (i < digits.length) {
            digits[i++] = oneToNine(random);
        }
        return digits;
    }

    private static char zeroToNine(final Random random) {
        return random.characterRange('0', '9');
    }

    private static char oneToNine(final Random random) {
        return random.characterRange('1', '9');
    }
}
