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
import org.instancio.internal.context.Global;

import java.time.LocalTime;

public class LocalTimeGenerator extends JavaTimeTemporalGenerator<LocalTime>
        implements LocalTimeSpec {

    public LocalTimeGenerator() {
        this(Global.generatorContext());
    }

    public LocalTimeGenerator(final GeneratorContext context) {
        super(context,
                LocalTime.of(0, 0, 0),
                LocalTime.of(23, 59, 59));
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
        return LocalTime.now().minusSeconds(1);
    }

    @Override
    LocalTime getEarliestFuture() {
        return LocalTime.now().plusMinutes(1);
    }

    @Override
    void validateRange() {
        ApiValidator.isTrue(min.compareTo(max) <= 0, "Start must not exceed end: %s, %s", min, max);
    }

    @Override
    public LocalTime generateNonNullValue(final Random random) {
        return LocalTime.ofNanoOfDay(random.longRange(
                min.toNanoOfDay(),
                max.toNanoOfDay()));
    }
}
