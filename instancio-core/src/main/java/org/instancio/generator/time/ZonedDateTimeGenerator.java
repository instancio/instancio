/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.generator.time;

import org.instancio.generator.GeneratorContext;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.random.RandomProvider;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static java.time.ZoneOffset.UTC;

public class ZonedDateTimeGenerator extends AbstractTemporalGenerator<ZonedDateTime> {

    private static final ZonedDateTime MIN = ZonedDateTime.ofInstant(TemporalGeneratorSpec.DEFAULT_MIN, UTC);
    private static final ZonedDateTime MAX = ZonedDateTime.ofInstant(TemporalGeneratorSpec.DEFAULT_MAX, UTC);

    public ZonedDateTimeGenerator(final GeneratorContext context) {
        super(context, MIN, MAX);
    }

    @Override
    ZonedDateTime now() {
        return ZonedDateTime.now();
    }

    @Override
    ZonedDateTime getEarliestFuture() {
        return ZonedDateTime.now().plusMinutes(1);
    }

    @Override
    void validateRange() {
        ApiValidator.isTrue(ChronoUnit.MILLIS.between(min, max) >= 1,
                "Start date must be before end date by at least one millisecond: %s, %s", min, max);
    }

    /**
     * {@inheritDoc}
     * <p>
     * There must be at least 1-millisecond (1,000,000 nanoseconds) difference
     * between the start and end dates, or else an error will be thrown.
     */
    @Override
    public TemporalGeneratorSpec<ZonedDateTime> range(final ZonedDateTime startInclusive, final ZonedDateTime endExclusive) {
        return super.range(startInclusive, endExclusive);
    }

    @Override
    public ZonedDateTime generate(final RandomProvider random) {
        final Instant instant = Instant.ofEpochMilli(random.longRange(
                min.toInstant().toEpochMilli(),
                max.toInstant().toEpochMilli()));

        return ZonedDateTime.ofInstant(instant, UTC);
    }
}
