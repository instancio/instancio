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

import static org.assertj.core.api.Assertions.assertThat;

class TituloEleitoralGeneratorTest extends AbstractGeneratorTestTemplate<String, TituloEleitoralGenerator> {

    private final TituloEleitoralGenerator generator = new TituloEleitoralGenerator(getGeneratorContext());

    @Override
    protected String getApiMethod() {
        return "tituloEleitoral()";
    }

    @Override
    protected TituloEleitoralGenerator generator() {
        return generator;
    }

    @Test
    @DisplayName("Test the default Titulo Eleitoral generation")
    void cnpjDefaultGeneration() {
        assertThat(generator.generate(random)).containsOnlyDigits().hasSize(12);
    }

}
