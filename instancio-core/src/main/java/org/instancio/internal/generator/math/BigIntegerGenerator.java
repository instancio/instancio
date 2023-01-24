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
import org.instancio.generator.specs.BigIntegerSpec;
import org.instancio.internal.context.Global;
import org.instancio.internal.generator.lang.AbstractRandomComparableNumberGeneratorSpec;

import java.math.BigInteger;

public class BigIntegerGenerator extends AbstractRandomComparableNumberGeneratorSpec<BigInteger>
        implements BigIntegerSpec {

    private static final BigInteger DEFAULT_MIN = BigInteger.ONE;
    private static final BigInteger DEFAULT_MAX = BigInteger.valueOf(Long.MAX_VALUE);

    public BigIntegerGenerator() {
        this(Global.generatorContext());
    }

    public BigIntegerGenerator(final GeneratorContext context) {
        super(context, DEFAULT_MIN, DEFAULT_MAX, false);
    }

    public BigIntegerGenerator(
            final GeneratorContext context, final BigInteger min, final BigInteger max, final boolean nullable) {
        super(context, min, max, nullable);
    }

    @Override
    public String apiMethod() {
        return "bigInteger()";
    }

    @Override
    public BigIntegerGenerator min(final BigInteger min) {
        super.min(min);
        return this;
    }

    @Override
    public BigIntegerGenerator max(final BigInteger max) {
        super.max(max);
        return this;
    }

    @Override
    public BigIntegerGenerator range(final BigInteger min, final BigInteger max) {
        super.range(min, max);
        return this;
    }

    @Override
    public BigIntegerGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected BigInteger generateNonNullValue(final Random random) {
        return BigInteger.valueOf(random.longRange(getMin().longValue(), getMax().longValue()));
    }
}
