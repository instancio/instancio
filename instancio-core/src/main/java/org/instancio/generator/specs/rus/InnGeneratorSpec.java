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
package org.instancio.generator.specs.rus;

import org.instancio.generator.specs.NullableGeneratorSpec;

/**
 * Spec for generating <a href="https://w.wiki/9iyP">
 * Russian taxpayer identification number'</a> (INN).
 *
 * @since 5.0.0
 */
public interface InnGeneratorSpec extends NullableGeneratorSpec<String> {

    /**
     * Generate an individual INN.
     * @return spec builder
     * @since 5.0.0
     */
    InnGeneratorSpec individual();

    /**
     * Generate a juridical INN.
     * @return spec builder
     * @since 5.0.0
     */
    InnGeneratorSpec juridical();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    InnGeneratorSpec nullable();
}
