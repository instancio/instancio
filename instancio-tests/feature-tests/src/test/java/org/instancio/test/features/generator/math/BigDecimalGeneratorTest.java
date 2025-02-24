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

import org.assertj.core.api.SoftAssertions;
import org.instancio.Instancio;
import org.instancio.generator.specs.BigDecimalSpec;
import org.instancio.generator.specs.NumberGeneratorSpec;
import org.instancio.generators.Generators;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.BaseNumericGeneratorTest;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    public static void main(String[] args) {
        System.out.println(new BigDecimal("10000.07").setScale(1, RoundingMode.HALF_UP));
    }

    @Test
    void rangeWithScaleSmallerThanThoseOfMinAndMax() {
        final BigDecimal min = new BigDecimal("0.01");
        final BigDecimal max = new BigDecimal("0.99");

        final int scale = 1;

        final BigDecimal minWithScale1 = new BigDecimal("0.0");
        final BigDecimal maxWithScale1 = new BigDecimal("1.0");

        final BigDecimalSpec rangedSpec = Instancio.gen().math().bigDecimal().range(min, max).scale(scale);

        BigDecimal minGenerated = rangedSpec.get();
        BigDecimal maxGenerated = rangedSpec.get();
        for (int i = 1; i < Constants.SAMPLE_SIZE_DDD; i++) {
            final BigDecimal generated = rangedSpec.get();
            if (generated.compareTo(minGenerated) < 0) {
                minGenerated = generated;
            } else if (generated.compareTo(maxGenerated) > 0) {
                maxGenerated = generated;
            }
        }

        final SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(minGenerated).isEqualByComparingTo(minWithScale1);
        softAssertions.assertThat(maxGenerated).isEqualByComparingTo(maxWithScale1);

        softAssertions.assertAll();
    }

}
