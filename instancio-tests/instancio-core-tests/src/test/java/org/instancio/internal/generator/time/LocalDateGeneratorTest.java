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

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.random.DefaultRandom;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocalDateGeneratorTest {
    private static final int SAMPLE_SIZE = 1000;
    private static final Settings settings = Settings.create();
    private static final Random random = new DefaultRandom();
    private static final GeneratorContext context = new GeneratorContext(settings, random);
    private static final LocalDate START = LocalDate.of(2000, 1, 1);

    private final LocalDateGenerator generator = new LocalDateGenerator(context);

    @Test
    void apiMethod() {
        assertThat(generator.apiMethod()).isEqualTo("localDate()");
    }

    @Test
    void smallestAllowedRange() {
        generator.range(START, START);
        assertThat(generator.generate(random)).isEqualTo(START);
    }

    @Test
    void past() {
        generator.past();
        assertThat(generator.generate(random)).isBefore(LocalDate.now());
    }

    @Test
    void future() {
        generator.future();
        assertThat(generator.generate(random)).isAfter(LocalDate.now());
    }

    @Test
    void validateRange() {
        assertThatThrownBy(() -> generator.range(START.plusDays(1), START))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Start must not exceed end: 2000-01-02, 2000-01-01");
    }

    @Test
    void smallRange() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            assertResult(START, START.plusDays(8));
        }
    }

    @Test
    void bigRange() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            assertResult(START, START.plusYears(10));
        }
    }

    @Test
    void randomStart() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final LocalDate start = Instancio.create(LocalDate.class);
            assertResult(start, start.plusDays(random.intRange(1, Integer.MAX_VALUE)));
        }
    }

    private void assertResult(final LocalDate start, final LocalDate end) {
        generator.range(start, end);
        assertThat(generator.generate(random)).isBetween(start, end);
    }
}
