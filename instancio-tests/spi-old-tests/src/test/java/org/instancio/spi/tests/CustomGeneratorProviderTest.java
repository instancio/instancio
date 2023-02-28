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
package org.instancio.spi.tests;

import org.example.generator.CustomIntegerGenerator;
import org.example.spi.CustomGeneratorProvider;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;

class CustomGeneratorProviderTest {

    @Test
    void overrideBuiltInGenerator() {
        assertThat(Instancio.create(String.class))
                .isEqualTo(CustomGeneratorProvider.STRING_GENERATOR_VALUE);
    }

    @Test
    void defineNewGenerator() {
        assertThat(Instancio.create(Pattern.class))
                .isSameAs(CustomGeneratorProvider.PATTERN_GENERATOR_VALUE);
    }

    @Test
    void shouldUseCustomIntegerGenerator() {
        assertThat(Instancio.create(int.class))
                .isBetween(CustomIntegerGenerator.MIN, CustomIntegerGenerator.MAX);
    }

    @Test
    @DisplayName("Having overridden int generator, should still be able to use built-in generators, if needed")
    void builtInGeneratorStillAvailableAfterOverride() {
        final int result = Instancio.of(int.class)
                .generate(allInts(), gen -> gen.ints().range(100, 105))
                .create();

        assertThat(result).isBetween(100, 105);
    }

    @Test
    void customGeneratorTakesPrecedenceOverBuiltInt() {
        final int expectedSize = 1000;
        final List<Integer> result = Instancio.of(new TypeToken<List<Integer>>() {})
                .supply(allInts(), new CustomIntegerGenerator().evenNumbers())
                // should be ignored, custom has higher precedence
                .generate(allInts(), gen -> gen.ints().range(100, 105))
                .generate(all(List.class), gen -> gen.collection().size(expectedSize))
                .create();

        assertThat(result)
                .hasSize(expectedSize)
                .allSatisfy(n -> assertThat(n)
                        .isEven()
                        .isBetween(CustomIntegerGenerator.MIN, CustomIntegerGenerator.MAX));
    }
}
