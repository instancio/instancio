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
package org.instancio.internal.generator.time;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.LocalTimeSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.support.Global;
import org.jetbrains.annotations.VisibleForTesting;

import java.time.LocalTime;

public class LocalTimeGenerator extends JavaTimeTemporalGenerator<LocalTime>
        implements LocalTimeSpec {

    private static final int CUT_OFF_BUFFER_MINUTES = 1;

    public LocalTimeGenerator() {
        this(Global.generatorContext());
    }

    public LocalTimeGenerator(final GeneratorContext context) {
        super(context, LocalTime.MIN, LocalTime.MAX);
    }

    @Override
    public String apiMethod() {
        return "localTime()";
    }

    @Override
    public LocalTimeGenerator past() {
        super.past();
        return this;
    }

    @Override
    public LocalTimeGenerator future() {
        super.future();
        return this;
    }

    @Override
    public LocalTimeGenerator range(final LocalTime start, final LocalTime end) {
        super.range(start, end);
        return this;
    }

    @Override
    public LocalTimeGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    LocalTime getLatestPast() {
        final LocalTime now = getNow();
        final LocalTime latestPast = now.minusMinutes(CUT_OFF_BUFFER_MINUTES);

        // Handle overflow into previous day
        return latestPast.isAfter(now) ? LocalTime.MIN : latestPast;
    }

    @Override
    LocalTime getEarliestFuture() {
        final LocalTime now = getNow();
        final LocalTime earliestFuture = now.plusMinutes(CUT_OFF_BUFFER_MINUTES);

        // Handle overflow into next day
        return earliestFuture.isBefore(now) ? LocalTime.MAX : earliestFuture;
    }

    @VisibleForTesting
    LocalTime getNow() {
        return LocalTime.now();
    }

    @Override
    void validateRange() {
        ApiValidator.validateStartEnd(min, max);
    }

    @Override
    protected LocalTime tryGenerateNonNull(final Random random) {
        return LocalTime.ofNanoOfDay(random.longRange(
                min.toNanoOfDay(),
                max.toNanoOfDay()));
    }
}
