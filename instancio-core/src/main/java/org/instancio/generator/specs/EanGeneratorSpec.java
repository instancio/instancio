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
 * Spec for generating
 * <a href="https://en.wikipedia.org/wiki/International_Article_Number">European Article Number</a>
 * (EAN).
 *
 * @since 2.11.0
 */
public interface EanGeneratorSpec extends NullableGeneratorSpec<String> {

    /**
     * Specifies that EAN-8 should be generated.
     *
     * @return spec builder
     * @since 2.11.0
     */
    EanGeneratorSpec type8();

    /**
     * Specifies that EAN-13 should be generated
     * (default behaviour if the type is not specified).
     *
     * @return spec builder
     * @since 2.11.0
     */
    EanGeneratorSpec type13();

    /**
     * {@inheritDoc}
     *
     * @since 2.11.0
     */
    @Override
    EanGeneratorSpec nullable();
}
