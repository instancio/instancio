/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.generators;

import org.instancio.generator.specs.EanSpec;
import org.instancio.generator.specs.IsbnSpec;
import org.instancio.generator.specs.NipSpec;
import org.instancio.generator.specs.PeselSpec;
import org.instancio.generator.specs.RegonSpec;
import org.instancio.internal.generator.domain.id.EanGenerator;
import org.instancio.internal.generator.domain.id.IsbnGenerator;
import org.instancio.internal.generator.domain.id.NipGenerator;
import org.instancio.internal.generator.domain.id.PeselGenerator;
import org.instancio.internal.generator.domain.id.RegonGenerator;

/**
 * Provides generators for various types of identifiers.
 *
 * @since 2.11.0
 */
public final class IdSpecs {

    /**
     * Generates European Article Number (EAN).
     *
     * @return API builder reference
     * @since 2.11.0
     */
    public EanSpec ean() {
        return new EanGenerator();
    }

    /**
     * Generates ISBN numbers.
     *
     * @return API builder reference
     * @since 2.11.0
     */
    public IsbnSpec isbn() {
        return new IsbnGenerator();
    }

    /**
     * Generates Polish VAT Identification Number (NIP).
     *
     * @return API builder reference
     * @since 3.1.0
     */
    public NipSpec nip() {
        return new NipGenerator();
    }

    /**
     * Generates Polish National Identification Number (PESEL).
     *
     * @return API builder reference
     * @since 3.1.0
     */
    public PeselSpec pesel() {
        return new PeselGenerator();
    }

    /**
     * Generates Polish Taxpayer Identification Number (REGON).
     *
     * @return API builder reference
     * @since 3.1.0
     */
    public RegonSpec regon() {
        return new RegonGenerator();
    }
}
