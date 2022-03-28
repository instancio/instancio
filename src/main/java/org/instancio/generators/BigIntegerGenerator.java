/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.generators;

import org.instancio.generators.coretypes.AbstractRandomNumberGeneratorSpec;
import org.instancio.generators.coretypes.NumberGeneratorSpec;
import org.instancio.internal.model.ModelContext;

import java.math.BigInteger;

public class BigIntegerGenerator extends AbstractRandomNumberGeneratorSpec<BigInteger> implements NumberGeneratorSpec<BigInteger> {

    private static final long DEFAULT_MIN = 1;
    private static final long DEFAULT_MAX = 10_000;

    public BigIntegerGenerator(final ModelContext<?> context) {
        super(context,
                BigInteger.valueOf(DEFAULT_MIN),
                BigInteger.valueOf(DEFAULT_MAX),
                false);
    }

    public BigIntegerGenerator(final ModelContext<?> context, final BigInteger min, final BigInteger max, final boolean nullable) {
        super(context, min, max, nullable);
    }

    @Override
    protected BigInteger generateNonNullValue() {
        return BigInteger.valueOf(random().longBetween(min.longValue(), max.longValue()));
    }
}
