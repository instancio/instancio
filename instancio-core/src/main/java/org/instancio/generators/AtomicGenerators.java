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

package org.instancio.generators;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.NumberGeneratorSpec;
import org.instancio.internal.generator.util.concurrent.atomic.AtomicIntegerGenerator;
import org.instancio.internal.generator.util.concurrent.atomic.AtomicLongGenerator;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Contains built-in generators for {@code java.util.concurrent.atomic} classes.
 */
public class AtomicGenerators {

    private final GeneratorContext context;

    AtomicGenerators(final GeneratorContext context) {
        this.context = context;
    }

    /**
     * Customises generated {@link AtomicInteger} values.
     *
     * @return customised generator
     */
    public NumberGeneratorSpec<AtomicInteger> atomicInteger() {
        return new AtomicIntegerGenerator(context);
    }

    /**
     * Customises generated {@link AtomicLong} values.
     *
     * @return customised generator
     */
    public NumberGeneratorSpec<AtomicLong> atomicLong() {
        return new AtomicLongGenerator(context);
    }
}
