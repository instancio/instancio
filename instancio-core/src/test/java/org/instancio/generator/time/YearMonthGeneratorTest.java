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
package org.instancio.generator.time;

import org.instancio.Random;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.random.DefaultRandom;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class YearMonthGeneratorTest {

    private static final Settings settings = Settings.create();
    private static final Random random = new DefaultRandom();
    private static final GeneratorContext context = new GeneratorContext(settings, random);

    private final YearMonthGenerator generator = new YearMonthGenerator(context);

    @Test
    void smallestAllowedRange() {
        generator.range(YearMonth.of(2000, 1), YearMonth.of(2000, 1));
        assertThat(generator.generate(random)).isEqualTo(YearMonth.of(2000, 1));
    }

    @RepeatedTest(100)
    void past() {
        generator.past();
        assertThat(generator.generate(random).isBefore(YearMonth.now())).isTrue();
    }

    @RepeatedTest(100)
    void future() {
        generator.future();
        assertThat(generator.generate(random).isAfter(YearMonth.now())).isTrue();
    }

    @MethodSource("temporalRanges")
    @ParameterizedTest(name = "{index}: {0} - {1}")
    void range(final YearMonth min, final YearMonth max) {
        generator.range(min, max);
        assertThat(generator.generate(random)).isBetween(min, max);
    }

    @Test
    void validateRange() {
        final YearMonth yearMonth = YearMonth.of(1970, 1);

        assertThatThrownBy(() -> generator.range(yearMonth.plusMonths(1), yearMonth))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Start must not exceed end: 1970-02, 1970-01");

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
        return Stream.of(args.toArray(new Arguments[0]));
    }
}
