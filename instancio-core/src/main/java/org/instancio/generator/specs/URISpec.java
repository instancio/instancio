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

import org.instancio.generator.Generator;
import org.instancio.generator.ValueSpec;

import java.net.URI;

/**
 * Spec for generating {@link URI} values.
 *
 * @since 2.6.0
 */
public interface URISpec extends ValueSpec<URI>, URIGeneratorSpec {

    /**
     * {@inheritDoc}
     */
    @Override
    URISpec scheme(String... schemes);

    /**
     * {@inheritDoc}
     */
    @Override
    URISpec userInfo(String userInfo);

    /**
     * {@inheritDoc}
     */
    @Override
    URISpec host(Generator<String> hostGenerator);

    /**
     * {@inheritDoc}
     */
    @Override
    URISpec port(int port);

    /**
     * {@inheritDoc}
     */
    @Override
    URISpec randomPort();

    /**
     * {@inheritDoc}
     */
    @Override
    URISpec path(Generator<String> pathGenerator);

    /**
     * {@inheritDoc}
     */
    @Override
    URISpec query(Generator<String> queryGenerator);

    /**
     * {@inheritDoc}
     */
    @Override
    URISpec fragment(Generator<String> fragmentGenerator);

    /**
     * {@inheritDoc}
     */
    @Override
    URISpec nullable();
}
