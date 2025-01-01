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
package org.instancio.internal.generator.time;

import org.instancio.Random;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.GeneratorContext;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.temporal.Temporal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

abstract class TemporalGeneratorSpecTestTemplate<T extends Temporal & Comparable<? super T>> {
    protected final int SAMPLE_SIZE = 100_000;
    protected final Random random = new DefaultRandom();
    protected final GeneratorContext context = new GeneratorContext(Settings.create(), random);

    private JavaTimeTemporalGenerator<T> generator;

    @BeforeEach
    void setUp() {
        generator = getGenerator();
    }

    abstract JavaTimeTemporalGenerator<T> getGenerator();

    abstract String getApiMethod();

    abstract T getNow();

    // only for temporal types that define one, e.g. Instant.MIN
    T getTemporalMin() {
        return null;
    }

    T getTemporalMax() {
        return null;
    }

    /**
     * Returns a non-random start value that is always the same.
     */
    abstract T getStart();

    /**
     * Returns {@link #getStart()} minus the smallest
     * unit increment for this temporal type.
     */
    abstract T getStartMinusSmallestIncrement();

    /**
     * Returns {@link #getStart()} plus a random small increment.
     */
    abstract T getStartPlusRandomSmallIncrement();

    /**
     * Returns {@link #getStart()} plus a random large increment.
     */
    abstract T getStartPlusRandomLargeIncrement();

    /**
     * Returns the default 'min' value used by the generator.
     */
    abstract T getDefaultMin();

    /**
     * Returns the default 'max' value used by the generator.
     */
    abstract T getDefaultMax();

    @Test
    final void apiMethod() {
        assertThat(generator.apiMethod()).isEqualTo(getApiMethod());
    }

    @Test
    final void past() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            generator.past();
            final T now = getNow();
            final T result = generator.generate(random);
            assertThat(result).isLessThanOrEqualTo(now);
        }
    }

    @Test
    void future() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            generator.future();
            final T result = generator.generate(random);
            assertThat(result).isGreaterThanOrEqualTo(getNow());
        }
    }

    @Test
    void min() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final T min = generator.generate(random);
            generator.min(min);
            final T result = generator.generate(random);
            assertThat(result).isGreaterThanOrEqualTo(min);
        }
    }

    @Test
    void max() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final T max = generator.generate(random);
            generator.max(max);
            final T result = generator.generate(random);
            assertThat(result).isLessThanOrEqualTo(max);
        }
    }

    @Test
    final void smallestAllowedMinMax() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final T start = getStart();
            generator.min(start).max(start);
            assertThat(generator.generate(random)).isEqualTo(start);
        }
    }

    @Test
    final void smallestAllowedRange() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final T start = getStart();
            generator.range(start, start);
            assertThat(generator.generate(random)).isEqualTo(start);
        }
    }

    @Test
    final void rangeWithTemporalMin() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final T min = getTemporalMin();
            if (min != null) assertGeneratedValueIsWithinRange(min, min);
        }
    }

    @Test
    final void rangeWithTemporalMax() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final T max = getTemporalMax();
            if (max != null) assertGeneratedValueIsWithinRange(max, max);
        }
    }

    @Test
    final void smallRange() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            assertGeneratedValueIsWithinRange(getStart(), getStartPlusRandomSmallIncrement());
        }
    }

    @Test
    final void bigRange() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            assertGeneratedValueIsWithinRange(getStart(), getStartPlusRandomLargeIncrement());
        }
    }

    @Test
    final void defaultRange() {
        final T defaultMin = getDefaultMin();
        final T defaultMax = getDefaultMax();

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final T result = generator.generate(random);
            assertThat(result).isGreaterThanOrEqualTo(defaultMin);
            assertThat(result).isLessThanOrEqualTo(defaultMax);
        }
    }

    @Test
    final void validateRange() {
        final T start = getStart();
        final T slightlyBeforeStart = getStartMinusSmallestIncrement();
        assertThatThrownBy(() -> generator.range(start, slightlyBeforeStart))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("start must not exceed end");
    }

    @Test
    final void nullable() {
        generator.nullable();
        assertThat(Stream.generate(() -> generator.generate(random))
                .limit(SAMPLE_SIZE)).containsNull();
    }

    protected final void assertGeneratedValueIsWithinRange(final T start, final T end) {
        generator.range(start, end);
        final T result = generator.generate(random);
        assertThat(result).isGreaterThanOrEqualTo(start);
        assertThat(result).isLessThanOrEqualTo(end);
    }
}
