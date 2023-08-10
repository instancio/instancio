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

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.EanGeneratorSpec;
import org.instancio.generator.specs.IsbnGeneratorSpec;
import org.instancio.generators.pol.PolIdGenerators;
import org.instancio.internal.generator.domain.id.EanGenerator;
import org.instancio.internal.generator.domain.id.IsbnGenerator;

/**
 * Contains built-in generators for various types of identifiers.
 *
 * @since 2.11.0
 */
public class IdGenerators {

    private final GeneratorContext context;

    public IdGenerators(final GeneratorContext context) {
        this.context = context;
    }

    /**
     * Generates European Article Number (EAN).
     *
     * @return API builder reference
     * @since 2.11.0
     */
    public EanGeneratorSpec ean() {
        return new EanGenerator(context);
    }

    /**
     * Generates ISBN numbers.
     *
     * @return API builder reference
     * @since 2.11.0
     */
    public IsbnGeneratorSpec isbn() {
        return new IsbnGenerator(context);
    }

    /**
     * Provides access to identifier generators for Poland.
     *
     * @return built-in id generators for Poland
     * @since 3.1.0
     */
    public PolIdGenerators pol() {
        return new PolIdGenerators(context);
    }
}
