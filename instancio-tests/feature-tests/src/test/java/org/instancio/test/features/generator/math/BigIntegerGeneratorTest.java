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

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@FeatureTag({Feature.GENERATOR, Feature.MATH_GENERATOR})
@ExtendWith(InstancioExtension.class)
class BigIntegerGeneratorTest extends BaseNumericGeneratorTest<BigInteger> {

    BigIntegerGeneratorTest() {
        super(BigInteger.class);
    }

    @Override
    public NumberGeneratorSpec<BigInteger> createSpec(final Generators gen) {
        return gen.math().bigInteger();
    }

    @CsvSource({
            "-10000000000001, -10000000000000",
            "0, 1",
            "100000000000000, 100000000000001",
            "111111111111111111111111111111111111111117, 111111111111111111111111111111111111111119"
    })
    @ParameterizedTest
    void bigIntegerRange(final BigInteger min, final BigInteger max) {
        final BigInteger result = Instancio.of(BigInteger.class)
                .generate(root(), gen -> gen.math().bigInteger().range(min, max))
                .create();

        assertThat(result)
                .isGreaterThanOrEqualTo(min)
                .isLessThanOrEqualTo(max);
    }
}
