/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.internal.generator.lang;

import org.instancio.exception.InstancioApiException;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.array.ArrayGenerator;
import org.instancio.internal.util.Constants;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArrayGeneratorSpecTest {
    private static final int PERCENTAGE = Constants.RANGE_ADJUSTMENT_PERCENTAGE;

    private final GeneratorContext context = new GeneratorContext(Settings.defaults(), new DefaultRandom());

    private final ArrayGeneratorExt<?> generator = new ArrayGeneratorExt<>(context);

    @BeforeEach
    void setUp() {
        generator.minLength(0).minLength(1);
    }

    @Test
    @DisplayName("newMax < min: new min should be less than newMax by PERCENTAGE")
    void newMaxIsLessThanMin() {
        final int newMax = 50;
        generator
                .minLength(Integer.MAX_VALUE)
                .maxLength(newMax);
        assertThat(generator.getMinLength()).isEqualTo(calculatePercentage(newMax, -PERCENTAGE));
    }

    @Test
    @DisplayName("newMax == min: new min should be less than newMax by PERCENTAGE")
    void newMaxIsEqualToMin() {
        final int newMax = 100;
        generator
                .minLength(Integer.MAX_VALUE)
                .maxLength(newMax);
        assertThat(generator.getMinLength()).isEqualTo(calculatePercentage(newMax, -PERCENTAGE));
    }

    @Test
    @DisplayName("newMin > max: new nax should be set to greater than newMin by PERCENTAGE")
    void newMinIsGreaterThanMax() {
        final int newMin = 100;
        generator.minLength(newMin);
        assertThat(generator.getMaxLength()).isEqualTo(calculatePercentage(newMin, PERCENTAGE));
    }

    @Test
    @DisplayName("newMin == max: new nax should be set to greater than newMin by PERCENTAGE")
    void newMinIsEqualToMax() {
        final int newMin = 100;
        generator
                .maxLength(0)
                .minLength(newMin);
        assertThat(generator.getMaxLength()).isEqualTo(calculatePercentage(newMin, PERCENTAGE));
    }

    @Test
    void length() {
        final int length = 2;
        generator.length(length);
        assertThat(generator.getMinLength()).isEqualTo(length);
        assertThat(generator.getMaxLength()).isEqualTo(length);
    }

    @Test
    void minValidation() {
        assertThatThrownBy(() -> generator.minLength(-1))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("length must not be negative: -1");
    }

    @Test
    void maxValidation() {
        assertThatThrownBy(() -> generator.maxLength(-1))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("length must not be negative: -1");
    }

    private static int calculatePercentage(final int initial, final int percentage) {
        return initial + initial * percentage / 100;
    }

    /**
     * Subclass to expose getters.
     */
    private static class ArrayGeneratorExt<T> extends ArrayGenerator<T> {
        ArrayGeneratorExt(final GeneratorContext context) {
            super(context);
        }

        int getMinLength() {
            return minLength;
        }

        int getMaxLength() {
            return maxLength;
        }
    }

}
