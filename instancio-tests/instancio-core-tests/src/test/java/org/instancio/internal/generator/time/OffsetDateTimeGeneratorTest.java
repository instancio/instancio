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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

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
    OffsetDateTime getDefaultMin() {
        return OffsetDateTimeGenerator.DEFAULT_MIN;
    }

    @Override
    OffsetDateTime getDefaultMax() {
        return OffsetDateTimeGenerator.DEFAULT_MAX;
    }

    @Override
    OffsetDateTime getTemporalMin() {
        return OffsetDateTime.MIN;
    }

    @Override
    OffsetDateTime getTemporalMax() {
        return OffsetDateTime.MAX;
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
    @Override
    void future() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            generator.future();
            final OffsetDateTime now = getNow();
            final OffsetDateTime result = generator.generate(random);
            assertThat(result).isAfter(now.toLocalDateTime().atOffset(ZoneOffset.UTC));
        }
    }

    @MethodSource("rangesWithOffsets")
    @ParameterizedTest
    void rangeWithOffsets(final OffsetDateTime min, final OffsetDateTime max) {
        generator.range(min, max);
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            assertThat(generator.generate(random)).isBetween(min, max);
        }
    }

    private static Stream<Arguments> rangesWithOffsets() {
        return Stream.of(
                Arguments.of(OffsetDateTime.parse("2020-01-01T10:00+02:00"), OffsetDateTime.parse("2020-01-01T09:30Z")),
                Arguments.of(OffsetDateTime.parse("2020-01-01T07:00-02:00"), OffsetDateTime.parse("2020-01-01T10:00Z")),
                Arguments.of(OffsetDateTime.MIN, OffsetDateTime.MIN.plusDays(1)),
                Arguments.of(OffsetDateTime.MAX.minusDays(1), OffsetDateTime.MAX));
    }

    @Test
    void randomStart() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final OffsetDateTime start = Instancio.create(OffsetDateTime.class);
            assertGeneratedValueIsWithinRange(start, start.plusSeconds(random.intRange(1, Integer.MAX_VALUE)));
        }
    }
}
