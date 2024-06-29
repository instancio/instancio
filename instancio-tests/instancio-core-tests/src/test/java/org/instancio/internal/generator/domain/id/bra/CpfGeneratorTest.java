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
package org.instancio.internal.generator.domain.id.bra;

import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class CpfGeneratorTest extends AbstractGeneratorTestTemplate<String, CpfGenerator> {

    private final CpfGenerator generator = new CpfGenerator(getGeneratorContext());

    @Override
    protected String getApiMethod() {
        return "cpf()";
    }

    @Override
    protected CpfGenerator generator() {
        return generator;
    }

    @Test
    @DisplayName("Test the default CPF generation, without formatting")
    void testCpfDefaultGeneration() {
        String result = generator.generate(random);
        assertThat(result).containsOnlyDigits().hasSize(11);
    }

    @Test
    @DisplayName("Test the custom CPF generation, with formatting")
    void testCpfCustomGeneration() {
        String result = generator.formatted().generate(random);
        assertThat(result).containsPattern(Pattern.compile("^(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})$")).hasSize(14);
    }
}
