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
package org.instancio.internal.generator.util;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.lang.LongGenerator;

import java.util.OptionalLong;

public final class OptionalLongGenerator extends AbstractGenerator<OptionalLong> {

    private final LongGenerator delegate;

    OptionalLongGenerator(GeneratorContext context) {
        super(context);
        this.delegate = new LongGenerator(context);
    }

    @Override
    public String apiMethod() {
        return null;
    }

    @Override
    protected OptionalLong tryGenerateNonNull(final Random random) {
        return OptionalLong.of(delegate.generate(random));
    }
}
