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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class LocalTimeGeneratorTest extends TemporalGeneratorSpecTestTemplate<LocalTime> {

    private static final LocalTime START = LocalTime.of(1, 1, 2);

    private final LocalTimeGenerator generator = new LocalTimeGenerator(context);

    @Override
    JavaTimeTemporalGenerator<LocalTime> getGenerator() {
        return generator;
    }

    @Override
    String getApiMethod() {
        return "localTime()";
    }

    @Override
    LocalTime getNow() {
        return LocalTime.now();
    }

    @Override
    LocalTime getDefaultMin() {
        return LocalTime.MIN;
    }

    @Override
    LocalTime getDefaultMax() {
        return LocalTime.MAX;
    }

    @Override
    LocalTime getTemporalMin() {
        return LocalTime.MIN;
    }

    @Override
    LocalTime getTemporalMax() {
        return LocalTime.MAX;
    }

    @Override
    LocalTime getStart() {
        return START;
    }

    @Override
    LocalTime getStartMinusSmallestIncrement() {
        return START.minusNanos(1);
    }

    @Override
    LocalTime getStartPlusRandomSmallIncrement() {
        return START.plusNanos(random.intRange(1, 1000));
    }

    @Override
    LocalTime getStartPlusRandomLargeIncrement() {
        // (start + increment) should not cross over the 24-hour mark
        return START.plusHours(random.intRange(1, 22));
    }

    @Nested
    class OverflowTest {

        @ValueSource(ints = {0, 1, 999_999_999})
        @ParameterizedTest
        void pastOverflow(final int nanos) {
            final LocalTimeGenerator gen = Mockito.spy(generator);
            when(gen.getNow()).thenReturn(LocalTime.of(0, 0, 0, nanos));

            final LocalTime result = gen.past().generate(random);

            assertThat(result).isEqualTo(LocalTime.MIN);
        }

        @ValueSource(ints = {0, 1, 999_999_999})
        @ParameterizedTest
        void futureOverflow(final int nanos) {
            final LocalTimeGenerator gen = Mockito.spy(generator);
            when(gen.getNow()).thenReturn(LocalTime.of(23, 59, 59, nanos));

            final LocalTime result = gen.future().generate(random);

            assertThat(result).isEqualTo(LocalTime.MAX);
        }
    }
}
