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

import java.math.BigDecimal;

public class BigDecimalGenerator extends AbstractRandomComparableNumberGeneratorSpec<BigDecimal>
        implements NumberGeneratorSpec<BigDecimal> {

    private static final long DEFAULT_MIN = 1;
    private static final long DEFAULT_MAX = 10_000;

    public BigDecimalGenerator(final GeneratorContext context) {
        super(context,
                BigDecimal.valueOf(DEFAULT_MIN),
                BigDecimal.valueOf(DEFAULT_MAX),
                false);
    }

    public BigDecimalGenerator(
            final GeneratorContext context, final BigDecimal min, final BigDecimal max, final boolean nullable) {
        super(context, min, max, nullable);
    }

    @Override
    protected BigDecimal generateNonNullValue() {
        return BigDecimal.valueOf(random().doubleBetween(min.doubleValue(), max.doubleValue()));
    }
}
