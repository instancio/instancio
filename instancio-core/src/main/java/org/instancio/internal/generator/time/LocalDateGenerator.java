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
import org.instancio.generator.specs.LocalDateSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.util.Constants;

import java.time.LocalDate;

import static java.time.temporal.ChronoField.EPOCH_DAY;

public class LocalDateGenerator extends JavaTimeTemporalGenerator<LocalDate>
        implements LocalDateSpec {

    static final LocalDate DEFAULT_MIN = Constants.DEFAULT_MIN.toLocalDate();
    static final LocalDate DEFAULT_MAX = Constants.DEFAULT_MAX.toLocalDate();

    public LocalDateGenerator(final GeneratorContext context) {
        super(context, DEFAULT_MIN, DEFAULT_MAX);
    }

    @Override
    public String apiMethod() {
        return "localDate()";
    }

    @Override
    public LocalDateGenerator past() {
        super.past();
        return this;
    }

    @Override
    public LocalDateGenerator future() {
        super.future();
        return this;
    }

    @Override
    public LocalDateGenerator min(final LocalDate min) {
        super.min(min);
        return this;
    }

    @Override
    public LocalDateGenerator max(final LocalDate max) {
        super.max(max);
        return this;
    }

    @Override
    public LocalDateGenerator range(final LocalDate min, final LocalDate max) {
        super.range(min, max);
        return this;
    }

    @Override
    public LocalDateGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    LocalDate getLatestPast() {
        return LocalDate.now().minusDays(1);
    }

    @Override
    LocalDate getEarliestFuture() {
        return LocalDate.now().plusDays(1);
    }

    @Override
    void validateRange() {
        ApiValidator.validateStartEnd(min, max);
    }

    @Override
    public LocalDate tryGenerateNonNull(final Random random) {
        return LocalDate.ofEpochDay(random.longRange(
                min.getLong(EPOCH_DAY),
                max.getLong(EPOCH_DAY)));
    }
}
