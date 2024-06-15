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
package org.instancio.generators.pol;

import org.instancio.generator.specs.pol.NipGeneratorSpec;
import org.instancio.generator.specs.pol.PeselGeneratorSpec;
import org.instancio.generator.specs.pol.RegonGeneratorSpec;

/**
 * Contains built-in generators for Polish identifiers.
 *
 * @since 3.1.0
 */
public interface PolIdGenerators {

    /**
     * Generates Polish VAT Identification Number (NIP).
     *
     * @return API builder reference
     * @since 3.1.0
     */
    NipGeneratorSpec nip();

    /**
     * Generates Polish National Identification Number (PESEL).
     *
     * @return API builder reference
     * @since 3.1.0
     */
    PeselGeneratorSpec pesel();

    /**
     * Generates Polish Taxpayer Identification Number (REGON).
     *
     * @return API builder reference
     * @since 3.1.0
     */
    RegonGeneratorSpec regon();
}
