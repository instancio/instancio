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
import org.instancio.generator.specs.YearMonthSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.util.Constants;
import org.instancio.support.Global;

import java.time.YearMonth;

public class YearMonthGenerator extends JavaTimeTemporalGenerator<YearMonth>
        implements YearMonthSpec {

    static final YearMonth DEFAULT_MIN = YearMonth.from(Constants.DEFAULT_MIN.toLocalDate());
    static final YearMonth DEFAULT_MAX = YearMonth.from(Constants.DEFAULT_MAX.toLocalDate());

    public YearMonthGenerator() {
        this(Global.generatorContext());
    }

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
    public YearMonthGenerator range(final YearMonth start, final YearMonth end) {
        super.range(start, end);
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
        ApiValidator.isTrue(min.compareTo(max) <= 0, "Start must not exceed end: %s, %s", min, max);
    }

    @Override
    protected YearMonth tryGenerateNonNull(final Random random) {
        final int minMonth = min.getYear() * 12 + min.getMonthValue() - 1;
        final int maxMonth = max.getYear() * 12 + max.getMonthValue() - 1;
        final int result = random.intRange(minMonth, maxMonth);
        final int year = result / 12;
        final int month = result - year * 12 + 1;
        return YearMonth.of(year, month);
    }
}
