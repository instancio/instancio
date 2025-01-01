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

import org.instancio.Random;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.GeneratorContext;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.junit.jupiter.api.Test;

import java.time.Period;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PeriodGeneratorTest {

    private static final Random random = new DefaultRandom();
    private final PeriodGenerator generator = new PeriodGenerator(
            new GeneratorContext(Settings.defaults(), random));

    @Test
    void apiMethod() {
        assertThat(generator.apiMethod()).isEqualTo("period()");
    }

    @Test
    void defaultPeriodSpec() {
        final Period result = generator.generate(random);
        assertThat(result.getDays()).isBetween(1, 365);
        assertThat(result.getMonths()).isZero();
        assertThat(result.getYears()).isZero();
    }

    @Test
    void daysMonthsYears() {
        generator.days(1, 3).months(4, 7).years(8, 10);

        final Period result = generator.generate(random);
        assertThat(result.getDays()).isBetween(1, 3);
        assertThat(result.getMonths()).isBetween(4, 7);
        assertThat(result.getYears()).isBetween(8, 10);
    }

    @Test
    void daysMonthsYearsWithEqualLowerAndUpperBounds() {
        generator.days(1, 1).months(2, 2).years(3, 3);

        final Period result = generator.generate(random);
        assertThat(result)
                .hasDays(1)
                .hasMonths(2)
                .hasYears(3);
    }

    @Test
    void validation() {
        assertThatThrownBy(() -> generator.days(2, 1))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Period days 'min' must be less than or equal 'max': days(2, 1)");

        assertThatThrownBy(() -> generator.months(2, 1))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Period months 'min' must be less than or equal 'max': months(2, 1)");

        assertThatThrownBy(() -> generator.years(2, 1))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Period years 'min' must be less than or equal 'max': years(2, 1)");
    }
}
