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

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class YearGeneratorTest {

    private static final Settings settings = Settings.create();
    private static final Random random = new DefaultRandom();
    private static final GeneratorContext context = new GeneratorContext(settings, random);

    private final YearGenerator generator = new YearGenerator(context);

    @Test
    void apiMethod() {
        assertThat(generator.apiMethod()).isEqualTo("year()");
    }

    @Test
    void smallestAllowedRange() {
        generator.range(Year.of(2000), Year.of(2000));
        assertThat(generator.generate(random)).isEqualTo(Year.of(2000));
    }

    @Test
    void past() {
        generator.past();
        assertThat(generator.generate(random).isBefore(Year.now())).isTrue();
    }

    @Test
    void future() {
        generator.future();
        assertThat(generator.generate(random).isAfter(Year.now())).isTrue();
    }

    @Test
    void validateRange() {
        final Year year = Year.of(1970);

        assertThatThrownBy(() -> generator.range(year.plusYears(1), year))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Start must not exceed end: 1971, 1970");
    }

    @Test
    void range() {
        final Year min = Year.now().plusYears(5);
        final Year max = min.plusYears(1);
        generator.range(min, max);
        assertThat(generator.generate(random)).isBetween(min, max);
    }
}
