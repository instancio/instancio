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
package org.instancio.internal.generator.util;

import org.instancio.exception.InstancioApiException;
import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DateGeneratorTest extends AbstractGeneratorTestTemplate<Date, DateGenerator> {

    private static final Instant START = LocalDateTime.of(1970, 1, 1, 0, 0, 1, 999999999)
            .atZone(ZoneOffset.systemDefault()).toInstant();

    private final DateGenerator generator = new DateGenerator(getGeneratorContext());

    @Override
    protected String getApiMethod() {
        return "date()";
    }

    @Override
    protected DateGenerator generator() {
        return generator;
    }

    @Test
    void smallestAllowedRange() {
        final Date time = Date.from(START);
        generator.range(time, time);
        assertThat(generator.generate(random)).isEqualTo(time);
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
        final Date min = Date.from(START);
        final Date max = Date.from(START.minusNanos(1000000));

        assertThatThrownBy(() -> generator.range(min, max))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("start must not exceed end");
    }

    @Test
    void minMax() {
        final Date min = Date.from(START);
        final Date max = Date.from(START.plusSeconds(10));
        generator.min(min).max(max);
        assertThat(generator.generate(random)).isBetween(min, max);
    }

    @Test
    void range() {
        final Date min = Date.from(START);
        final Date max = Date.from(START.plusSeconds(10));
        generator.range(min, max);
        assertThat(generator.generate(random)).isBetween(min, max);
    }
}
