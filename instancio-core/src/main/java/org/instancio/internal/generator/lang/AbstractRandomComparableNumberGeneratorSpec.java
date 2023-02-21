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

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.NumberAsGeneratorSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.util.Constants;
import org.instancio.internal.util.NumberUtils;

public abstract class AbstractRandomComparableNumberGeneratorSpec<T extends Number & Comparable<T>>
        extends AbstractRandomNumberGeneratorSpec<T> {

    protected AbstractRandomComparableNumberGeneratorSpec(
            final GeneratorContext context, final T min, final T max, final boolean nullable) {

        super(context, min, max, nullable);
    }

    /**
     * {@inheritDoc}
     * <p>
     * If the specified {@code min} value is greater than or equal to the current {@code max} value,
     * the {@code max} value will be updated to a value higher than the given {@code min}.
     */
    @Override
    public NumberAsGeneratorSpec<T> min(final T min) {
        super.min(min);
        super.max(NumberUtils.calculateNewMax(getMax(), min, Constants.RANGE_ADJUSTMENT_PERCENTAGE));
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * If the specified {@code max} value is less than or equal to the current {@code min} value,
     * the {@code min} value will be updated to a value lower than the given {@code max}.
     */
    @Override
    public NumberAsGeneratorSpec<T> max(final T max) {
        super.max(max);
        super.min(NumberUtils.calculateNewMin(getMin(), max, Constants.RANGE_ADJUSTMENT_PERCENTAGE));
        return this;
    }

    @Override
    public NumberAsGeneratorSpec<T> range(final T min, final T max) {
        super.range(min, max);
        ApiValidator.isTrue(min.compareTo(max) <= 0,
                "Invalid 'range(%s, %s)': lower bound must be less than or equal to upper bound", min, max);
        return this;
    }
}
