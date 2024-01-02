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
package org.instancio.internal.generator.domain.id.usa;

import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class SsnGeneratorTest extends AbstractGeneratorTestTemplate<String, SsnGenerator> {

    private final SsnGenerator generator = new SsnGenerator(getGeneratorContext());

    @Override
    protected String getApiMethod() {
        return "ssn()";
    }

    @Override
    protected SsnGenerator generator() {
        return generator;
    }

    @Test
    void generate() {
        final String result = generator.generate(random);

        assertThat(result)
                .containsOnlyDigits()
                .hasSize(9);
    }

    @ValueSource(strings = {
            "123004567",
            "123450000",
            "000123456",
            "666123456",
            "912345678",
            "219099999",
            "123456789"
    })
    @ParameterizedTest
    void isInvalid(final String ssn) {
        assertThat(SsnGenerator.isInvalid(ssn)).isTrue();
    }
}
