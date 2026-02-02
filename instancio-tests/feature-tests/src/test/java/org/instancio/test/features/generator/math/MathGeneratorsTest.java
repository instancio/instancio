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

package org.instancio.test.features.generator.math;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.SupportedMathTypes;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({Feature.GENERATE, Feature.MATH_GENERATOR})
@ExtendWith(InstancioExtension.class)
class MathGeneratorsTest {

    @Test
    void generate() {
        final SupportedMathTypes result = Instancio.of(SupportedMathTypes.class)
                .generate(all(BigInteger.class), gen -> gen.math().bigInteger().range(BigInteger.ONE, BigInteger.ONE))
                .generate(all(BigDecimal.class), gen -> gen.math().bigDecimal().range(BigDecimal.ONE, BigDecimal.ONE))
                .create();

        assertThat(result.getBigInteger()).isEqualTo(BigInteger.ONE);
        assertThat(result.getBigDecimal()).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    void bigDecimalWithScale() {
        final int expectedScale = 9;
        final SupportedMathTypes result = Instancio.of(SupportedMathTypes.class)
                .generate(all(BigDecimal.class), gen -> gen.math().bigDecimal()
                        .scale(expectedScale)
                        .min(BigDecimal.ZERO)
                        .max(BigDecimal.ONE))
                .create();

        assertThat(result.getBigDecimal())
                .isBetween(BigDecimal.ZERO, BigDecimal.ONE)
                .hasScaleOf(expectedScale);
    }

    @Test
    void bigDecimalWithPrecision() {
        final int expectedPrecision = 20;
        final int expectedScale = 9;

        final SupportedMathTypes result = Instancio.of(SupportedMathTypes.class)
                .generate(all(BigDecimal.class), gen -> gen.math().bigDecimal()
                        .precision(expectedPrecision)
                        .scale(expectedScale))
                .create();

        assertThat(result.getBigDecimal())
                .hasScaleOf(expectedScale)
                .extracting(BigDecimal::precision)
                .isEqualTo(expectedPrecision);
    }

    /**
     * precision() and range()/min()/max() are incompatible with each other.
     * If used together, the last one wins.
     */
    @Nested
    class BigDecimalPrecisionAndRangeOrderTest {

        @Test
        void rangeWins() {
            final SupportedMathTypes result = Instancio.of(SupportedMathTypes.class)
                    .generate(all(BigDecimal.class), gen -> gen.math().bigDecimal()
                            .precision(20)
                            .range(BigDecimal.ZERO, BigDecimal.ONE))
                    .create();

            assertThat(result.getBigDecimal()).isBetween(BigDecimal.ZERO, BigDecimal.ONE);
        }

        @Test
        void minWins() {
            final BigDecimal min = new BigDecimal("200000000000000");
            final SupportedMathTypes result = Instancio.of(SupportedMathTypes.class)
                    .generate(all(BigDecimal.class), gen -> gen.math().bigDecimal()
                            .precision(20)
                            .min(min))
                    .create();

            assertThat(result.getBigDecimal()).isGreaterThanOrEqualTo(min);
        }

        @Test
        void maxWins() {
            final BigDecimal max = new BigDecimal("-200000000000000");
            final SupportedMathTypes result = Instancio.of(SupportedMathTypes.class)
                    .generate(all(BigDecimal.class), gen -> gen.math().bigDecimal()
                            .precision(20)
                            .max(max))
                    .create();

            assertThat(result.getBigDecimal()).isLessThanOrEqualTo(max);
        }

        @Test
        void precisionWins() {
            final int expectedPrecision = 20;

            final SupportedMathTypes result = Instancio.of(SupportedMathTypes.class)
                    .generate(all(BigDecimal.class), gen -> gen.math().bigDecimal()
                            .range(BigDecimal.ZERO, BigDecimal.ONE)
                            .precision(expectedPrecision))
                    .create();

            assertThat(result.getBigDecimal().precision()).isEqualTo(expectedPrecision);
        }
    }
}