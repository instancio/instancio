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
package org.instancio.internal.generator.checksum;

import org.instancio.exception.InstancioApiException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Mod11GeneratorTest extends AbstractVariableLengthModCheckGeneratorTest<Mod11Generator> {

    private final Mod11Generator generator = new Mod11Generator(getGeneratorContext());

    @Override
    protected String getApiMethod() {
        return "mod11()";
    }

    @Override
    protected Mod11Generator generator() {
        return generator;
    }

    @Test
    void validationThreshold() {
        assertThatThrownBy(() -> generator.threshold(-1))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("threshold must not be negative: -1");
    }
}
