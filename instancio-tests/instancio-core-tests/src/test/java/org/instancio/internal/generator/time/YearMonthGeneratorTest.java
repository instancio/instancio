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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class YearMonthGeneratorTest extends TemporalGeneratorSpecTestTemplate<YearMonth> {

    private static final YearMonth START = YearMonth.of(1970, Month.JANUARY);

    private final YearMonthGenerator generator = new YearMonthGenerator(context);

    @Override
    JavaTimeTemporalGenerator<YearMonth> getGenerator() {
        return generator;
    }

    @Override
    String getApiMethod() {
        return "yearMonth()";
    }

    @Override
    YearMonth getNow() {
        return YearMonth.now();
    }

    @Override
    YearMonth getDefaultMin() {
        return YearMonthGenerator.DEFAULT_MIN;
    }

    @Override
    YearMonth getDefaultMax() {
        return YearMonthGenerator.DEFAULT_MAX;
    }

    @Override
    YearMonth getStart() {
        return START;
    }

    @Override
    YearMonth getStartMinusSmallestIncrement() {
        return START.minusMonths(1);
    }

    @Override
    YearMonth getStartPlusRandomSmallIncrement() {
        return START.plusMonths(random.intRange(1, 10));
    }

    @Override
    YearMonth getStartPlusRandomLargeIncrement() {
        return START.plusYears(random.intRange(1, 10));
    }

    @MethodSource("temporalRanges")
    @ParameterizedTest(name = "{index}: {0} - {1}")
    void range(final YearMonth min, final YearMonth max) {
        generator.range(min, max);
        assertThat(generator.generate(random)).isBetween(min, max);
    }

    private static Stream<Arguments> temporalRanges() {
        final List<Arguments> args = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            final YearMonth start = YearMonth.of(2020, 11).plusMonths(i);
            for (int j = 1; j <= 12; j++) {
                final YearMonth end = start.plusMonths(j);
                args.add(Arguments.of(start, end));
            }
        }
        return args.stream();
    }
}
