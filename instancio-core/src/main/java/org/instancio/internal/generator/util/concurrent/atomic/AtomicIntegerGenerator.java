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
package org.instancio.internal.generator.util.concurrent.atomic;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.NumberGeneratorSpec;
import org.instancio.internal.generator.lang.AbstractRandomNumberGeneratorSpec;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerGenerator extends AbstractRandomNumberGeneratorSpec<AtomicInteger>
        implements NumberGeneratorSpec<AtomicInteger> {

    private static final int DEFAULT_MIN = 1;
    private static final int DEFAULT_MAX = 10_000;

    public AtomicIntegerGenerator(final GeneratorContext context) {
        super(context,
                new AtomicInteger(DEFAULT_MIN),
                new AtomicInteger(DEFAULT_MAX),
                false);
    }

    @Override
    public String apiMethod() {
        return "atomicInteger()";
    }

    @Override
    protected AtomicInteger generateNonNullValue(final Random random) {
        return new AtomicInteger(random.intRange(getMin().intValue(), getMax().intValue()));
    }
}
