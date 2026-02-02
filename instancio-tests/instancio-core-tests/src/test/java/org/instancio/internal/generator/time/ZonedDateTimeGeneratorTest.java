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

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

class ZonedDateTimeGeneratorTest extends TemporalGeneratorSpecTestTemplate<ZonedDateTime> {

    private static final ZonedDateTime START = ZonedDateTime.of(LocalDateTime.of(
            1970, 1, 1, 0, 0, 1, 999999999), ZoneOffset.UTC);

    private final ZonedDateTimeGenerator generator = new ZonedDateTimeGenerator(context);

    @Override
    JavaTimeTemporalGenerator<ZonedDateTime> getGenerator() {
        return generator;
    }

    @Override
    String getApiMethod() {
        return "zonedDateTime()";
    }

    @Override
    ZonedDateTime getNow() {
        return ZonedDateTime.now();
    }

    @Override
    ZonedDateTime getDefaultMin() {
        return ZonedDateTimeGenerator.DEFAULT_MIN;
    }

    @Override
    ZonedDateTime getDefaultMax() {
        return ZonedDateTimeGenerator.DEFAULT_MAX;
    }

    @Override
    ZonedDateTime getStart() {
        return START;
    }

    @Override
    ZonedDateTime getStartMinusSmallestIncrement() {
        return START.minusNanos(1);
    }

    @Override
    ZonedDateTime getStartPlusRandomSmallIncrement() {
        return START.plusNanos(random.intRange(1, 1000));
    }

    @Override
    ZonedDateTime getStartPlusRandomLargeIncrement() {
        return START.plusYears(random.intRange(1, 10));
    }

    @Test
    void randomStart() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final ZonedDateTime start = Instancio.create(ZonedDateTime.class);
            assertGeneratedValueIsWithinRange(start, start.plusDays(random.intRange(1, Integer.MAX_VALUE)));
        }
    }
}
