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
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

class OffsetDateTimeGeneratorTest extends TemporalGeneratorSpecTestTemplate<OffsetDateTime> {

    private static final OffsetDateTime START = OffsetDateTime.of(LocalDateTime.of(
            1970, 1, 1, 0, 0, 1, 999999999), ZoneOffset.UTC);

    private final OffsetDateTimeGenerator generator = new OffsetDateTimeGenerator(context);

    @Override
    JavaTimeTemporalGenerator<OffsetDateTime> getGenerator() {
        return generator;
    }

    @Override
    String getApiMethod() {
        return "offsetDateTime()";
    }

    @Override
    OffsetDateTime getNow() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }

    @Override
    OffsetDateTime getStart() {
        return START;
    }

    @Override
    OffsetDateTime getStartMinusSmallestIncrement() {
        return START.minusNanos(1);
    }

    @Override
    OffsetDateTime getStartPlusRandomSmallIncrement() {
        return START.plusNanos(random.intRange(1, 1000));
    }

    @Override
    OffsetDateTime getStartPlusRandomLargeIncrement() {
        return START.plusYears(random.intRange(1, 10));
    }

    @Test
    void randomStart() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final OffsetDateTime start = Instancio.create(OffsetDateTime.class);
            assertGeneratedValueIsWithinRange(start, start.plusDays(random.intRange(1, Integer.MAX_VALUE)));
        }
    }
}
