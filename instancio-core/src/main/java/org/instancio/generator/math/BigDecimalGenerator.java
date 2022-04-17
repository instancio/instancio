/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.generator.math;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.lang.AbstractRandomComparableNumberGeneratorSpec;
import org.instancio.generator.lang.NumberGeneratorSpec;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.Verify;

import java.math.BigDecimal;

public class BigDecimalGenerator extends AbstractRandomComparableNumberGeneratorSpec<BigDecimal>
        implements NumberGeneratorSpec<BigDecimal> {

    private static final BigDecimal DEFAULT_MIN = BigDecimal.valueOf(0.000001d);
    private static final BigDecimal DEFAULT_MAX = BigDecimal.valueOf(Double.MAX_VALUE);

    public BigDecimalGenerator(final GeneratorContext context) {
        super(context, DEFAULT_MIN, DEFAULT_MAX, false);
    }

    public BigDecimalGenerator(
            final GeneratorContext context, final BigDecimal min, final BigDecimal max, final boolean nullable) {
        super(context, min, max, nullable);
    }

    @Override
    public NumberGeneratorSpec<BigDecimal> min(final BigDecimal min) {
        this.min = Verify.notNull(min);
        if (min.compareTo(max) >= 0) {
            max = min.add(DEFAULT_MAX);
        }
        return this;
    }

    @Override
    public NumberGeneratorSpec<BigDecimal> max(final BigDecimal max) {
        this.max = Verify.notNull(max);
        if (max.compareTo(min) <= 0) {
            min = max.subtract(DEFAULT_MAX);
        }
        return this;
    }

    @Override
    protected BigDecimal generateNonNullValue(final RandomProvider random) {
        return BigDecimal.valueOf(random.doubleRange(min.doubleValue(), max.doubleValue()));
    }
}
