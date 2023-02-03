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
package org.instancio.internal.generator.lang;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.FloatSpec;
import org.instancio.internal.context.Global;
import org.instancio.settings.Keys;

public class FloatGenerator extends AbstractRandomComparableNumberGeneratorSpec<Float>
        implements FloatSpec {

    public FloatGenerator() {
        this(Global.generatorContext());
    }

    public FloatGenerator(final GeneratorContext context) {
        super(context,
                context.getSettings().get(Keys.FLOAT_MIN),
                context.getSettings().get(Keys.FLOAT_MAX),
                context.getSettings().get(Keys.FLOAT_NULLABLE));
    }

    @Override
    public String apiMethod() {
        return "floats()";
    }

    @Override
    public FloatGenerator min(final Float min) {
        super.min(min);
        return this;
    }

    @Override
    public FloatGenerator max(final Float max) {
        super.max(max);
        return this;
    }

    @Override
    public FloatGenerator range(final Float min, final Float max) {
        super.range(min, max);
        return this;
    }

    @Override
    public FloatGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    public FloatGenerator nullable(final boolean isNullable) {
        super.nullable(isNullable);
        return this;
    }

    @Override
    protected Float generateNonNullValue(final Random random) {
        return random.floatRange(getMin(), getMax());
    }
}
