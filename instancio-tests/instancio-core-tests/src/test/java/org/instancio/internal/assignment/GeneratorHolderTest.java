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
package org.instancio.internal.assignment;

import org.instancio.GeneratorSpecProvider;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeneratorHolderTest {

    @Test
    void ofSupplier() {
        assertThatThrownBy(() -> GeneratorHolder.of((Supplier<?>) null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("null Supplier passed to 'supply()' method");
    }

    @Test
    void ofGenerator() {
        assertThatThrownBy(() -> GeneratorHolder.of((Generator<?>) null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("null Generator passed to 'supply()' method");
    }

    @Test
    void ofGeneratorSpecProvider() {
        assertThatThrownBy(() -> GeneratorHolder.of((GeneratorSpecProvider<?>) null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("the second argument of 'generate()' method must not be null");
    }

    @Test
    void ofGeneratorSpec() {
        assertThatThrownBy(() -> GeneratorHolder.of((GeneratorSpec<?>) null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("the second argument of 'generate()' method must not be null");
    }
}
