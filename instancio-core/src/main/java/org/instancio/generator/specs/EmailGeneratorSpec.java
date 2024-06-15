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

/**
 * Spec for generating email addresses.
 *
 * @since 2.11.0
 */
public interface EmailGeneratorSpec extends
        AsGeneratorSpec<String>,
        NullableGeneratorSpec<String> {

    /**
     * Specifies email address length.
     *
     * @param length of email address
     * @return spec builder
     * @since 2.11.0
     */
    EmailGeneratorSpec length(int length);

    /**
     * Specifies email address length range.
     *
     * @param min minimum length of address (inclusive)
     * @param max maximum length of address (inclusive)
     * @return spec builder
     * @since 2.11.0
     */
    EmailGeneratorSpec length(int min, int max);

    /**
     * {@inheritDoc}
     *
     * @since 2.11.0
     */
    @Override
    EmailGeneratorSpec nullable();
}
