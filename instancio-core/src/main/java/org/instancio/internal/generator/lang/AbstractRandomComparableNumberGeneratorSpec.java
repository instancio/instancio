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

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.NumberGeneratorSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.specs.InternalNumberGeneratorSpec;
import org.instancio.internal.util.Constants;
import org.instancio.internal.util.NumberUtils;
import org.instancio.internal.util.NumericBounds;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;

public abstract class AbstractRandomComparableNumberGeneratorSpec<T extends Number & Comparable<T>>
        extends AbstractRandomNumberGeneratorSpec<T> implements InternalNumberGeneratorSpec<T> {

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
    public NumberGeneratorSpec<T> min(final @NonNull T min) {
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
    public NumberGeneratorSpec<T> max(final @NonNull T max) {
        super.max(max);
        super.min(NumberUtils.calculateNewMin(getMin(), max, Constants.RANGE_ADJUSTMENT_PERCENTAGE));
        return this;
    }

    @Override
    public NumberGeneratorSpec<T> range(final @NonNull T min, final @NonNull T max) {
        super.range(min, max); // validates that neither is null
        ApiValidator.isTrue(min.compareTo(max) <= 0,
                "invalid 'range(%s, %s)': lower bound must be less than or equal to upper bound", min, max);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void ensureMinIsGreaterThanOrEqualTo(final BigDecimal otherMin) {
        final T currentMin = getMin();
        final T newMin = (T) NumberUtils
                .bigDecimalConverter(currentMin.getClass())
                .apply(otherMin);

        if (newMin.compareTo(currentMin) > 0) {
            this.min(newMin);
        }
    }

    /**
     * Ensures that the maximum value of the number generator is less than or equal
     * to the specified {@code otherMax} value. If the current maximum value is
     * greater than {@code otherMax}, the maximum value will be updated to a
     * value converted from {@code otherMax}.
     *
     * @param otherMax the maximum value to compare against; if the current maximum
     *                 value exceeds this, it will be updated to an equivalent or smaller value
     *                 based on the comparison.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void ensureMaxIsLessThanOrEqualTo(final BigDecimal otherMax) {
        final T currentMax = getMax();
        final T newMax = (T) NumberUtils
                .bigDecimalConverter(currentMax.getClass())
                .apply(otherMax);

        if (newMax.compareTo(currentMax) < 0) {
            this.max(newMax);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void integerMax(final int integerMax) {
        final Class<T> targetClass = (Class<T>) getMin().getClass();

        final BigDecimal currentMin = NumberUtils.toBigDecimal(getMin());
        final BigDecimal currentMax = NumberUtils.toBigDecimal(getMax());

        final NumericBounds updatedBounds =
                new NumericBounds(currentMin, currentMax).updateBounds(integerMax);

        final BigDecimal newMinBD = updatedBounds.getMin()
                .max(NumberUtils.toBigDecimal(NumberUtils.getMinValue(targetClass)))
                .max(currentMin);

        final BigDecimal newMaxBD = updatedBounds.getMax()
                .min(NumberUtils.toBigDecimal(NumberUtils.getMaxValue(targetClass)))
                .min(currentMax);

        final T newMin = (T) NumberUtils.bigDecimalConverter(targetClass).apply(newMinBD);
        final T newMax = (T) NumberUtils.bigDecimalConverter(targetClass).apply(newMaxBD);

        this.min(newMin);
        this.max(newMax);
    }

}
