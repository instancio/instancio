/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.generator.lang;

import org.instancio.generator.GeneratorContext;
import org.instancio.internal.random.RandomProvider;
import org.instancio.internal.random.RandomProviderImpl;
import org.instancio.settings.Setting;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IntegerGeneratorTest {
    private static final Settings settings = Settings.defaults();
    private static final int MIN = settings.get(Setting.INTEGER_MIN);
    private static final int MAX = settings.get(Setting.INTEGER_MAX);
    private final RandomProvider random = new RandomProviderImpl();
    private final GeneratorContext context = new GeneratorContext(settings, random);
    private final IntegerGenerator generator = new IntegerGenerator(context);

    @Test
    void minShouldBeSetToMaxValueIfMinEqualsMax() {
        generator.min(MAX);
        assertThat(generator.getMax()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void minShouldBeSetToMaxValueIfMinGreaterThanMax() {
        generator.min(MAX + 1);
        assertThat(generator.getMax()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void maxShouldBeSetToMinValueIfMaxEqualsMin() {
        generator.max(MIN);
        assertThat(generator.getMin()).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    void maxShouldBeSetToMinValueIfMaxLessThanMin() {
        generator.max(MIN - 1);
        assertThat(generator.getMin()).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    void range() {
        final int min = 2;
        final int max = 3;
        generator.range(min, max);
        assertThat(generator.getMin()).isEqualTo(min);
        assertThat(generator.getMax()).isEqualTo(max);
    }

    @Test
    void rangeThrowsErrorIfMinIsGreaterThanOrEqualToMax() {
        assertThatThrownBy(() -> generator.range(3, 2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid 'range(3, 2)': lower bound must be less than upper bound");

        assertThatThrownBy(() -> generator.range(3, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid 'range(3, 3)': lower bound must be less than upper bound");
    }

    @Test
    void supports() {
        assertThat(generator.supports(int.class)).isTrue();
        assertThat(generator.supports(Integer.class)).isTrue();
        assertThat(generator.supports(int[].class)).isFalse();
    }
}
