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
import org.instancio.generators.BigDecimalGenerator;
import org.instancio.generators.BigIntegerGenerator;
import org.instancio.internal.random.RandomProvider;
import org.instancio.settings.Setting;
import org.instancio.settings.Settings;
import org.instancio.testsupport.tags.NonDeterministicTag;
import org.instancio.testsupport.tags.SettingsTag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.testsupport.asserts.GeneratedHintsAssert.assertHints;

@SettingsTag
@NonDeterministicTag
class NumberGeneratorTest {

    private static final int MIN = -10;
    private static final int MAX = 10;
    private static final int SAMPLE_SIZE = 10_000;
    private static final Settings settings = Settings.defaults()
            .set(Setting.BYTE_MIN, (byte) MIN)
            .set(Setting.BYTE_MAX, (byte) MAX)
            .set(Setting.BYTE_NULLABLE, true)
            .set(Setting.SHORT_MIN, (short) MIN)
            .set(Setting.SHORT_MAX, (short) MAX)
            .set(Setting.SHORT_NULLABLE, true)
            .set(Setting.INTEGER_MIN, MIN)
            .set(Setting.INTEGER_MAX, MAX)
            .set(Setting.INTEGER_NULLABLE, true)
            .set(Setting.LONG_MIN, (long) MIN)
            .set(Setting.LONG_MAX, (long) MAX)
            .set(Setting.LONG_NULLABLE, true)
            .set(Setting.FLOAT_MIN, (float) MIN)
            .set(Setting.FLOAT_MAX, (float) MAX)
            .set(Setting.FLOAT_NULLABLE, true)
            .set(Setting.DOUBLE_MIN, (double) MIN)
            .set(Setting.DOUBLE_MAX, (double) MAX)
            .set(Setting.DOUBLE_NULLABLE, true);

    private static final GeneratorContext context = new GeneratorContext(settings, new RandomProvider());

    static class WholeNumberGeneratorTestTemplate<GEN extends AbstractRandomNumberGeneratorSpec<?>> {

        void verifyGenerate(final GEN generator) {
            final Set<Object> results = new HashSet<>();
            for (int i = 0; i < SAMPLE_SIZE; i++) {
                final Number result = generator.generate();
                results.add(result);

                if (result != null) {
                    assertThat(result.intValue()).isBetween(MIN, MAX);
                }
            }

            assertThat(results).containsNull().hasSize(MAX - MIN + 1);
            assertHints(generator.getHints())
                    .nullableResult(true)
                    .ignoreChildren(true);
        }
    }

    static class FractionalNumberGeneratorTestTemplate<GEN extends AbstractRandomNumberGeneratorSpec<?>> {

        void verifyGenerate(final GEN generator) {
            final Set<Object> results = new HashSet<>();
            for (int i = 0; i < SAMPLE_SIZE; i++) {
                final Number result = generator.generate();
                results.add(result);

                if (result != null) {
                    assertThat(result.doubleValue()).isBetween((double) MIN, (double) MAX);
                }
            }

            assertThat(results).containsNull()
                    .as("Expecting at least half of sample size of unique fractional numbers")
                    .hasSizeGreaterThan(SAMPLE_SIZE / 2);

            assertHints(generator.getHints())
                    .nullableResult(true)
                    .ignoreChildren(true);
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
