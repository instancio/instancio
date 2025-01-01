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
package org.instancio.internal.generator.time;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.time.OffsetTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class OffsetTimeGeneratorTest extends TemporalGeneratorSpecTestTemplate<OffsetTime> {

    private static final OffsetTime START = OffsetTime.of(1, 1, 2, 0, ZoneOffset.UTC);

    private final OffsetTimeGenerator generator = new OffsetTimeGenerator(context);

    @Override
    JavaTimeTemporalGenerator<OffsetTime> getGenerator() {
        return generator;
    }

    @Override
    String getApiMethod() {
        return "offsetTime()";
    }

    @Override
    OffsetTime getNow() {
        return OffsetTime.now(ZoneOffset.UTC);
    }

    @Override
    OffsetTime getDefaultMin() {
        return OffsetTime.MIN;
    }

    @Override
    OffsetTime getDefaultMax() {
        return OffsetTime.MAX;
    }

    @Override
    OffsetTime getStart() {
        return START;
    }

    @Override
    OffsetTime getStartMinusSmallestIncrement() {
        return START.minusNanos(1);
    }

    @Override
    OffsetTime getStartPlusRandomSmallIncrement() {
        return START.plusNanos(random.intRange(1, 1000));
    }

    @Override
    OffsetTime getStartPlusRandomLargeIncrement() {
        // (start + increment) should not cross over the 24-hour mark
        return START.plusHours(random.intRange(1, 22));
    }

    @Test
    void range() {
        final OffsetTime max = START.plusMinutes(1);
        generator.range(START, max);
        assertThat(generator.generate(random)).isBetween(START, max);
    }


    @Nested
    class OverflowTest {

        @ValueSource(ints = {0, 1, 999_999_999})
        @ParameterizedTest
        void pastOverflow(final int nanos) {
            final OffsetTimeGenerator gen = Mockito.spy(generator);
            when(gen.getNow()).thenReturn(OffsetTime.of(0, 0, 0, nanos, ZoneOffset.UTC));

            final OffsetTime result = gen.past().generate(random);

            assertThat(result).isEqualTo(OffsetTime.MIN.toLocalTime().atOffset(ZoneOffset.UTC));
        }

        @ValueSource(ints = {0, 1, 999_999_999})
        @ParameterizedTest
        void futureOverflow(final int nanos) {
            final OffsetTimeGenerator gen = Mockito.spy(generator);
            when(gen.getNow()).thenReturn(OffsetTime.of(23, 59, 59, nanos, ZoneOffset.UTC));

            final OffsetTime result = gen.future().generate(random);

            assertThat(result).isEqualTo(OffsetTime.MAX.toLocalTime().atOffset(ZoneOffset.UTC));
        }
    }
}
