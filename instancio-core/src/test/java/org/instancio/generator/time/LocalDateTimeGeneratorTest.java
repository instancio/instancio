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
package org.instancio.generator.time;

import org.instancio.Random;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.random.DefaultRandom;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocalDateTimeGeneratorTest {

    private static final Settings settings = Settings.create();
    private static final Random random = new DefaultRandom();
    private static final GeneratorContext context = new GeneratorContext(settings, random);

    private final LocalDateTimeGenerator generator = new LocalDateTimeGenerator(context);

    @Test
    void past() {
        generator.past();
        assertThat(generator.generate(random)).isBefore(LocalDateTime.now());
    }

    @Test
    void future() {
        generator.future();
        assertThat(generator.generate(random)).isAfter(LocalDateTime.now());
    }

    @Test
    void validateRange() {
        final LocalDateTime min = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        final LocalDateTime max = min.plus(1, ChronoUnit.MILLIS);

        generator.range(min, max); // no error with 1 millisecond delta

        assertThatThrownBy(() -> generator.range(min, max.minus(1, ChronoUnit.NANOS)))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Start date must be before end date by at least one millisecond: " +
                        "1970-01-01T00:00, 1970-01-01T00:00:00.000999999");
    }

    @Test
    void range() {
        final LocalDateTime min = LocalDateTime.now().plusMinutes(5);
        final LocalDateTime max = min.plusMinutes(1);
        generator.range(min, max);
        assertThat(generator.generate(random)).isBetween(min, max);
    }
}
