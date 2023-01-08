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
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.internal.util.ObjectUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class URLGenerator extends AbstractGenerator<URL> implements URLGeneratorSpec {
    private static final int PORT_MIN = 1;
    private static final int PORT_MAX = 65_535;
    private static final int HOST_MIN_LENGTH = 3;
    private static final int HOST_MAX_LENGTH = 12;
    private static final String DEFAULT_PROTOCOL = "http";

    private List<String> protocols = Collections.emptyList();
    private Integer port;
    private Generator<String> hostGenerator;

    public URLGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "url()";
    }

    @Override
    public URLGeneratorSpec protocol(final String... protocols) {
        this.protocols = CollectionUtils.asList(protocols);
        return this;
    }

    @Override
    public URLGeneratorSpec port(final int port) {
        this.port = port;
        return this;
    }

    @Override
    public URLGeneratorSpec host(final Generator<String> hostGenerator) {
        this.hostGenerator = hostGenerator;
        return this;
    }

    @Override
    public URL generate(final Random random) {
        final String protocol = getProtocol(random);
        final String host = getHost(random);
        final int portNumber = getPortNumber(random);

        try {
            return new URL(protocol, host, portNumber, /* file = */ "");
        } catch (MalformedURLException ex) {
            final String params = String.format("%n  protocol: '%s'"
                    + "%n  host: '%s'"
                    + "%n  port: %s", protocol, host, portNumber);

            throw new InstancioApiException("Error generating a URL using parameters: " + params, ex);
        }
    }

    private int getPortNumber(final Random random) {
        return ObjectUtils.defaultIfNull(port, random.intRange(PORT_MIN, PORT_MAX));
    }

    private String getHost(final Random random) {
        return hostGenerator == null
                ? random.lowerCaseAlphabetic(random.intRange(HOST_MIN_LENGTH, HOST_MAX_LENGTH))
                : hostGenerator.generate(random);
    }

    private String getProtocol(final Random random) {
        return protocols.isEmpty() ? DEFAULT_PROTOCOL : random.oneOf(protocols);
    }
}
