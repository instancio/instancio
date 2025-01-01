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
package org.instancio.generator.specs;

import org.instancio.documentation.ExperimentalApi;

/**
 * Spec for generating English words.
 *
 * @since 5.1.0
 */
@ExperimentalApi
public interface WordGeneratorSpec extends
        AsGeneratorSpec<String>,
        NullableGeneratorSpec<String> {

    /**
     * Specifies that an adjective should be generated.
     *
     * @return spec builder
     * @since 5.1.0
     */
    @ExperimentalApi
    WordGeneratorSpec adjective();

    /**
     * Specifies that an adverb should be generated.
     *
     * @return spec builder
     * @since 5.1.0
     */
    @ExperimentalApi
    WordGeneratorSpec adverb();

    /**
     * Specifies that a noun should be generated.
     *
     * @return spec builder
     * @since 5.1.0
     */
    @ExperimentalApi
    WordGeneratorSpec noun();

    /**
     * Specifies that a verb should be generated.
     *
     * @return spec builder
     * @since 5.1.0
     */
    @ExperimentalApi
    WordGeneratorSpec verb();

    /**
     * {@inheritDoc}
     *
     * @since 5.1.0
     */
    @Override
    @ExperimentalApi
    WordGeneratorSpec nullable();
}
