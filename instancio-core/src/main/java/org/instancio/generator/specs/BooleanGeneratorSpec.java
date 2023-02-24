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
package org.instancio.generator.specs;

import org.instancio.generator.GeneratorSpec;

/**
 * Generator spec for booleans.
 *
 * @since 2.0.0
 */
public interface BooleanGeneratorSpec extends GeneratorSpec<Boolean>, NullableGeneratorSpec<Boolean> {

    /**
     * Specifies the probability of generating {@code true}.
     *
     * @param probability of generating {@code true} between 0 and 1 inclusive
     * @return spec builder
     * @since 2.11.0
     */
    BooleanGeneratorSpec probability(double probability);

    /**
     * {@inheritDoc}
     *
     * @since 2.0.0
     */
    @Override
    BooleanGeneratorSpec nullable();
}
