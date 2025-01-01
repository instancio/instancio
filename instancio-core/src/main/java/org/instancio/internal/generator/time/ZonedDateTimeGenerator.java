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
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.ZonedDateTimeSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.util.Constants;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalUnit;

import static org.instancio.internal.util.Constants.ZONE_OFFSET;

public class ZonedDateTimeGenerator extends JavaTimeTemporalGenerator<ZonedDateTime>
        implements ZonedDateTimeSpec {

    static final ZonedDateTime DEFAULT_MIN = Constants.DEFAULT_MIN.atZone(ZONE_OFFSET);
    static final ZonedDateTime DEFAULT_MAX = Constants.DEFAULT_MAX.atZone(ZONE_OFFSET);

    private final InstantGenerator delegate;

    public ZonedDateTimeGenerator(final GeneratorContext context) {
        super(context, DEFAULT_MIN, DEFAULT_MAX);

        delegate = new InstantGenerator(context);
    }

    @Override
    public String apiMethod() {
        return "zonedDateTime()";
    }

    @Override
    public ZonedDateTimeGenerator past() {
        super.past();
        return this;
    }

    @Override
    public ZonedDateTimeGenerator future() {
        super.future();
        return this;
    }

    @Override
    public ZonedDateTimeGenerator min(final ZonedDateTime min) {
        super.min(min);
        return this;
    }

    @Override
    public ZonedDateTimeGenerator max(final ZonedDateTime max) {
        super.max(max);
        return this;
    }

    @Override
    public ZonedDateTimeGenerator range(final ZonedDateTime min, final ZonedDateTime max) {
        super.range(min, max);
        return this;
    }

    @Override
    public ZonedDateTimeGenerator truncatedTo(final TemporalUnit unit) {
        super.truncatedTo(unit);
        return this;
    }

    @Override
    public ZonedDateTimeGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    ZonedDateTime getLatestPast() {
        return ZonedDateTime.now(ZONE_OFFSET).minusSeconds(1);
    }

    @Override
    ZonedDateTime getEarliestFuture() {
        return ZonedDateTime.now(ZONE_OFFSET).plusMinutes(1);
    }

    @Override
    void validateRange() {
        ApiValidator.validateStartEnd(min, max);
    }

    @Override
    public ZonedDateTime tryGenerateNonNull(final Random random) {
        delegate.range(min.toInstant(), max.toInstant());
        final ZonedDateTime result = ZonedDateTime.ofInstant(delegate.tryGenerateNonNull(random), ZONE_OFFSET);
        return truncateTo == null ? result : result.truncatedTo(truncateTo);
    }
}
