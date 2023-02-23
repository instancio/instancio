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
import org.instancio.generator.specs.MonthDayGeneratorSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;

import java.time.Month;
import java.time.MonthDay;

public class MonthDayGenerator extends AbstractGenerator<MonthDay> implements MonthDayGeneratorSpec {

    private MonthDay min = MonthDay.of(1, 1);
    private MonthDay max = MonthDay.of(12, 31);

    public MonthDayGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "monthDay()";
    }

    @Override
    public MonthDayGeneratorSpec min(final MonthDay min) {
        this.min = ApiValidator.notNull(min, "'min' must not be null");
        return this;
    }

    @Override
    public MonthDayGeneratorSpec max(final MonthDay max) {
        this.max = ApiValidator.notNull(max, "'max' must not be null");
        return this;
    }

    @Override
    public MonthDayGeneratorSpec range(final MonthDay min, final MonthDay max) {
        this.min = ApiValidator.notNull(min, "MonthDay start must not be null");
        this.max = ApiValidator.notNull(max, "MonthDay end must not be null");
        ApiValidator.isTrue(min.compareTo(max) <= 0,
                "Start must not exceed end: %s, %s", this.min, this.max);
        return this;
    }

    @Override
    protected MonthDay tryGenerateNonNull(final Random random) {
        final int minMonth = min.getMonthValue();
        final int maxMonth = max.getMonthValue();
        final int month = random.intRange(minMonth, maxMonth);

        int minDay = 1;
        int maxDay = 31;

        if (month == minMonth) {
            minDay = min.getDayOfMonth();
            maxDay = Math.max(minDay, max.getDayOfMonth());
        } else if (month == maxMonth) {
            maxDay = max.getDayOfMonth();
        }

        final int day = random.intRange(minDay, maxDay);
        return MonthDay.of(month, Math.min(day, Month.of(month).maxLength()));
    }
}
