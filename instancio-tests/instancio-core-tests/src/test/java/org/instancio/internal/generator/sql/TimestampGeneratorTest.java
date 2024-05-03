/*
 * Copyright 2022-2024 the original author or authors.
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

import org.instancio.exception.InstancioApiException;
import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimestampGeneratorTest extends AbstractGeneratorTestTemplate<Timestamp, TimestampGenerator> {

    private static final LocalDateTime START = LocalDateTime.of(
            1970, 1, 1, 0, 0, 1, 999999999);

    private static final boolean INCLUSIVE = true;

    private final TimestampGenerator generator = new TimestampGenerator(getGeneratorContext());

    @Override
    protected String getApiMethod() {
        return "timestamp()";
    }

    @Override
    protected TimestampGenerator generator() {
        return generator;
    }

    @Test
    void smallestAllowedRange() {
        final Timestamp start = Timestamp.valueOf(START);
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
        final Timestamp start = Timestamp.valueOf(START);
        final Timestamp end = Timestamp.valueOf(START.minusNanos(1));

        assertThatThrownBy(() -> generator.range(start, end))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("start must not exceed end");
    }

    @Test
    void min() {
        final Timestamp min = Timestamp.valueOf(START);
        generator.min(min);
        assertThat(generator.generate(random)).isAfterOrEqualTo(min);
    }

    @Test
    void max() {
        final Timestamp max = Timestamp.valueOf(START);
        generator.max(max);
        assertThat(generator.generate(random)).isBeforeOrEqualTo(max);
    }

    @Test
    void range() {
        final Timestamp start = Timestamp.valueOf(START);
        final Timestamp end = Timestamp.valueOf(START.plusYears(1));
        generator.range(start, end);
        assertThat(generator.generate(random)).isBetween(start, end, INCLUSIVE, INCLUSIVE);
    }
}
