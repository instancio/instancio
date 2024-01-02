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

    @Override
    URISpec scheme(String... schemes);

    @Override
    URISpec userInfo(String userInfo);

    @Override
    URISpec host(Generator<String> hostGenerator);

    @Override
    URISpec port(int port);

    @Override
    URISpec randomPort();

    @Override
    URISpec path(Generator<String> pathGenerator);

    @Override
    URISpec query(Generator<String> queryGenerator);

    @Override
    URISpec fragment(Generator<String> fragmentGenerator);

    @Override
    URISpec nullable();
}
