/*
 * Copyright 2022-2026 the original author or authors.
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
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.math.BigDecimalGenerator;
import org.instancio.internal.generator.specs.InternalFractionalNumberGeneratorSpec;
import org.instancio.settings.Keys;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;

public class DoubleGenerator extends AbstractGenerator<Double>
        implements DoubleSpec, InternalFractionalNumberGeneratorSpec<Double> {

    private final BigDecimalGenerator delegate;

    public DoubleGenerator(final GeneratorContext context) {
        super(context);
        delegate = new BigDecimalGenerator(context)
                .min(new BigDecimal(String.valueOf(context.getSettings().get(Keys.DOUBLE_MIN))))
                .max(new BigDecimal(String.valueOf(context.getSettings().get(Keys.DOUBLE_MAX))))
                .nullable(context.getSettings().get(Keys.DOUBLE_NULLABLE));
    }

    @Override
    public String apiMethod() {
        return "doubles()";
    }

    @Override
    public DoubleGenerator scale(final int scale) {
        delegate.scale(scale);
        return this;
    }

    @Override
    public DoubleGenerator precision(final int precision) {
        delegate.precision(precision);
        return this;
    }

    @Override
    public DoubleGenerator min(final Double min) {
        ApiValidator.notNull(min, "'min' must not be null");
        delegate.min(new BigDecimal(String.valueOf(min)));
        return this;
    }

    @Override
    public DoubleGenerator max(final Double max) {
        ApiValidator.notNull(max, "'max' must not be null");
        delegate.max(new BigDecimal(String.valueOf(max)));
        return this;
    }

    @Override
    public DoubleGenerator range(final Double min, final Double max) {
        ApiValidator.notNull(min, "'min' must not be null");
        ApiValidator.notNull(max, "'max' must not be null");
        delegate.range(
                new BigDecimal(String.valueOf(min)),
                new BigDecimal(String.valueOf(max)));
        return this;
    }

    @Override
    public void ensureMinIsGreaterThanOrEqualTo(final BigDecimal otherMin) {
        delegate.ensureMinIsGreaterThanOrEqualTo(otherMin);
    }

    @Override
    public void ensureMaxIsLessThanOrEqualTo(final BigDecimal otherMax) {
        delegate.ensureMaxIsLessThanOrEqualTo(otherMax);
    }

    @Override
    public void integerMax(final int integerMax) {
        delegate.integerMax(integerMax);
    }

    @Override
    public DoubleGenerator nullable() {
        delegate.nullable();
        return this;
    }

    @Override
    public DoubleGenerator nullable(final boolean isNullable) {
        delegate.nullable(isNullable);
        return this;
    }

    @Override
    public boolean isNullable() {
        return delegate.isNullable();
    }

    @Override
    protected Double tryGenerateNonNull(final Random random) {
        return delegate.tryGenerateNonNull(random).doubleValue();
    }

    @Override
    public @Nullable Double generate(final Random random) {
        final BigDecimal result = delegate.generate(random);
        return result == null ? null : result.doubleValue();
    }
}
