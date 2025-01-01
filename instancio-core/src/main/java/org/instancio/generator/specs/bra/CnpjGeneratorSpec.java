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
package org.instancio.generator.specs.bra;

import org.instancio.generator.specs.NullableGeneratorSpec;

/**
 * Spec for generating <a href="https://en.wikipedia.org/wiki/CNPJ">
 * Brazilian 'Cadastro Nacional de Pessoas Jurídicas'</a> (CNPJ).
 *
 * @since 5.0.0
 */
public interface CnpjGeneratorSpec extends NullableGeneratorSpec<String> {

    /**
     * Format the CNPJ output.
     * By default, the output is not formatted: {@code '123456780000199'}
     * In case the formatted is called, the result will be formatted as follows: {@code '12.345.678/00001-99'}
     *
     * @return spec builder
     * @since 5.0.0
     */
    CnpjGeneratorSpec formatted();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    CnpjGeneratorSpec nullable();
}
