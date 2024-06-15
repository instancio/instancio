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
 * Spec for generating hashes
 *
 * @since 2.11.0
 */
public interface HashGeneratorSpec extends
        AsGeneratorSpec<String>,
        NullableGeneratorSpec<String> {

    /**
     * Specifies that an MD5 hash should be generated.
     *
     * @return spec builder
     * @since 2.11.0
     */
    HashGeneratorSpec md5();

    /**
     * Specifies that a SHA-1 hash should be generated.
     *
     * @return spec builder
     * @since 2.11.0
     */
    HashGeneratorSpec sha1();

    /**
     * Specifies that a SHA-256 hash should be generated.
     *
     * @return spec builder
     * @since 2.11.0
     */
    HashGeneratorSpec sha256();

    /**
     * Specifies that a SHA-512 hash should be generated.
     *
     * @return spec builder
     * @since 2.11.0
     */
    HashGeneratorSpec sha512();

    /**
     * {@inheritDoc}
     *
     * @since 2.11.0
     */
    @Override
    HashGeneratorSpec nullable();
}
