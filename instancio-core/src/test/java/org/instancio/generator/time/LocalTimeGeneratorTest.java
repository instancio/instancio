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

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocalTimeGeneratorTest {

    private static final Settings settings = Settings.create();
    private static final Random random = new DefaultRandom();
    private static final GeneratorContext context = new GeneratorContext(settings, random);

    private final LocalTimeGenerator generator = new LocalTimeGenerator(context);

    @Test
    void past() {
        generator.past();
        assertThat(generator.generate(random)).isBefore(LocalTime.now());
    }

    @Test
    void future() {
        generator.future();
        assertThat(generator.generate(random)).isAfter(LocalTime.now());
    }

    @Test
    void validateRange() {
        final LocalTime time = LocalTime.of(0, 0, 0);

        assertThatThrownBy(() -> generator.range(time, time))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Start time must be before end time: 00:00, 00:00");

        generator.range(time, time.plus(1, ChronoUnit.NANOS)); // no error
    }

    @Test
    void range() {
        final LocalTime min = LocalTime.now().plusMinutes(5);
        final LocalTime max = min.plusMinutes(1);
        generator.range(min, max);
        assertThat(generator.generate(random)).isBetween(min, max);
    }
}
