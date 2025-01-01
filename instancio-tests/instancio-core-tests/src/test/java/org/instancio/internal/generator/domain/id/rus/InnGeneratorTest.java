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
package org.instancio.internal.generator.domain.id.rus;

import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InnGeneratorTest extends AbstractGeneratorTestTemplate<String, InnGenerator> {

    private final InnGenerator generator = new InnGenerator(getGeneratorContext());

    @Override
    protected String getApiMethod() {
        return "inn()";
    }

    @Override
    protected InnGenerator generator() {
        return generator;
    }

    @Test
    @DisplayName("Test the default INN generation")
    void innDefaultGeneration() {
        String result = generator.generate(random);
        assertThat(result).hasSizeBetween(10, 12).containsOnlyDigits();
    }

    @Test
    @DisplayName("Test the INN generation of type individual")
    void innIndividualGeneration() {
        String result = generator.individual().generate(random);
        assertThat(result).hasSize(12).containsOnlyDigits();
    }

    @Test
    @DisplayName("Test the INN generation of type juridical")
    void innJuridicalGeneration() {
        String result = generator.juridical().generate(random);
        assertThat(result).hasSize(10).containsOnlyDigits();
    }
}
