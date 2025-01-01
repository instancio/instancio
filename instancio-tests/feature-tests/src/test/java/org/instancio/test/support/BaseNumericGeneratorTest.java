/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.test.support;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Select;
import org.instancio.Selector;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.specs.NumberGeneratorSpec;
import org.instancio.generators.Generators;
import org.instancio.internal.util.Constants;
import org.instancio.internal.util.NumberUtils;
import org.instancio.internal.util.TypeUtils;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
public abstract class BaseNumericGeneratorTest<T extends Number & Comparable<T>> {

    private static final int PERCENTAGE = Constants.RANGE_ADJUSTMENT_PERCENTAGE;
    private static final int SAMPLE_SIZE = 1000;
    private static final long INITIAL_MIN = 0;
    private static final long INITIAL_MAX = 50;

    private final Class<T> targetClass;
    private final Selector selector;
    private final T initialMin;
    private final T initialMax;

    protected BaseNumericGeneratorTest(final Class<T> targetClass) {
        this.targetClass = targetClass;
        this.selector = Select.all(targetClass);
        this.initialMin = asT(INITIAL_MIN);
        this.initialMax = asT(INITIAL_MAX);
    }

    protected abstract NumberGeneratorSpec<T> createSpec(final Generators gen);

    /**
     * Set up the spec with initial values.
     */
    private NumberGeneratorSpec<T> spec(final Generators gen) {
        return createSpec(gen).min(initialMin).max(initialMax);
    }

    @RepeatedTest(SAMPLE_SIZE)
    @DisplayName("newMax < min: new min should be less than newMax by PERCENTAGE")
    final void newMaxIsLessThanMin() {
        final T newMax = asT(initialMin.longValue() - 1);
        final T newMin = asT(newMax.longValue() - PERCENTAGE * Math.abs(newMax.longValue()));

        final T result = Instancio.of(targetClass)
                .generate(selector, gen -> spec(gen).max(newMax))
                .create();

        assertThat(result)
                .as("newMin=%s, newMax=%s", newMin, newMax)
                .isBetween(newMin, newMax);
    }

    @RepeatedTest(SAMPLE_SIZE)
    @DisplayName("newMax == min: min should remain unchanged")
    final void newMaxIsEqualToMin() {
        final T newMax = asT(initialMin.longValue());

        final T result = Instancio.of(targetClass)
                .generate(selector, gen -> spec(gen).max(newMax))
                .create();

        assertThat(result)
                .as("newMin=%s, newMax=%s", initialMax, newMax)
                .isEqualByComparingTo(newMax);
    }

    @RepeatedTest(SAMPLE_SIZE)
    @DisplayName("newMin > max: new max should be set to greater than newMin by PERCENTAGE")
    final void newMinIsGreaterThanMax() {
        final T newMin = asT(initialMax.longValue() + 1);
        final T newMax = asT(Math.round(newMin.longValue() + PERCENTAGE / 100d * Math.abs(newMin.longValue())));

        final T result = Instancio.of(targetClass)
                .generate(selector, gen -> spec(gen).min(newMin))
                .create();

        assertThat(result)
                .as("newMin=%s, newMax=%s", newMin, newMax)
                .isBetween(newMin, newMax);
    }

    @RepeatedTest(SAMPLE_SIZE)
    @DisplayName("newMin == max: max should remain unchanged")
    final void newMinIsEqualToMax() {
        final T newMin = initialMax;

        final T result = Instancio.of(targetClass)
                .generate(selector, gen -> spec(gen).min(newMin))
                .create();

        assertThat(result).isEqualByComparingTo(newMin);
    }

    @RepeatedTest(SAMPLE_SIZE)
    final void range() {
        final T min = asT(2);
        final T max = asT(3);

        final T result = Instancio.of(targetClass)
                .generate(selector, gen -> spec(gen).range(min, max))
                .create();

        assertThat(result)
                .as("min=%s, max=%s", min, max)
                .isBetween(min, max);
    }

    @RepeatedTest(SAMPLE_SIZE)
    final void rangeWithEqualMinMax() {
        final T min = asT(3);
        final T max = asT(3);

        final T result = Instancio.of(targetClass)
                .generate(selector, gen -> spec(gen).range(min, max))
                .create();

        assertThat(result)
                .as("min=%s, max=%s", min, max)
                .isBetween(min, max);
    }

    @RepeatedTest(SAMPLE_SIZE)
    final void rangeStack() {
        final T range1 = asT(1);
        final T range2 = asT(5);

        // equal lower/upper bounds to simplify testing decimal ranges
        final Set<T> results = Instancio.of(targetClass)
                .generate(selector, gen -> spec(gen)
                        .range(range1, range1)
                        .range(range2, range2))
                .stream()
                .limit(50)
                .collect(Collectors.toSet());

        assertThat(results)
                .hasSize(2)
                .allSatisfy(result -> {
                    assertThat(result.compareTo(range1) == 0 || result.compareTo(range2) == 0)
                            .as("Expected %s to be within %s-%s or %s-%s", result, range1, range1, range2, range2)
                            .isTrue();
                });
    }

    @Test
    final void nullable() {
        final Stream<T> results = Instancio.of(targetClass)
                .generate(selector, gen -> spec(gen).nullable())
                .stream()
                .limit(SAMPLE_SIZE);

        assertThat(results).hasSizeGreaterThan(1).containsNull();
    }

    @Test
    final void minValidation() {
        final InstancioApi<T> api = Instancio.of(targetClass)
                .generate(selector, gen -> spec(gen).min(null));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("'min' must not be null");
    }

    @Test
    final void maxValidation() {
        final InstancioApi<T> api = Instancio.of(targetClass)
                .generate(selector, gen -> spec(gen).max(null));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("'max' must not be null");
    }

    @Test
    final void rangeValidationMin() {
        final InstancioApi<T> api = Instancio.of(targetClass)
                .generate(selector, gen -> spec(gen).range(null, initialMax));

        assertThatThrownBy(api::create)
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContaining("'min' must not be null");
    }

    @Test
    final void rangeValidationMax() {
        final InstancioApi<T> api = Instancio.of(targetClass)
                .generate(selector, gen -> spec(gen).range(initialMin, null));

        assertThatThrownBy(api::create)
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContaining("'max' must not be null");
    }

    @Test
    final void rangeThrowsErrorIfMinIsGreaterThanMax() {
        final String errorRange = String.format("%s, %s", initialMax, initialMin);

        final InstancioApi<T> api = Instancio.of(targetClass)
                .generate(selector, gen -> spec(gen).range(initialMax, initialMin));

        assertThatThrownBy(api::create)
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContaining("invalid 'range(%s)': lower bound must be less than or equal to upper bound", errorRange);
    }

    @SuppressWarnings("unchecked")
    private T asT(final long value) {
        final Class<?> numberType = TypeUtils.getGenericSuperclassTypeArgument(getClass());
        assertThat(numberType).isNotNull();
        return (T) NumberUtils.longConverter(numberType).apply(value);
    }
}
