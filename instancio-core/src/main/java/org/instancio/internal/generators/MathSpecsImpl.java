/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.internal.generators;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.BigDecimalSpec;
import org.instancio.generator.specs.BigIntegerSpec;
import org.instancio.generators.MathSpecs;
import org.instancio.internal.generator.math.BigDecimalGenerator;
import org.instancio.internal.generator.math.BigIntegerGenerator;

final class MathSpecsImpl implements MathSpecs {

    private final GeneratorContext context;

    MathSpecsImpl(final GeneratorContext context) {
        this.context = context;
    }

    @Override
    public BigDecimalSpec bigDecimal() {
        return new BigDecimalGenerator(context);
    }

    @Override
    public BigIntegerSpec bigInteger() {
        return new BigIntegerGenerator(context);
    }
}
