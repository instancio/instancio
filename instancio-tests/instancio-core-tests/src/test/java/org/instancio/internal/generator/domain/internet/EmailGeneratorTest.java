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
package org.instancio.internal.generator.domain.internet;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Gen.ints;

class EmailGeneratorTest {
    private static final Random random = new DefaultRandom();
    private final GeneratorContext context = new GeneratorContext(Settings.create(), random);
    private final EmailGenerator generator = new EmailGenerator(context);

    @Test
    void apiMethod() {
        assertThat(generator.apiMethod()).isEqualTo("email()");
    }

    @ValueSource(ints = {3, 4, 5, 6})
    @ParameterizedTest
    void lengthLessThanSix(final int length) {
        generator.length(length);
        assertThat(generator.generate(random))
                .hasSize(length)
                .matches("^\\w+@\\w+$");
    }

    @ValueSource(ints = {7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20})
    @ParameterizedTest
    void lengthGreaterThanSix(final int length) {
        EmailGenerator generator = new EmailGenerator(context);
        generator.length(length);
        assertThat(generator.generate(random))
                .hasSize(length)
                .matches("^\\w+@\\w+\\.\\w{3}$");
    }

    @RepeatedTest(100)
    void lengthRange() {
        final int min = ints().range(7, 20).get();
        final int max = min + ints().range(0, 20).get();

        generator.length(min, max);

        assertThat(generator.generate(random))
                .hasSizeBetween(min, max)
                .matches("^\\w+@\\w+\\.\\p{Lower}{3}$");
    }
}
