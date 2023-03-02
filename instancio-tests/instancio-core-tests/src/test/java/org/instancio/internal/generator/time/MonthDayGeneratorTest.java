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

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.GeneratorContext;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Month;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MonthDayGeneratorTest {

    private static final Random random = new DefaultRandom();
    private static final GeneratorContext context = new GeneratorContext(Settings.create(), random);

    private final MonthDayGenerator generator = new MonthDayGenerator(context);

    @Test
    void apiMethod() {
        assertThat(generator.apiMethod()).isEqualTo("monthDay()");
    }

    @Test
    void smallestAllowedRange() {
        generator.range(MonthDay.of(1, 1), MonthDay.of(1, 1));
        assertThat(generator.generate(random)).isEqualTo(MonthDay.of(1, 1));
    }

    @RepeatedTest(100)
    void min() {
        final MonthDay min = Instancio.create(MonthDay.class);
        generator.min(min);
        assertThat(generator.generate(random).compareTo(min)).isNotNegative();
    }

    @RepeatedTest(100)
    void max() {
        final MonthDay max = Instancio.create(MonthDay.class);
        generator.max(max);
        assertThat(generator.generate(random).compareTo(max)).isNotPositive();
    }

    @MethodSource("ranges")
    @ParameterizedTest(name = "{index}: {0} - {1}")
    void range(final MonthDay min, final MonthDay max) {
        generator.range(min, max);
        assertThat(generator.generate(random)).isBetween(min, max);
    }

    @Test
    void validateMinMax() {
        assertThatThrownBy(() -> generator.min(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("'min' must not be null");

        assertThatThrownBy(() -> generator.max(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("'max' must not be null");
    }

    @Test
    void validateRange() {
        final MonthDay start = MonthDay.of(1, 2);
        final MonthDay end = MonthDay.of(1, 1);

        assertThatThrownBy(() -> generator.range(start, end))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Start must not exceed end: --01-02, --01-01");
    }

    private static Stream<Arguments> ranges() {
        final int sampleSize = 500;
        final List<Arguments> args = new ArrayList<>(sampleSize);

        for (int i = 0; i < sampleSize; i++) {
            final Month startMonth = random.oneOf(Month.values());
            final Month endMonth = Month.of(random.intRange(startMonth.getValue(), 12));
            final int startDay = random.intRange(1, startMonth.maxLength());
            final int endDay = startMonth == endMonth
                    ? random.intRange(startDay, endMonth.maxLength())
                    : endMonth.maxLength();

            args.add(Arguments.of(
                    MonthDay.of(startMonth, startDay),
                    MonthDay.of(endMonth, endDay)));
        }
        return args.stream();
    }
}
