/*
 * Copyright 2022-2026 the original author or authors.
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

import org.instancio.exception.InstancioApiException;
import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BigDecimalGeneratorTest extends AbstractGeneratorTestTemplate<BigDecimal, BigDecimalGenerator> {

    @Override
    protected String getApiMethod() {
        return "bigDecimal()";
    }

    @Override
    protected BigDecimalGenerator generator() {
        return new BigDecimalGenerator(getGeneratorContext());
    }

    @Test
    void nonPositiveScaleWithPrecision() {
        final BigDecimalGenerator generator = generator();

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
        final BigDecimalGenerator generator = generator();

        assertThatThrownBy(() -> generator.precision(precision))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("'precision' must be positive: %s", precision);
    }
}
