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
import org.instancio.generator.specs.YearMonthSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.util.Constants;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneOffset;

import static org.instancio.internal.util.Constants.ZONE_OFFSET;

public class YearMonthGenerator extends JavaTimeTemporalGenerator<YearMonth>
        implements YearMonthSpec {

    static final YearMonth DEFAULT_MIN = YearMonth.from(Constants.DEFAULT_MIN.toLocalDate());
    static final YearMonth DEFAULT_MAX = YearMonth.from(Constants.DEFAULT_MAX.toLocalDate());

    public YearMonthGenerator(final GeneratorContext context) {
        super(context, DEFAULT_MIN, DEFAULT_MAX);
    }

    @Override
    public String apiMethod() {
        return "yearMonth()";
    }

    @Override
    public YearMonthGenerator past() {
        super.past();
        return this;
    }

    @Override
    public YearMonthGenerator future() {
        super.future();
        return this;
    }

    @Override
    public YearMonthGenerator min(final YearMonth min) {
        super.min(min);
        return this;
    }

    @Override
    public YearMonthGenerator max(final YearMonth max) {
        super.max(max);
        return this;
    }

    @Override
    public YearMonthGenerator range(final YearMonth min, final YearMonth max) {
        super.range(min, max);
        return this;
    }

    @Override
    public YearMonthGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    YearMonth getLatestPast() {
        return YearMonth.now().minusMonths(1);
    }

    @Override
    YearMonth getEarliestFuture() {
        return YearMonth.now().plusMonths(1);
    }

    @Override
    void validateRange() {
        ApiValidator.validateStartEnd(min, max);
    }

    @Override
    Instant toStartInstant(final YearMonth value) {
        return value.atDay(1).atStartOfDay().toInstant(ZONE_OFFSET);
    }

    @Override
    Instant toEndInstant(final YearMonth value) {
        return value.atEndOfMonth().atTime(LocalTime.MAX).toInstant(ZONE_OFFSET);
    }

    @Override
    YearMonth fromInstant(final Instant instant, final ZoneOffset offset) {
        return YearMonth.from(LocalDate.ofInstant(instant, offset));
    }
}
