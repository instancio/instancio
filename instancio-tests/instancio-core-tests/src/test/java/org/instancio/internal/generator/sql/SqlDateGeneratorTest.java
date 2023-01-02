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
package org.instancio.internal.generator.sql;

import org.instancio.Random;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.random.DefaultRandom;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SqlDateGeneratorTest {

    private static final Settings settings = Settings.create();
    private static final Random random = new DefaultRandom();
    private static final GeneratorContext context = new GeneratorContext(settings, random);
    private static final LocalDate START = LocalDate.of(2000, 1, 1);
    private static final boolean INCLUSIVE = true;

    private final SqlDateGenerator generator = new SqlDateGenerator(context);

    @Test
    void apiMethod() {
        assertThat(generator.apiMethod()).isEqualTo("sqlDate()");
    }

    @Test
    void smallestAllowedRange() {
        final Date start = Date.valueOf(START);
        generator.range(start, start);
        assertThat(generator.generate(random)).isEqualTo(start);
    }

    @Test
    void past() {
        generator.past();
        assertThat(generator.generate(random)).isInThePast();
    }

    @Test
    void future() {
        generator.future();
        assertThat(generator.generate(random)).isInTheFuture();
    }

    @Test
    void validateRange() {
        final Date end = Date.valueOf(START.minusDays(1));

        assertThatThrownBy(() -> generator.range(Date.valueOf(START), end))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Start must not exceed end: 2000-01-01, 1999-12-31");
    }

    @Test
    void range() {
        final Date start = Date.valueOf(START);
        final Date end = Date.valueOf(START.plusYears(1));
        generator.range(start, end);
        assertThat(generator.generate(random)).isBetween(start, end, INCLUSIVE, INCLUSIVE);
    }
}
