/*
 * Copyright 2022 the original author or authors.
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

import org.instancio.Random;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.random.DefaultRandom;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DurationGeneratorTest {

    private static final int SAMPLE_SIZE = 500;
    private static final Random random = new DefaultRandom();
    private final DurationGenerator generator = new DurationGenerator(
            new GeneratorContext(Settings.defaults(), random));

    @Test
    void apiMethod() {
        assertThat(generator.apiMethod()).isEqualTo("duration()");
    }

    @Test
    void defaultDurationSpec() {
        final Duration duration = generator.generate(random);
        assertThat(duration.get(ChronoUnit.NANOS)).isBetween(1L, 1_000_000_000_000_000L);
    }

    @Test
    void allowZero() {
        generator.allowZero();

        final Stream<Duration> durationStream = IntStream.range(0, SAMPLE_SIZE)
                .mapToObj(i -> generator.generate(random));

        assertThat(durationStream).contains(Duration.ZERO);
    }

    @Test
    void customDuration() {
        final long minAmount = 5;
        final long maxAmount = 7;
        generator.of(minAmount, maxAmount, ChronoUnit.SECONDS);
        final Duration duration = generator.generate(random);
        assertThat(duration.get(ChronoUnit.SECONDS)).isBetween(minAmount, maxAmount);
    }

    @Test
    void validation() {
        assertThatThrownBy(() -> generator.of(2, 1, ChronoUnit.NANOS))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Minimum duration amount must be less than or equal to the maximum amount: of(2, 1, Nanos)");

        assertThatThrownBy(() -> generator.of(1, 2, null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Unit must not be null");
    }
}
