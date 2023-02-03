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
import org.instancio.generator.specs.OffsetTimeSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.context.Global;

import java.time.OffsetTime;
import java.time.ZoneOffset;

public class OffsetTimeGenerator extends JavaTimeTemporalGenerator<OffsetTime>
        implements OffsetTimeSpec {

    private static final int MAX_NANO = 999_999_999;
    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;

    public OffsetTimeGenerator() {
        this(Global.generatorContext());
    }

    public OffsetTimeGenerator(final GeneratorContext context) {
        super(context,
                OffsetTime.of(0, 0, 0, 0, ZONE_OFFSET),
                OffsetTime.of(23, 59, 59, MAX_NANO, ZONE_OFFSET));
    }

    @Override
    public String apiMethod() {
        return "offsetTime()";
    }

    @Override
    public OffsetTimeGenerator past() {
        super.past();
        return this;
    }

    @Override
    public OffsetTimeGenerator future() {
        super.future();
        return this;
    }

    @Override
    public OffsetTimeGenerator range(final OffsetTime start, final OffsetTime end) {
        super.range(start, end);
        return this;
    }

    @Override
    public OffsetTimeGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    OffsetTime getLatestPast() {
        return OffsetTime.now(ZONE_OFFSET).minusSeconds(1);
    }

    @Override
    OffsetTime getEarliestFuture() {
        return OffsetTime.now(ZONE_OFFSET).plusMinutes(1);
    }

    @Override
    void validateRange() {
        ApiValidator.isTrue(min.compareTo(max) <= 0, "Start must not exceed end: %s, %s", min, max);
    }

    @Override
    public OffsetTime generateNonNullValue(final Random random) {
        int hour = random.intRange(min.getHour(), max.getHour());
        int minute = random.intRange(min.getMinute(), max.getMinute());
        int second = random.intRange(min.getSecond(), max.getSecond());
        int nano = random.intRange(min.getNano(), max.getNano());
        return OffsetTime.of(hour, minute, second, nano, ZONE_OFFSET);
    }
}
