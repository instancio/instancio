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
package org.instancio.internal.generator.time;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class LocalDateGeneratorTest extends TemporalGeneratorSpecTestTemplate<LocalDate> {

    private static final LocalDate START = LocalDate.of(2000, 1, 1);

    private final LocalDateGenerator generator = new LocalDateGenerator(context);

    @Override
    JavaTimeTemporalGenerator<LocalDate> getGenerator() {
        return generator;
    }

    @Override
    String getApiMethod() {
        return "localDate()";
    }

    @Override
    LocalDate getNow() {
        return LocalDate.now();
    }

    @Override
    LocalDate getDefaultMin() {
        return LocalDateGenerator.DEFAULT_MIN;
    }

    @Override
    LocalDate getDefaultMax() {
        return LocalDateGenerator.DEFAULT_MAX;
    }

    @Override
    LocalDate getTemporalMin() {
        return LocalDate.MIN;
    }

    @Override
    LocalDate getTemporalMax() {
        return LocalDate.MAX;
    }

    @Override
    LocalDate getStart() {
        return START;
    }

    @Override
    LocalDate getStartMinusSmallestIncrement() {
        return START.minusDays(1);
    }

    @Override
    LocalDate getStartPlusRandomSmallIncrement() {
        return START.plusDays(random.intRange(1, 100));
    }

    @Override
    LocalDate getStartPlusRandomLargeIncrement() {
        return START.plusYears(random.intRange(1, 1000));
    }

    @Test
    void randomStart() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final LocalDate start = Instancio.create(LocalDate.class);
            assertGeneratedValueIsWithinRange(start, start.plusDays(random.intRange(1, Integer.MAX_VALUE)));
        }
    }
}
