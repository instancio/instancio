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
import org.instancio.generator.specs.URLGeneratorSpec;

import java.net.MalformedURLException;
import java.net.URL;

import static org.instancio.internal.util.StringUtils.singleQuote;

public class URLGenerator extends AbstractURIGenerator<URL> implements URLGeneratorSpec {

    public URLGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "url()";
    }

    @Override
    public URLGeneratorSpec protocol(final String... protocols) {
        withScheme(protocols);
        return this;
    }

    @Override
    public URLGeneratorSpec port(final int port) {
        withPort(port);
        return this;
    }

    @Override
    public URLGeneratorSpec randomPort() {
        withRandomPort();
        return this;
    }

    @Override
    public URLGeneratorSpec host(final Generator<String> hostGenerator) {
        withHost(hostGenerator);
        return this;
    }

    @Override
    public URLGeneratorSpec file(final Generator<String> fileGenerator) {
        withPath(fileGenerator);
        return this;
    }

    @Override
    public URL generate(final Random random) {
        final String protocol = getScheme(random);
        final String host = getHost(random);
        final int portNumber = getPort(random);
        final String file = getPath(random, "");

        try {
            return new URL(protocol, host, portNumber, file);
        } catch (MalformedURLException ex) {
            final String params = String.format("%n  protocol: %s"
                            + "%n  host: %s"
                            + "%n  port: %s"
                            + "%n  file: %s",
                    singleQuote(protocol),
                    singleQuote(host),
                    portNumber,
                    singleQuote(file));

            throw new InstancioApiException("Error generating a URL using parameters: " + params, ex);
        }
    }
}
