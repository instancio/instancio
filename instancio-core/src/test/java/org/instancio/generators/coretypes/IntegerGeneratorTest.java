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
package org.instancio.generators.coretypes;

import org.instancio.GeneratorContext;
import org.instancio.internal.random.RandomProvider;
import org.instancio.settings.Setting;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IntegerGeneratorTest {
    private static final Settings settings = Settings.defaults();
    private static final int MIN = settings.get(Setting.INTEGER_MIN);
    private static final int MAX = settings.get(Setting.INTEGER_MAX);
    private final GeneratorContext context = new GeneratorContext(settings, new RandomProvider());
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
}
