/*
 * Copyright 2022 the original author or authors.
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

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.math.BigDecimalGenerator;
import org.instancio.internal.generator.math.BigIntegerGenerator;
import org.instancio.internal.random.DefaultRandom;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.instancio.testsupport.asserts.HintsAssert;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@NonDeterministicTag
@FeatureTag(Feature.SETTINGS)
class NumberGeneratorTest {

    private static final int MIN = -10;
    private static final int MAX = 10;
    private static final int SAMPLE_SIZE = 10_000;
    private static final Settings settings = Settings.defaults()
            .set(Keys.BYTE_MIN, (byte) MIN)
            .set(Keys.BYTE_MAX, (byte) MAX)
            .set(Keys.BYTE_NULLABLE, true)
            .set(Keys.SHORT_MIN, (short) MIN)
            .set(Keys.SHORT_MAX, (short) MAX)
            .set(Keys.SHORT_NULLABLE, true)
            .set(Keys.INTEGER_MIN, MIN)
            .set(Keys.INTEGER_MAX, MAX)
            .set(Keys.INTEGER_NULLABLE, true)
            .set(Keys.LONG_MIN, (long) MIN)
            .set(Keys.LONG_MAX, (long) MAX)
            .set(Keys.LONG_NULLABLE, true)
            .set(Keys.FLOAT_MIN, (float) MIN)
            .set(Keys.FLOAT_MAX, (float) MAX)
            .set(Keys.FLOAT_NULLABLE, true)
            .set(Keys.DOUBLE_MIN, (double) MIN)
            .set(Keys.DOUBLE_MAX, (double) MAX)
            .set(Keys.DOUBLE_NULLABLE, true);

    private static final Random random = new DefaultRandom();
    private static final GeneratorContext context = new GeneratorContext(settings, random);

    static class WholeNumberGeneratorTestTemplate<GEN extends AbstractRandomComparableNumberGeneratorSpec<?>> {

        void verifyGenerate(final GEN generator) {
            final Set<Object> results = new HashSet<>();
            for (int i = 0; i < SAMPLE_SIZE; i++) {
                final Number result = generator.generate(random);
                results.add(result);

                if (result != null) {
                    assertThat(result.intValue()).isBetween(MIN, MAX);
                }
            }

            assertThat(results).containsNull().hasSize(MAX - MIN + 2);
            HintsAssert.assertHints(generator.hints())
                    .populateActionIsApplySelectors();
        }
    }

    static class FractionalNumberGeneratorTestTemplate<GEN extends AbstractRandomComparableNumberGeneratorSpec<?>> {

        void verifyGenerate(final GEN generator) {
            final Set<Object> results = new HashSet<>();
            for (int i = 0; i < SAMPLE_SIZE; i++) {
                final Number result = generator.generate(random);
                results.add(result);

                if (result != null) {
                    assertThat(result.doubleValue()).isBetween((double) MIN, (double) MAX);
                }
            }

            assertThat(results).containsNull()
                    .as("Expecting at least half of sample size of unique fractional numbers")
                    .hasSizeGreaterThan(SAMPLE_SIZE / 2);

            HintsAssert.assertHints(generator.hints())
                    .populateActionIsApplySelectors();
        }
    }

    @Test
    void byteGenerator() {
        new WholeNumberGeneratorTestTemplate<ByteGenerator>().verifyGenerate(new ByteGenerator(context));
    }

    @Test
    void shortGenerator() {
        new WholeNumberGeneratorTestTemplate<ShortGenerator>().verifyGenerate(new ShortGenerator(context));
    }

    @Test
    void integerGenerator() {
        new WholeNumberGeneratorTestTemplate<IntegerGenerator>().verifyGenerate(new IntegerGenerator(context));
    }

    @Test
    void longGenerator() {
        new WholeNumberGeneratorTestTemplate<LongGenerator>().verifyGenerate(new LongGenerator(context));
    }

    @Test
    void bigIntegerGenerator() {
        final BigIntegerGenerator generator = new BigIntegerGenerator(context,
                BigInteger.valueOf(MIN), BigInteger.valueOf(MAX), true);
        new WholeNumberGeneratorTestTemplate<BigIntegerGenerator>().verifyGenerate(generator);
    }

    @Test
    void floatGenerator() {
        new FractionalNumberGeneratorTestTemplate<FloatGenerator>().verifyGenerate(new FloatGenerator(context));
    }

    @Test
    void doubleGenerator() {
        new FractionalNumberGeneratorTestTemplate<DoubleGenerator>().verifyGenerate(new DoubleGenerator(context));
    }

    @Test
    void bigDecimalGenerator() {
        final BigDecimalGenerator generator = new BigDecimalGenerator(context,
                BigDecimal.valueOf(MIN), BigDecimal.valueOf(MAX), true);
        new FractionalNumberGeneratorTestTemplate<BigDecimalGenerator>().verifyGenerate(generator);
    }

}
