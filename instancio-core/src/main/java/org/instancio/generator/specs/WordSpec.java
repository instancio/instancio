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
package org.instancio.generator.specs;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.ValueSpec;

/**
 * Spec for generating English words.
 *
 * @since 5.1.0
 */
@ExperimentalApi
public interface WordSpec extends ValueSpec<String>, WordGeneratorSpec {

    /**
     * {@inheritDoc}
     *
     * @since 5.1.0
     */
    @ExperimentalApi
    @Override
    WordSpec adjective();

    /**
     * {@inheritDoc}
     *
     * @since 5.1.0
     */
    @ExperimentalApi
    @Override
    WordSpec adverb();

    /**
     * {@inheritDoc}
     *
     * @since 5.1.0
     */
    @ExperimentalApi
    @Override
    WordSpec noun();

    /**
     * {@inheritDoc}
     *
     * @since 5.1.0
     */
    @ExperimentalApi
    @Override
    WordSpec verb();

    /**
     * {@inheritDoc}
     *
     * @since 5.1.0
     */
    @ExperimentalApi
    @Override
    WordSpec nullable();
}
