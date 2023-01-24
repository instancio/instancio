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
import org.instancio.generator.specs.LocalDateTimeSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.context.Global;

import java.time.LocalDateTime;

import static org.instancio.internal.util.Constants.DEFAULT_MAX;
import static org.instancio.internal.util.Constants.DEFAULT_MIN;
import static org.instancio.internal.util.Constants.ZONE_OFFSET;

public class LocalDateTimeGenerator extends JavaTimeTemporalGenerator<LocalDateTime>
        implements LocalDateTimeSpec {

    private final InstantGenerator delegate;

    public LocalDateTimeGenerator() {
        this(Global.generatorContext());
    }

    public LocalDateTimeGenerator(final GeneratorContext context) {
        super(context,
                LocalDateTime.ofInstant(DEFAULT_MIN, ZONE_OFFSET),
                LocalDateTime.ofInstant(DEFAULT_MAX, ZONE_OFFSET));

        delegate = new InstantGenerator(context);
    }

    @Override
    public String apiMethod() {
        return "localDateTime()";
    }

    @Override
    public LocalDateTimeGenerator past() {
        super.past();
        return this;
    }

    @Override
    public LocalDateTimeGenerator future() {
        super.future();
        return this;
    }

    @Override
    public LocalDateTimeGenerator range(final LocalDateTime start, final LocalDateTime end) {
        super.range(start, end);
        return this;
    }

    @Override
    LocalDateTime getLatestPast() {
        return LocalDateTime.now().minusSeconds(1);
    }

    @Override
    LocalDateTime getEarliestFuture() {
        return LocalDateTime.now().plusMinutes(1);
    }

    @Override
    void validateRange() {
        ApiValidator.isTrue(min.compareTo(max) <= 0, "Start must not exceed end: %s, %s", min, max);
    }

    @Override
    public LocalDateTime generate(final Random random) {
        delegate.range(min.toInstant(ZONE_OFFSET), max.toInstant(ZONE_OFFSET));
        return LocalDateTime.ofInstant(delegate.generate(random), ZONE_OFFSET);
    }
}
