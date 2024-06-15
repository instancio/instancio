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
package org.instancio.generator.specs.can;

import org.instancio.generator.specs.AsGeneratorSpec;
import org.instancio.generator.specs.NullableGeneratorSpec;

/**
 * Spec for generating <a href="https://en.wikipedia.org/wiki/Social_insurance_number">
 * Canadian Social Insurance Number</a> (SIN).
 *
 * @since 3.1.0
 */
public interface SinGeneratorSpec extends
        AsGeneratorSpec<String>,
        NullableGeneratorSpec<String> {

    /**
     * Generates a permanent card number with
     * the first digit {@code 1-7}, inclusive.
     *
     * @return spec builder
     * @since 3.1.0
     */
    SinGeneratorSpec permanent();

    /**
     * Generates a temporary card number
     * with the first digit {@code 9}.
     *
     * @return spec builder
     * @since 3.1.0
     */
    SinGeneratorSpec temporary();

    /**
     * Separator for groups of three digits.
     *
     * @param separator for separating groups of three digits,
     *                  default is {@code null} (groups are not separated)
     * @return spec builder
     * @since 3.1.0
     */
    SinGeneratorSpec separator(String separator);

    /**
     * {@inheritDoc}
     *
     * @since 3.1.0
     */
    @Override
    SinGeneratorSpec nullable();
}
