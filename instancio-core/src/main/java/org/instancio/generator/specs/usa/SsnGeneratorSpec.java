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
package org.instancio.generator.specs.usa;

import org.instancio.generator.specs.NullableGeneratorSpec;

/**
 * Spec for generating <a href="https://en.wikipedia.org/wiki/Social_Security_number">
 * US Social Security Number</a> (SSN).
 *
 * @since 3.1.0
 */
public interface SsnGeneratorSpec extends NullableGeneratorSpec<String> {

    /**
     * Generates an SSN formatted with the given separator.
     * For example, if the separator is {@code '-'}, the SSN
     * will be formatted as follows: {@code 111-11-1111}.
     *
     * @param separator for separating groups of digits,
     *                  default is {@code null} (groups are not separated)
     * @return spec builder
     * @since 3.1.0
     */
    SsnGeneratorSpec separator(String separator);

    /**
     * {@inheritDoc}
     *
     * @since 3.1.0
     */
    @Override
    SsnGeneratorSpec nullable();
}
