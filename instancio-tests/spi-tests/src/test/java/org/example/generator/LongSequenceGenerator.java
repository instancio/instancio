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
package org.example.generator;

import org.instancio.Generator;
import org.instancio.Random;
import org.instancio.generator.GeneratorContext;

import static org.assertj.core.api.Fail.fail;

public class LongSequenceGenerator implements Generator<Long> {

    public static final long START_FROM = 10;
    private long current;
    private boolean initialised;

    @Override
    public void init(final GeneratorContext context) {
        current = START_FROM;
        if (initialised) {
            // All generators, including those from SPI, should be initialised exactly
            // once per Instancio.of() invocation.
            fail("%s should not be initialised more than once!", getClass().getName());
        }
        initialised = true;
    }

    @Override
    public Long generate(final Random random) {
        return current++;
    }
}
