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
package org.instancio.generator.math;

import org.instancio.generator.GeneratorContext;
import org.instancio.internal.random.RandomProvider;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

class BigIntegerGeneratorTest {
    private static final BigInteger MIN = BigInteger.ONE;
    private static final BigInteger MAX = BigInteger.valueOf(10_000L);

    private final GeneratorContext context = new GeneratorContext(Settings.defaults(), new RandomProvider());
    private final BigIntegerGeneratorExt generator = new BigIntegerGeneratorExt(context, MIN, MAX, false);

    @Test
    void minShouldBeSetToMaxValueIfMinEqualsMax() {
        generator.min(MAX);
        final BigInteger newMax = generator.getMin().add(BigInteger.valueOf(Long.MAX_VALUE));
        assertThat(generator.getMax()).isEqualTo(newMax);
    }

    @Test
    void minShouldBeSetToMaxValueIfMinGreaterThanMax() {
        generator.min(MAX.add(BigInteger.ONE));
        final BigInteger newMax = generator.getMin().add(BigInteger.valueOf(Long.MAX_VALUE));
        assertThat(generator.getMax()).isEqualTo(newMax);
    }

    @Test
    void maxShouldBeSetToMinValueIfMaxEqualsMin() {
        generator.max(MIN);
        final BigInteger newMin = generator.getMax().subtract(BigInteger.valueOf(Long.MAX_VALUE));
        assertThat(generator.getMin()).isEqualTo(newMin);
    }

    @Test
    void maxShouldBeSetToMinValueIfMaxLessThanMin() {
        generator.max(MIN.subtract(BigInteger.ONE));
        final BigInteger newMin = generator.getMax().subtract(BigInteger.valueOf(Long.MAX_VALUE));
        assertThat(generator.getMin()).isEqualTo(newMin);
    }

    // subclass to expose getters
    private static class BigIntegerGeneratorExt extends BigIntegerGenerator {
        public BigIntegerGeneratorExt(GeneratorContext context, BigInteger min, BigInteger max, boolean nullable) {
            super(context, min, max, nullable);
        }

        @Override
        public BigInteger getMin() {
            return super.getMin();
        }

        @Override
        public BigInteger getMax() {
            return super.getMax();
        }
    }
}
