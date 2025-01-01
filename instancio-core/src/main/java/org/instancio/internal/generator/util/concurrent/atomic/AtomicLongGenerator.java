/*
 * Copyright 2022-2025 the original author or authors.
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

import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongGenerator extends AbstractRandomNumberGeneratorSpec<AtomicLong>
        implements NumberGeneratorSpec<AtomicLong> {

    private static final int DEFAULT_MIN = 1;
    private static final int DEFAULT_MAX = 10_000;

    public AtomicLongGenerator(final GeneratorContext context) {
        super(context,
                new AtomicLong(DEFAULT_MIN),
                new AtomicLong(DEFAULT_MAX),
                false);
    }

    @Override
    public String apiMethod() {
        return "atomicLong()";
    }

    @Override
    protected AtomicLong tryGenerateNonNull(final Random random) {
        return new AtomicLong(random.intRange(getMin().intValue(), getMax().intValue()));
    }
}
