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
package org.instancio.internal.generator.time;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.TemporalGeneratorSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

abstract class JavaTimeTemporalGenerator<T extends Temporal> extends AbstractGenerator<T>
        implements TemporalGeneratorSpec<T> {

    private static final int MAX_NANO = 999_999_999;

    static final Instant LOCAL_DATE_TIME_FIRST_INSTANT = LocalDateTime.MIN.toInstant(ZoneOffset.UTC);
    static final Instant LOCAL_DATE_TIME_LAST_INSTANT = LocalDateTime.MAX.toInstant(ZoneOffset.UTC);

    private final T defaultMin;
    private final T defaultMax;

    protected T min;
    protected T max;
    protected @Nullable TemporalUnit truncateTo;

    JavaTimeTemporalGenerator(final GeneratorContext context, final T min, final T max) {
        super(context);
        this.defaultMin = min;
        this.defaultMax = max;
        this.min = min;
        this.max = max;
    }

    abstract T getLatestPast();

    abstract T getEarliestFuture();

    abstract void validateRange();

    abstract Instant toStartInstant(T value);

    /**
     * Types spanning a period, such as {@code Year}, return the last instant of the
     * period, otherwise the period of {@code max} could never be generated.
     */
    Instant toEndInstant(final T value) {
        return toStartInstant(value);
    }

    /**
     * The instants UTC can express for this type. An instant outside them takes the offset
     * of the bound on its side: the bound is expressed in that offset and the instant lies
     * between the bounds, so it always fits.
     */
    Instant firstUtcInstant() {
        return Instant.MIN;
    }

    Instant lastUtcInstant() {
        return Instant.MAX;
    }

    abstract T fromInstant(Instant instant, ZoneOffset offset);

    @Override
    protected T tryGenerateNonNull(final Random random) {
        final Instant start = toStartInstant(min);
        final Instant end = toEndInstant(max);
        final long sec = random.longRange(start.getEpochSecond(), end.getEpochSecond());
        final int minNano = sec == start.getEpochSecond() ? start.getNano() : 0;
        final int maxNano = sec == end.getEpochSecond() ? end.getNano() : MAX_NANO;
        final Instant instant = Instant.ofEpochSecond(sec, random.intRange(minNano, maxNano));
        return fromInstant(instant, offsetFor(instant));
    }

    private ZoneOffset offsetFor(final Instant instant) {
        if (instant.isBefore(firstUtcInstant())) return ZoneOffset.from(min);
        if (instant.isAfter(lastUtcInstant())) return ZoneOffset.from(max);
        return ZoneOffset.UTC;
    }

    @Override
    public TemporalGeneratorSpec<T> past() {
        min = defaultMin;
        max = getLatestPast();
        return this;
    }

    @Override
    public TemporalGeneratorSpec<T> future() {
        min = getEarliestFuture();
        max = defaultMax;
        return this;
    }

    @Override
    public TemporalGeneratorSpec<T> min(final @NonNull T min) {
        this.min = ApiValidator.notNull(min, "'min' must not be null");
        return this;
    }

    @Override
    public TemporalGeneratorSpec<T> max(final @NonNull T max) {
        this.max = ApiValidator.notNull(max, "'max' must not be null");
        return this;
    }

    @Override
    public TemporalGeneratorSpec<T> range(final @NonNull T min, final @NonNull T max) {
        this.min = ApiValidator.notNull(min, "'min' must not be null");
        this.max = ApiValidator.notNull(max, "'max' must not be null");
        validateRange();
        return this;
    }

    @Override
    public JavaTimeTemporalGenerator<T> nullable() {
        super.nullable();
        return this;
    }

    TemporalGeneratorSpec<T> truncatedTo(final TemporalUnit unit) {
        this.truncateTo = unit;
        return this;
    }
}
