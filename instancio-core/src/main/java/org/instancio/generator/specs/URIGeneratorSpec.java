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

import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;

import java.net.URI;

/**
 * Generator spec for {@link URI}.
 *
 * @since 2.3.0
 */
public interface URIGeneratorSpec extends GeneratorSpec<URI> {

    /**
     * Generate a random scheme from the given choices.
     * If not specified, the default is HTTP.
     *
     * @param schemes one or more values from which
     *                a random scheme will be selected
     * @return spec builder
     * @since 2.3.0
     */
    URIGeneratorSpec scheme(String... schemes);

    /**
     * Specifies username.
     * If not specified, a {@code null} user will be generated.
     *
     * @param userInfo username to generate
     * @return spec builder
     * @since 2.3.0
     */
    URIGeneratorSpec userInfo(String userInfo);

    /**
     * Specifies a generator for the host name.
     * If not specified, a random host name will be generated.
     *
     * @param hostGenerator generator for the host name
     * @return spec builder
     * @since 2.3.0
     */
    URIGeneratorSpec host(Generator<String> hostGenerator);

    /**
     * Specifies the port number.
     * If not specified, default port {@code -1} will be used.
     *
     * @param port port number to use
     * @return spec builder
     * @since 2.3.0
     */
    URIGeneratorSpec port(int port);

    /**
     * Specifies that a random port number between
     * 1 and 65535 (inclusive) should be generated.
     *
     * @return spec builder
     * @since 2.3.0
     */
    URIGeneratorSpec randomPort();

    /**
     * Specifies a generator for the path.
     * If not specified, {@code null} path will be generated.
     *
     * @param pathGenerator generator for the path
     * @return spec builder
     * @since 2.3.0
     */
    URIGeneratorSpec path(Generator<String> pathGenerator);

    /**
     * Specifies a generator for the query.
     * If not specified, {@code null} query will be generated.
     *
     * @param queryGenerator generator for the query
     * @return spec builder
     * @since 2.3.0
     */
    URIGeneratorSpec query(Generator<String> queryGenerator);

    /**
     * Specifies a generator for the fragmentGenerator.
     * If not specified, {@code null} fragmentGenerator will be generated.
     *
     * @param fragmentGenerator generator for the fragment
     * @return spec builder
     * @since 2.3.0
     */
    URIGeneratorSpec fragment(Generator<String> fragmentGenerator);
}
