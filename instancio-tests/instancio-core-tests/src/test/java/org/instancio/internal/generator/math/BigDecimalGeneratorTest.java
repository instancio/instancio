/*
 * Copyright 2022-2023 the original author or authors.
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

import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.lang.AbstractRandomNumberGeneratorSpec;
import org.instancio.internal.generator.lang.NumberGeneratorSpecTestTemplate;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BigDecimalGeneratorTest extends NumberGeneratorSpecTestTemplate<BigDecimal> {

    private final GeneratorContext context = new GeneratorContext(Settings.defaults(), new DefaultRandom());

    @Override
    protected AbstractRandomNumberGeneratorSpec<BigDecimal> createGenerator() {
        return new BigDecimalGenerator(context);
    }

    @Override
    protected String apiMethod() {
        return "bigDecimal()";
    }

    @CsvSource({
            "-10.0012, -10.0011",
            "0.00015, 0.00019",
            "0.00008, 0.00009",
            "0, 0.00001",
            "-0.00001, 0",
            "9999999999999.555, 9999999999999.557",
            "9999999999999999999999999999999999999.111, 9999999999999999999999999999999999999.1111"
    })
    @ParameterizedTest
    void rangeWithFractionalValues(final BigDecimal min, final BigDecimal max) {
        final AbstractRandomNumberGeneratorSpec<BigDecimal> generator = getGenerator();
        generator.range(min, max);

        final BigDecimal result = generator.generate(new DefaultRandom());
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

        final BigDecimal result = generator.generate(new DefaultRandom());
        assertThat(result)
                .isNotNull()
                .isGreaterThanOrEqualTo(min)
                .isLessThanOrEqualTo(max);
    }
}
