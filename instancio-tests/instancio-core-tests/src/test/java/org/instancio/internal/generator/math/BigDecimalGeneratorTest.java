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
package org.instancio.internal.generator.math;

import org.instancio.Random;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.lang.AbstractRandomNumberGeneratorSpec;
import org.instancio.internal.generator.lang.NumberGeneratorSpecTestTemplate;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BigDecimalGeneratorTest extends NumberGeneratorSpecTestTemplate<BigDecimal> {

    private final Random random = new DefaultRandom();
    private final GeneratorContext context = new GeneratorContext(Settings.defaults(), random);

    @Override
    protected AbstractRandomNumberGeneratorSpec<BigDecimal> createGenerator() {
        return new BigDecimalGenerator(context);
    }

    @Override
    protected String apiMethod() {
        return "bigDecimal()";
    }

    @CsvSource({
            "-10.12, -10.11",
            "0.15, 0.19",
            "0.08, 0.09",
            "0, 0.01",
            "-0.01, 0",
            "9999999999999.55, 9999999999999.57",
            "9999999999999999999999999999999999999.11, 9999999999999999999999999999999999999.11"
    })
    @ParameterizedTest
    void rangeWithFractionalValues(final BigDecimal min, final BigDecimal max) {
        final AbstractRandomNumberGeneratorSpec<BigDecimal> generator = getGenerator();
        generator.range(min, max);

        final BigDecimal result = generator.generate(random);
        assertThat(result)
                .isNotNull()
                .isGreaterThanOrEqualTo(min)
                .isLessThanOrEqualTo(max);
    }

    @CsvSource({
            "-10.00000000000000000002, -10.00000000000000000001",
            "0.00000000000000000001, 0.00000000000000000001",
            "0, 0.00000000000000001234",
            "-0.00000000000000001234, 0",
            "9999999999999999999999999999999999999.00000000000000000008, 9999999999999999999999999999999999999.00000000000000000009"
    })
    @ParameterizedTest
    void rangeWithLargeScale(final BigDecimal min, final BigDecimal max) {
        final BigDecimalGenerator generator = (BigDecimalGenerator) getGenerator();
        generator.range(min, max);
        generator.scale(20);

        final BigDecimal result = generator.generate(random);
        assertThat(result)
                .isNotNull()
                .isGreaterThanOrEqualTo(min)
                .isLessThanOrEqualTo(max);
    }

    @Test
    void nonPositiveScaleWithPrecision() {
        final BigDecimalGenerator generator = (BigDecimalGenerator) getGenerator();

        for (int precision = 1; precision < 20; precision++) {
            for (int scale = -10; scale <= 20; scale++) {

                generator.precision(precision).scale(scale);

                for (int i = 0; i < Constants.SAMPLE_SIZE_DDD; i++) {
                    final BigDecimal result = generator.generate(random);

                    assertThat(result)
                            .as("precision=%s, scale=%s, result=%s", scale, precision, result)
                            .hasScaleOf(scale)
                            .extracting(BigDecimal::precision)
                            .isEqualTo(precision);
                }
            }
        }
    }

    @ValueSource(ints = {-1, 0})
    @ParameterizedTest
    void nonPositivePrecisionShouldProduceAnError(final int precision) {
        final BigDecimalGenerator generator = (BigDecimalGenerator) getGenerator();

        assertThatThrownBy(() -> generator.precision(precision))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("'precision' must be positive: %s", precision);
    }
}
