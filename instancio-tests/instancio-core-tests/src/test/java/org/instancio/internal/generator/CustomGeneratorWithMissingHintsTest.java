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
package org.instancio.internal.generator;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;

class CustomGeneratorWithMissingHintsTest {

    @Test
    @DisplayName("Should generate expected value if hints or AfterGenerate are null")
    void nullHints() {
        final Hints mockHints = Mockito.mock(Hints.class);
        assertGeneratedValueWithHints(mockHints);
        assertGeneratedValueWithHints(null);
    }

    private static void assertGeneratedValueWithHints(final Hints hints) {
        final String expected = "foo";
        final StringHolder result = Instancio.of(StringHolder.class)
                .supply(allStrings(), new Generator<String>() {
                    @Override
                    public String generate(final Random random) {
                        return expected;
                    }

                    @Override
                    public Hints hints() {
                        return hints;
                    }
                })
                .create();

        assertThat(result.getValue()).isEqualTo(expected);
    }
}
