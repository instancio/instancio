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
package org.instancio.internal.generator.text;

import org.instancio.Random;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.GeneratorContext;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CsvGeneratorTest {

    private static final Random random = new DefaultRandom();

    private final CsvGenerator generator = new CsvGenerator(
            new GeneratorContext(Settings.defaults(), random));

    @Test
    void apiMethod() {
        assertThat(generator.apiMethod()).isEqualTo("csv()");
    }

    @Test
    void validationRows() {
        assertThatThrownBy(() -> generator.rows(-1))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("min must not be negative: -1");

        assertThatThrownBy(() -> generator.rows(-1, 10))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("min must not be negative: -1");

        assertThatThrownBy(() -> generator.rows(2, 1))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("min must be less than or equal to max: (2, 1)");
    }

    @Test
    void validationWrapIf() {
        assertThatThrownBy(() -> generator.wrapIf(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("wrapIf() predicate must not be null");
    }

    @Test
    void validationColumns() {
        assertThatThrownBy(() -> generator.column(null, random -> "foo"))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("column() name must not be null");

        assertThatThrownBy(() -> generator.column("name", null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("column() generator must not be null");
    }

}
