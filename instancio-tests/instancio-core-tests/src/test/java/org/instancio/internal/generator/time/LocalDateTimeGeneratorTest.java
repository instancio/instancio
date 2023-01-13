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

class LocalDateTimeGeneratorTest extends TemporalGeneratorSpecTestTemplate<LocalDateTime> {

    private static final LocalDateTime START = LocalDateTime.of(1970, 1, 1, 0, 0, 1, 999999999);

    private final LocalDateTimeGenerator generator = new LocalDateTimeGenerator(context);

    @Override
    JavaTimeTemporalGenerator<LocalDateTime> getGenerator() {
        return generator;
    }

    @Override
    String getApiMethod() {
        return "localDateTime()";
    }

    @Override
    LocalDateTime getNow() {
        return LocalDateTime.now();
    }

    @Override
    LocalDateTime getTemporalMin() {
        return LocalDateTime.MIN;
    }

    @Override
    LocalDateTime getTemporalMax() {
        return LocalDateTime.MAX;
    }

    @Override
    LocalDateTime getStart() {
        return START;
    }

    @Override
    LocalDateTime getStartMinusSmallestIncrement() {
        return START.minusNanos(1);
    }

    @Override
    LocalDateTime getStartPlusRandomSmallIncrement() {
        return START.plusNanos(random.intRange(1, 1000));
    }

    @Override
    LocalDateTime getStartPlusRandomLargeIncrement() {
        return START.plusYears(random.intRange(1, 1000));
    }

    @Test
    void randomStart() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final LocalDateTime start = Instancio.create(LocalDateTime.class);
            assertGeneratedValueIsWithinRange(start, start.plusSeconds(random.intRange(1, Integer.MAX_VALUE)));
        }
    }
}
