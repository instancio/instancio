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

import java.time.Instant;

class InstantGeneratorTest extends TemporalGeneratorSpecTestTemplate<Instant> {

    private static final Instant START = Instant.ofEpochSecond(1652369346, 1111111111);
    private static final int FIFTY_YEARS_IN_SECONDS = 60 * 60 * 24 * 365 * 50;

    private final InstantGenerator generator = new InstantGenerator(context);

    @Override
    JavaTimeTemporalGenerator<Instant> getGenerator() {
        return generator;
    }

    @Override
    String getApiMethod() {
        return "instant()";
    }

    @Override
    Instant getNow() {
        return Instant.now();
    }

    @Override
    Instant getDefaultMin() {
        return InstantGenerator.DEFAULT_MIN;
    }

    @Override
    Instant getDefaultMax() {
        return InstantGenerator.DEFAULT_MAX;
    }

    @Override
    Instant getTemporalMin() {
        return Instant.MIN;
    }

    @Override
    Instant getTemporalMax() {
        return Instant.MAX;
    }

    @Override
    Instant getStart() {
        return START;
    }

    @Override
    Instant getStartMinusSmallestIncrement() {
        return START.minusNanos(1);
    }

    @Override
    Instant getStartPlusRandomSmallIncrement() {
        return START.plusNanos(random.intRange(1, 1000));
    }

    @Override
    Instant getStartPlusRandomLargeIncrement() {
        return START.plusSeconds(random.intRange(1, FIFTY_YEARS_IN_SECONDS));
    }

    @Test
    void randomStart() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final Instant start = Instancio.create(Instant.class);
            assertGeneratedValueIsWithinRange(start, start.plusSeconds(random.intRange(1, Integer.MAX_VALUE)));
        }
    }
}
