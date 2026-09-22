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

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.OffsetDateTimeSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.util.Constants;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;

public class OffsetDateTimeGenerator extends JavaTimeTemporalGenerator<OffsetDateTime>
        implements OffsetDateTimeSpec {

    private static final ZoneOffset ZONE_OFFSET = Constants.ZONE_OFFSET;
    static final OffsetDateTime DEFAULT_MIN = Constants.DEFAULT_MIN.atOffset(ZONE_OFFSET);
    static final OffsetDateTime DEFAULT_MAX = Constants.DEFAULT_MAX.atOffset(ZONE_OFFSET);

    public OffsetDateTimeGenerator(final GeneratorContext context) {
        super(context, DEFAULT_MIN, DEFAULT_MAX);
    }

    @Override
    public String apiMethod() {
        return "offsetDateTime()";
    }

    @Override
    public OffsetDateTimeGenerator past() {
        super.past();
        return this;
    }

    @Override
    public OffsetDateTimeGenerator future() {
        super.future();
        return this;
    }

    @Override
    public OffsetDateTimeGenerator min(final OffsetDateTime min) {
        super.min(min);
        return this;
    }

    @Override
    public OffsetDateTimeGenerator max(final OffsetDateTime max) {
        super.max(max);
        return this;
    }

    @Override
    public OffsetDateTimeGenerator range(final OffsetDateTime min, final OffsetDateTime max) {
        super.range(min, max);
        return this;
    }

    @Override
    public OffsetDateTimeGenerator truncatedTo(final TemporalUnit unit) {
        super.truncatedTo(unit);
        return this;
    }

    @Override
    public OffsetDateTimeGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    OffsetDateTime getLatestPast() {
        return OffsetDateTime.now(ZONE_OFFSET).minusSeconds(1);
    }

    @Override
    OffsetDateTime getEarliestFuture() {
        return OffsetDateTime.now(ZONE_OFFSET).plusMinutes(1);
    }

    @Override
    void validateRange() {
        ApiValidator.validateStartEnd(min, max);
    }

    @Override
    Instant toStartInstant(final OffsetDateTime value) {
        return value.toInstant();
    }

    @Override
    Instant firstUtcInstant() {
        return LOCAL_DATE_TIME_FIRST_INSTANT;
    }

    @Override
    Instant lastUtcInstant() {
        return LOCAL_DATE_TIME_LAST_INSTANT;
    }

    @Override
    OffsetDateTime fromInstant(final Instant instant, final ZoneOffset offset) {
        final OffsetDateTime result = instant.atOffset(offset);
        return truncateTo == null ? result : result.truncatedTo(truncateTo);
    }
}
