/*
 * Copyright 2022 the original author or authors.
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
import org.instancio.internal.ApiValidator;

import java.time.YearMonth;

public class YearMonthGenerator extends JavaTimeTemporalGenerator<YearMonth> {

    public YearMonthGenerator(final GeneratorContext context) {
        super(context,
                YearMonth.of(1970, 1),
                YearMonth.now().plusYears(50));
    }

    @Override
    public String apiMethod() {
        return "yearMonth()";
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
    public YearMonth generate(final Random random) {
        final int minMonth = min.getYear() * 12 + min.getMonthValue() - 1;
        final int maxMonth = max.getYear() * 12 + max.getMonthValue() - 1;
        final int result = random.intRange(minMonth, maxMonth);
        final int year = result / 12;
        final int month = result - year * 12 + 1;
        return YearMonth.of(year, month);
    }
}
