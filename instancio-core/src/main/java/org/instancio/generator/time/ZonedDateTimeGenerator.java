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

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.ApiValidator;

import java.time.Instant;
import java.time.ZonedDateTime;

import static java.time.ZoneOffset.UTC;

public class ZonedDateTimeGenerator extends AbstractTemporalGenerator<ZonedDateTime> {

    public ZonedDateTimeGenerator(final GeneratorContext context) {
        super(context,
                ZonedDateTime.ofInstant(TemporalGeneratorSpec.DEFAULT_MIN, UTC),
                ZonedDateTime.ofInstant(TemporalGeneratorSpec.DEFAULT_MAX, UTC));
    }

    @Override
    ZonedDateTime getLatestPast() {
        return ZonedDateTime.now().minusSeconds(1);
    }

    @Override
    ZonedDateTime getEarliestFuture() {
        return ZonedDateTime.now().plusMinutes(1);
    }

    @Override
    void validateRange() {
        ApiValidator.isTrue(min.compareTo(max) <= 0, "Start must not exceed end: %s, %s", min, max);
    }

    /**
     * {@inheritDoc}
     * <p>
     * There must be at least 1-millisecond (1,000,000 nanoseconds) difference
     * between the start and end dates, or else an error will be thrown.
     */
    @Override
    public TemporalGeneratorSpec<ZonedDateTime> range(final ZonedDateTime start, final ZonedDateTime end) {
        return super.range(start, end);
    }

    @Override
    public ZonedDateTime generate(final Random random) {
        final Instant instant = Instant.ofEpochMilli(random.longRange(
                min.toInstant().toEpochMilli(),
                max.toInstant().toEpochMilli()));

        return ZonedDateTime.ofInstant(instant, UTC);
    }
}
