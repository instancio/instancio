/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.internal.generator.domain.id.bra;

import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class CnpjGeneratorTest extends AbstractGeneratorTestTemplate<String, CnpjGenerator> {

    private final CnpjGenerator generator = new CnpjGenerator(getGeneratorContext());

    @Override
    protected String getApiMethod() {
        return "cnpj()";
    }

    @Override
    protected CnpjGenerator generator() {
        return generator;
    }

    @Test
    @DisplayName("Test the default CNPJ generation, without formatting")
    void cnpjDefaultGeneration() {
        String result = generator.generate(random);
        assertThat(result).containsOnlyDigits().hasSize(14);
    }

    @Test
    @DisplayName("Test the custom CNPJ generation, with formatting")
    void cnpjCustomGeneration() {
        String result = generator.formatted().generate(random);
        assertThat(result).containsPattern(Pattern.compile("^(\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2})$")).hasSize(18);
    }
}
