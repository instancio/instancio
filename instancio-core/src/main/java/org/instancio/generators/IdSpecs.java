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
import org.instancio.generators.can.CanIdSpecs;
import org.instancio.generators.pol.PolIdSpecs;
import org.instancio.generators.usa.UsaIdSpecs;
import org.instancio.internal.generator.domain.id.EanGenerator;
import org.instancio.internal.generator.domain.id.IsbnGenerator;

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
     * Provides identifier generators for Canada.
     *
     * @return built-in identifier generators
     * @since 3.1.0
     */
    public CanIdSpecs can() {
        return new CanIdSpecs();
    }

    /**
     * Provides identifier generators for Poland.
     *
     * @return built-in identifier generators
     * @since 3.1.0
     */
    public PolIdSpecs pol() {
        return new PolIdSpecs();
    }

    /**
     * Provides identifier generators for USA.
     *
     * @return built-in identifier generators
     * @since 3.1.0
     */
    public UsaIdSpecs usa() {
        return new UsaIdSpecs();
    }
}
