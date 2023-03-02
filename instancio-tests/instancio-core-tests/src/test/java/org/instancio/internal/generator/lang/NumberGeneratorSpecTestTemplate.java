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

import org.instancio.exception.InstancioApiException;
import org.instancio.generator.specs.NumberGeneratorSpec;
import org.instancio.internal.util.Constants;
import org.instancio.internal.util.NumberUtils;
import org.instancio.internal.util.TypeUtils;
import org.instancio.support.DefaultRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies {@link NumberGeneratorSpec} implementation for {@code java.lang} numeric types.
 *
 * @param <T> number type
 */
public abstract class NumberGeneratorSpecTestTemplate<T extends Number & Comparable<T>> {
    private static final int PERCENTAGE = Constants.RANGE_ADJUSTMENT_PERCENTAGE;
    private static final long INITIAL_MIN = 0;
    private static final long INITIAL_MAX = 50;
    private static final DefaultRandom RANDOM = new DefaultRandom();

    private AbstractRandomNumberGeneratorSpec<T> generator;

    private T initialMin, initialMax;

    /**
     * @return generator under test
     */
    protected abstract AbstractRandomNumberGeneratorSpec<T> createGenerator();

    protected abstract String apiMethod();

    @BeforeEach
    final void initTemplate() {
        initialMin = asT(INITIAL_MIN);
        initialMax = asT(INITIAL_MAX);
        generator = createGenerator();
        generator.range(initialMin, initialMax);
    }

    protected final AbstractRandomNumberGeneratorSpec<T> getGenerator() {
        return generator;
    }

    @Test
    void verifyApiMethod() {
        assertThat(generator.apiMethod()).isEqualTo(apiMethod());
    }

    @Test
    @DisplayName("newMax < min: new min should be less than newMax by PERCENTAGE")
    void newMaxIsLessThanMin() {
        T newMax = asT(initialMin.longValue() - 1);
        generator.max(newMax);
        assertThat(generator.getMin())
                .as("min=%s, newMax=%s", initialMin, newMax)
                .isEqualTo(calculatePercentage(newMax, -PERCENTAGE));
    }

    @Test
    @DisplayName("newMax == min: new min should be less than newMax by PERCENTAGE")
    void newMaxIsEqualToMin() {
        T newMax = initialMin;
        generator.max(newMax);
        assertThat(generator.getMin())
                .as("min=%s, newMax=%s", initialMin, newMax)
                // using ComparingTo to ignore BigDecimal scale
                .isEqualByComparingTo(calculatePercentage(newMax, -PERCENTAGE));
    }

    @Test
    @DisplayName("newMin > max: new max should be set to greater than newMin by PERCENTAGE")
    void newMinIsGreaterThanMax() {
        T newMin = asT(initialMax.longValue() + 1);
        generator.min(newMin);
        assertThat(generator.getMax()).isEqualTo(calculatePercentage(newMin, PERCENTAGE));
    }

    @Test
    @DisplayName("newMin == max: max should remain unchanged")
    void newMinIsEqualToMax() {
        T newMin = asT(initialMax.longValue());
        generator.min(newMin);

        assertThat(generator.getMin())
                .isEqualTo(newMin)
                .isEqualTo(initialMax)
                .isEqualTo(generator.getMax());

        final T result = generator.generate(RANDOM);
        // Not using isEqualTo() here because BigDecimal equality check includes the scale field,
        // therefore 50 and 50.0 are not equal, which fails the test
        assertThat(result).isEqualByComparingTo(newMin);
    }

    @Test
    void range() {
        final T min = asT(2L);
        final T max = asT(3L);
        generator.range(min, max);
        assertThat(generator.getMin()).isEqualTo(min);
        assertThat(generator.getMax()).isEqualTo(max);
    }

    @Test
    void rangeWithEqualMinMax() {
        final T min = asT(3L);
        final T max = asT(3L);
        generator.range(min, max);
        assertThat(generator.getMin())
                .isEqualTo(generator.getMax())
                .isEqualTo(min);
    }

    @Test
    void minValidation() {
        assertThatThrownBy(() -> generator.min(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("'min' must not be null");
    }

    @Test
    void maxValidation() {
        assertThatThrownBy(() -> generator.max(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("'max' must not be null");
    }

    @Test
    void rangeValidation() {
        assertThatThrownBy(() -> generator.range(null, initialMax))
                .isInstanceOf(InstancioApiException.class)
                .hasMessage("'min' must not be null");

        assertThatThrownBy(() -> generator.range(initialMin, null))
                .isInstanceOf(InstancioApiException.class)
                .hasMessage("'max' must not be null");
    }

    @Test
    void rangeThrowsErrorIfMinIsGreaterThanMax() {
        final T min = asT(3L);
        final T max = asT(2L);
        final String errorRange = String.format("%s, %s", min, max);
        assertThatThrownBy(() -> generator.range(min, max))
                .isInstanceOf(InstancioApiException.class)
                .hasMessage("Invalid 'range(%s)': lower bound must be less than or equal to upper bound", errorRange);
    }

    @Test
    void nullable() {
        final Set<T> results = new HashSet<>();

        generator.nullable();
        for (int i = 0; i < 1000; i++) {
            results.add(generator.generate(RANDOM));
        }

        assertThat(results).hasSizeGreaterThan(1).containsNull();
    }

    private T calculatePercentage(final T initial, final int percentage) {
        final BigDecimal initialBD = new BigDecimal(initial.toString());
        final BigDecimal tmp = initialBD.multiply(
                new BigDecimal(percentage).divide(new BigDecimal(100), 3, RoundingMode.HALF_UP));

        final BigDecimal delta = percentage > 0
                ? initialBD.add(tmp)
                : initialBD.subtract(tmp);

        final BigDecimal deltaAbs = delta.abs();
        final BigDecimal result = delta.signum() < 0 ? deltaAbs.negate() : deltaAbs;
        return (T) NumberUtils.bigDecimalConverter(initial.getClass()).apply(result);
    }

    @SuppressWarnings("unchecked")
    private T asT(final long value) {
        final Class<?> numberType = TypeUtils.getGenericSuperclassTypeArgument(getClass());
        assertThat(numberType).isNotNull();
        return (T) NumberUtils.longConverter(numberType).apply(value);
    }
}
