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

package org.instancio.generators.pol;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.pol.NipGeneratorSpec;
import org.instancio.generator.specs.pol.PeselGeneratorSpec;
import org.instancio.generator.specs.pol.RegonGeneratorSpec;
import org.instancio.internal.generator.domain.id.pol.NipGenerator;
import org.instancio.internal.generator.domain.id.pol.PeselGenerator;
import org.instancio.internal.generator.domain.id.pol.RegonGenerator;

/**
 * Contains built-in generators for Polish identifiers.
 *
 * @since 3.1.0
 */
public class PolIdGenerators {

    private final GeneratorContext context;

    public PolIdGenerators(final GeneratorContext context) {
        this.context = context;
    }

    /**
     * Generates Polish VAT Identification Number (NIP).
     *
     * @return API builder reference
     * @since 3.1.0
     */
    public NipGeneratorSpec nip() {
        return new NipGenerator(context);
    }

    /**
     * Generates Polish National Identification Number (PESEL).
     *
     * @return API builder reference
     * @since 3.1.0
     */
    public PeselGeneratorSpec pesel() {
        return new PeselGenerator(context);
    }

    /**
     * Generates Polish Taxpayer Identification Number (REGON).
     *
     * @return API builder reference
     * @since 3.1.0
     */
    public RegonGeneratorSpec regon() {
        return new RegonGenerator(context);
    }
}
