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
package org.instancio.generator.lang;

import org.instancio.exception.InstancioApiException;
import org.instancio.generator.specs.NumberGeneratorSpec;
import org.instancio.internal.random.DefaultRandom;
import org.instancio.util.Constants;
import org.instancio.util.NumberUtils;
import org.instancio.util.TypeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
    @DisplayName("newMax < min: new min should be less than newMax by PERCENTAGE")
    void newMaxIsLessThanMin() {
        T newMax = asT(initialMin.longValue() - 1);
        generator.min(newMax);
        assertThat(generator.getMin()).isEqualTo(calculatePercentage(newMax, -PERCENTAGE));
    }

    @Test
    @DisplayName("newMax == min: new min should be less than newMax by PERCENTAGE")
    void newMaxIsEqualToMin() {
        T newMax = asT(initialMin.longValue());
        generator.min(newMax);
        assertThat(generator.getMin()).isEqualTo(calculatePercentage(newMax, -PERCENTAGE));
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
        final long result = initial.longValue() + initial.longValue() * percentage / 100;
        return asT(result);
    }

    @SuppressWarnings("unchecked")
    private T asT(final long value) {
        final Class<?> numberType = TypeUtils.getGeneratorTypeArgument(getClass());
        assertThat(numberType).isNotNull();
        return (T) NumberUtils.getToLongConverter(numberType).apply(value);
    }
}
