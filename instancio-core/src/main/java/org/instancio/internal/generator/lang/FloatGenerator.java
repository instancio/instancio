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
import org.instancio.generator.specs.FloatSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.math.BigDecimalGenerator;
import org.instancio.internal.generator.specs.InternalFractionalNumberGeneratorSpec;
import org.instancio.settings.Keys;

import java.math.BigDecimal;

public class FloatGenerator extends AbstractGenerator<Float>
        implements FloatSpec, InternalFractionalNumberGeneratorSpec<Float> {

    private final BigDecimalGenerator delegate;

    public FloatGenerator(final GeneratorContext context) {
        super(context);
        delegate = new BigDecimalGenerator(context)
                .min(new BigDecimal(String.valueOf(context.getSettings().get(Keys.FLOAT_MIN))))
                .max(new BigDecimal(String.valueOf(context.getSettings().get(Keys.FLOAT_MAX))))
                .nullable(context.getSettings().get(Keys.FLOAT_NULLABLE));
    }

    @Override
    public String apiMethod() {
        return "floats()";
    }

    @Override
    public Float getMin() {
        return delegate.getMin().floatValue();
    }

    @Override
    public Float getMax() {
        return delegate.getMax().floatValue();
    }

    @Override
    public FloatGenerator scale(final int scale) {
        delegate.scale(scale);
        return this;
    }

    @Override
    public FloatGenerator precision(final int precision) {
        delegate.precision(precision);
        return this;
    }

    @Override
    public FloatGenerator min(final Float min) {
        ApiValidator.notNull(min, "'min' must not be null");
        delegate.min(new BigDecimal(String.valueOf(min)));
        return this;
    }

    @Override
    public FloatGenerator max(final Float max) {
        ApiValidator.notNull(max, "'max' must not be null");
        delegate.max(new BigDecimal(String.valueOf(max)));
        return this;
    }

    @Override
    public FloatGenerator range(final Float min, final Float max) {
        ApiValidator.notNull(min, "'min' must not be null");
        ApiValidator.notNull(max, "'max' must not be null");
        delegate.range(
                new BigDecimal(String.valueOf(min)),
                new BigDecimal(String.valueOf(max)));
        return this;
    }

    @Override
    public FloatGenerator nullable() {
        delegate.nullable();
        return this;
    }

    @Override
    public FloatGenerator nullable(final boolean isNullable) {
        delegate.nullable(isNullable);
        return this;
    }

    @Override
    public boolean isNullable() {
        return delegate.isNullable();
    }

    @Override
    protected Float tryGenerateNonNull(final Random random) {
        return delegate.tryGenerateNonNull(random).floatValue();
    }
}
