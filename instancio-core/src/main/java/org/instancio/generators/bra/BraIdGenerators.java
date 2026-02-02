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
package org.instancio.generators.bra;

import org.instancio.generator.specs.bra.CnpjGeneratorSpec;
import org.instancio.generator.specs.bra.CpfGeneratorSpec;
import org.instancio.generator.specs.bra.TituloEleitoralGeneratorSpec;

/**
 * Contains built-in generators for Brazilian identifiers.
 *
 * @since 5.0.0
 */
public interface BraIdGenerators {

    /**
     * Generates Cadastro de Pessoa Fisica (CPF).
     *
     * @return API builder reference
     * @since 5.0.0
     */
    CpfGeneratorSpec cpf();

    /**
     * Generates Cadastro Nacional de Pessoas Jurídicas (CNPJ).
     *
     * @return API builder reference
     * @since 5.0.0
     */
    CnpjGeneratorSpec cnpj();

    /**
     * Generates Titulo Eleitoral
     *
     * @return API builder reference
     * @since 5.0.0
     */
    TituloEleitoralGeneratorSpec tituloEleitoral();
}
