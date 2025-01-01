/*
 * Copyright 2022-2025 the original author or authors.
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
import org.instancio.generator.specs.NumberGeneratorSpec;
import org.instancio.generators.Generators;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.BaseNumericGeneratorTest;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@FeatureTag({Feature.GENERATOR, Feature.MATH_GENERATOR})
@ExtendWith(InstancioExtension.class)
class BigDecimalGeneratorTest extends BaseNumericGeneratorTest<BigDecimal> {

    BigDecimalGeneratorTest() {
        super(BigDecimal.class);
    }

    @Override
    public NumberGeneratorSpec<BigDecimal> createSpec(final Generators gen) {
        return gen.math().bigDecimal();
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
        final BigDecimal result = Instancio.of(BigDecimal.class)
                .generate(root(), gen -> gen.math().bigDecimal().range(min, max))
                .create();

        assertThat(result)
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

        final BigDecimal result = Instancio.of(BigDecimal.class)
                .generate(root(), gen -> gen.math().bigDecimal().range(min, max).scale(20))
                .create();

        assertThat(result)
                .isGreaterThanOrEqualTo(min)
                .isLessThanOrEqualTo(max);
    }
}
