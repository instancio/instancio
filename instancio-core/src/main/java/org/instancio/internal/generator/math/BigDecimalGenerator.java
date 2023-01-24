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
package org.instancio.internal.generator.math;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.BigDecimalAsStringGeneratorSpec;
import org.instancio.generator.specs.BigDecimalSpec;
import org.instancio.internal.context.Global;
import org.instancio.internal.generator.lang.AbstractRandomComparableNumberGeneratorSpec;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalGenerator extends AbstractRandomComparableNumberGeneratorSpec<BigDecimal>
        implements BigDecimalSpec, BigDecimalAsStringGeneratorSpec {

    private static final BigDecimal DEFAULT_MIN = BigDecimal.valueOf(0.000_001d);
    private static final BigDecimal DEFAULT_MAX = BigDecimal.valueOf(10_000);
    private static final int DEFAULT_SCALE = 5;

    private int scale = DEFAULT_SCALE;

    public BigDecimalGenerator() {
        this(Global.generatorContext());
    }

    public BigDecimalGenerator(final GeneratorContext context) {
        this(context, DEFAULT_MIN, DEFAULT_MAX, false);
    }

    public BigDecimalGenerator(
            final GeneratorContext context, final BigDecimal min, final BigDecimal max, final boolean nullable) {
        super(context, min, max, nullable);
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
    public BigDecimalGenerator min(final BigDecimal min) {
        super.min(min);
        return this;
    }

    @Override
    public BigDecimalGenerator max(final BigDecimal max) {
        super.max(max);
        return this;
    }

    @Override
    public BigDecimalGenerator range(final BigDecimal min, final BigDecimal max) {
        super.range(min, max);
        return this;
    }

    @Override
    public BigDecimalGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected BigDecimal generateNonNullValue(final Random random) {
        final double val = random.doubleRange(getMin().doubleValue(), getMax().doubleValue());
        return BigDecimal.valueOf(val).setScale(scale, RoundingMode.HALF_UP);
    }
}
