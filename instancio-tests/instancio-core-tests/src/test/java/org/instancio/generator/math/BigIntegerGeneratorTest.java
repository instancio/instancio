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
package org.instancio.generator.math;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.lang.AbstractRandomNumberGeneratorSpec;
import org.instancio.generator.lang.NumberGeneratorSpecTestTemplate;
import org.instancio.internal.random.DefaultRandom;
import org.instancio.settings.Settings;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BigIntegerGeneratorTest extends NumberGeneratorSpecTestTemplate<BigInteger> {

    private final GeneratorContext context = new GeneratorContext(Settings.defaults(), new DefaultRandom());

    @Override
    protected AbstractRandomNumberGeneratorSpec<BigInteger> createGenerator() {
        return new BigIntegerGenerator(context);
    }

    @Override
    protected Map<Class<?>, Boolean> verifySupported() {
        return new HashMap<Class<?>, Boolean>() {{
            put(BigInteger.class, true);
            put(BigInteger[].class, false);
        }};
    }

    @CsvSource({
            "-10000000000001, -10000000000000",
            "0, 1",
            "100000000000000, 100000000000001"
    })
    @ParameterizedTest
    void bigIntegerRange(final BigInteger min, final BigInteger max) {
        final AbstractRandomNumberGeneratorSpec<BigInteger> generator = getGenerator();
        generator.range(min, max);

        final BigInteger result = generator.generate(new DefaultRandom());

        assertThat(result)
                .isNotNull()
                .isGreaterThanOrEqualTo(min)
                .isLessThanOrEqualTo(max);
    }
}
