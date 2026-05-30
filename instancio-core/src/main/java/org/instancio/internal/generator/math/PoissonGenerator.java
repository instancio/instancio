/*
 * Copyright 2022-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.internal.generator.math;

import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.settings.Keys;

/**
 * Generator for producing Poisson distributed random values.
 * <p>
 * This generator uses the {@link Keys#POISSON_LAMBDA} setting
 * to determine the mean (lambda) of the distribution.
 */
public class PoissonGenerator implements Generator<Integer> {

    private final GeneratorContext context;

    public PoissonGenerator(final GeneratorContext context) {
        this.context = context;
    }

    @Override
    public Integer generate(final Random random) {
        // TODO: Implement actual Poisson distribution logic using context.settings()
        return 0;
    }
}