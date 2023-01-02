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

import org.instancio.Random;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.random.DefaultRandom;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocalTimeGeneratorTest {

    private static final Settings settings = Settings.create();
    private static final Random random = new DefaultRandom();
    private static final GeneratorContext context = new GeneratorContext(settings, random);
    private static final LocalTime START = LocalTime.of(1, 1, 2);

    private final LocalTimeGenerator generator = new LocalTimeGenerator(context);

    @Test
    void apiMethod() {
        assertThat(generator.apiMethod()).isEqualTo("localTime()");
    }

    @Test
    void smallestAllowedRange() {
        generator.range(START, START);
        assertThat(generator.generate(random)).isEqualTo(START);
    }

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
        assertThatThrownBy(() -> generator.range(START, START.minusSeconds(1)))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Start must not exceed end: 01:01:02, 01:01:01");
    }

    @Test
    void range() {
        final LocalTime max = START.plusMinutes(1);
        generator.range(START, max);
        assertThat(generator.generate(random)).isBetween(START, max);
    }
}
