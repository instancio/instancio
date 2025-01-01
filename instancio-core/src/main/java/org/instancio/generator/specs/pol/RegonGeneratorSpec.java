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
package org.instancio.generator.specs.pol;

import org.instancio.generator.specs.NullableGeneratorSpec;

/**
 * Spec for generating <a href="https://pl.wikipedia.org/wiki/REGON">Polish Taxpayer Identification Number (REGON)</a>.
 *
 * @since 3.1.0
 */
public interface RegonGeneratorSpec extends NullableGeneratorSpec<String> {

    /**
     * Specifies that 9-digit REGON should be generated
     * (default behaviour if the type is not specified).
     *
     * @return spec builder
     * @since 3.1.0
     */
    RegonGeneratorSpec type9();

    /**
     * Specifies that 14-digit REGON should be generated.
     *
     * @return spec builder
     * @since 3.1.0
     */
    RegonGeneratorSpec type14();

    /**
     * {@inheritDoc}
     *
     * @since 3.1.0
     */
    @Override
    RegonGeneratorSpec nullable();
}
