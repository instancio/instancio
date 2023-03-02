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
package org.instancio.internal.generator.net;

import org.instancio.Random;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.URIAsGeneratorSpec;
import org.instancio.generator.specs.URISpec;
import org.instancio.support.Global;

import java.net.URI;
import java.net.URISyntaxException;

import static org.instancio.internal.util.StringUtils.singleQuote;

public class URIGenerator extends AbstractURIGenerator<URI>
        implements URISpec, URIAsGeneratorSpec {

    private String userInfo;
    private Generator<String> queryGenerator;
    private Generator<String> fragmentGenerator;

    public URIGenerator() {
        this(Global.generatorContext());
    }

    public URIGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "uri()";
    }

    @Override
    public URIGenerator scheme(final String... schemes) {
        withScheme(schemes);
        return this;
    }

    @Override
    public URIGenerator userInfo(final String userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    @Override
    public URIGenerator host(final Generator<String> hostGenerator) {
        withHost(hostGenerator);
        return this;
    }

    @Override
    public URIGenerator port(final int port) {
        withPort(port);
        return this;
    }

    @Override
    public URIGenerator randomPort() {
        withRandomPort();
        return this;
    }

    @Override
    public URIGenerator path(final Generator<String> path) {
        withPath(path);
        return this;
    }

    @Override
    public URIGenerator query(final Generator<String> queryGenerator) {
        this.queryGenerator = queryGenerator;
        return this;
    }

    @Override
    public URIGenerator fragment(final Generator<String> fragmentGenerator) {
        this.fragmentGenerator = fragmentGenerator;
        return this;
    }

    @Override
    public URIGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected URI tryGenerateNonNull(final Random random) {
        final String scheme = getScheme(random);
        final int port = getPort(random);
        final String host = getHost(random);
        final String path = getPath(random, null);
        final String query = queryGenerator == null ? null : queryGenerator.generate(random);
        final String fragment = fragmentGenerator == null ? null : fragmentGenerator.generate(random);

        try {
            return new URI(scheme, userInfo, host, port, path, query, fragment);
        } catch (URISyntaxException ex) {
            final String params = String.format("%n  scheme: %s"
                            + "%n  userInfo: %s"
                            + "%n  host: %s"
                            + "%n  port: %s"
                            + "%n  path: %s"
                            + "%n  query: %s"
                            + "%n  fragment: %s",
                    singleQuote(scheme), singleQuote(userInfo), singleQuote(host),
                    port, singleQuote(path), singleQuote(query), singleQuote(fragment));

            throw new InstancioApiException("Error generating a URI using parameters: " + params, ex);
        }
    }
}
