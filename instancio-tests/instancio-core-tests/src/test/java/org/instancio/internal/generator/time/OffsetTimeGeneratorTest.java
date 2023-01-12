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

import java.time.OffsetTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OffsetTimeGeneratorTest {

    private static final Settings settings = Settings.create();
    private static final Random random = new DefaultRandom();
    private static final GeneratorContext context = new GeneratorContext(settings, random);
    private static final OffsetTime START = OffsetTime.of(1, 1, 2, 0, ZoneOffset.UTC);

    private final OffsetTimeGenerator generator = new OffsetTimeGenerator(context);

    @Test
    void apiMethod() {
        assertThat(generator.apiMethod()).isEqualTo("offsetTime()");
    }

    @Test
    void smallestAllowedRange() {
        generator.range(START, START);
        assertThat(generator.generate(random)).isEqualTo(START);
    }

    @Test
    void past() {
        generator.past();
        assertThat(generator.generate(random)).isBefore(OffsetTime.now());
    }

    @Test
    void future() {
        generator.future();
        assertThat(generator.generate(random)).isAfter(OffsetTime.now());
    }

    @Test
    void validateRange() {
        final OffsetTime end = START.minusSeconds(1);
        assertThatThrownBy(() -> generator.range(START, end))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Start must not exceed end: 01:01:02Z, 01:01:01Z");
    }

    @Test
    void range() {
        final OffsetTime max = START.plusMinutes(1);
        generator.range(START, max);
        assertThat(generator.generate(random)).isBetween(START, max);
    }
}
