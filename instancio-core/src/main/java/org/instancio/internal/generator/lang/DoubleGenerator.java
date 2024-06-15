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
package org.instancio.internal.generator.lang;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.DoubleSpec;
import org.instancio.settings.Keys;

public class DoubleGenerator extends AbstractRandomComparableNumberGeneratorSpec<Double>
        implements DoubleSpec {

    public DoubleGenerator(final GeneratorContext context) {
        super(context,
                context.getSettings().get(Keys.DOUBLE_MIN),
                context.getSettings().get(Keys.DOUBLE_MAX),
                context.getSettings().get(Keys.DOUBLE_NULLABLE));
    }

    @Override
    public String apiMethod() {
        return "doubles()";
    }

    @Override
    public DoubleGenerator min(final Double min) {
        super.min(min);
        return this;
    }

    @Override
    public DoubleGenerator max(final Double max) {
        super.max(max);
        return this;
    }

    @Override
    public DoubleGenerator range(final Double min, final Double max) {
        super.range(min, max);
        return this;
    }

    @Override
    public DoubleGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    public DoubleGenerator nullable(final boolean isNullable) {
        super.nullable(isNullable);
        return this;
    }

    @Override
    protected Double tryGenerateNonNull(final Random random) {
        return random.doubleRange(getMin(), getMax());
    }
}
