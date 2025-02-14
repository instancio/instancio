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
package org.instancio.generators;

import org.instancio.generator.specs.EanSpec;
import org.instancio.generator.specs.IsbnSpec;
import org.instancio.generators.bra.BraIdSpecs;
import org.instancio.generators.can.CanIdSpecs;
import org.instancio.generators.pol.PolIdSpecs;
import org.instancio.generators.rus.RusIdSpecs;
import org.instancio.generators.usa.UsaIdSpecs;

/**
 * Provides generators for various types of identifiers.
 *
 * @since 5.0.0
 */
public interface IdSpecs extends IdGenerators {

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    EanSpec ean();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    IsbnSpec isbn();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    CanIdSpecs can();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    PolIdSpecs pol();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    UsaIdSpecs usa();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    BraIdSpecs bra();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    RusIdSpecs rus();
}
