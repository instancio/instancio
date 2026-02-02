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
package org.instancio.generators;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.specs.CsvSpec;
import org.instancio.generator.specs.LoremIpsumSpec;
import org.instancio.generator.specs.TextPatternSpec;
import org.instancio.generator.specs.UUIDStringSpec;
import org.instancio.generator.specs.WordSpec;
import org.instancio.generator.specs.WordTemplateSpec;

/**
 * Provides text generators.
 *
 * @since 5.0.0
 */
public interface TextSpecs extends TextGenerators {

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    @ExperimentalApi
    CsvSpec csv();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    LoremIpsumSpec loremIpsum();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    TextPatternSpec pattern(String pattern);

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    UUIDStringSpec uuid();

    /**
     * {@inheritDoc}
     *
     * @since 5.1.0
     */
    @Override
    @ExperimentalApi
    WordSpec word();

    /**
     * {@inheritDoc}
     *
     * @since 5.2.0
     */
    @Override
    @ExperimentalApi
    WordTemplateSpec wordTemplate(String template);
}
