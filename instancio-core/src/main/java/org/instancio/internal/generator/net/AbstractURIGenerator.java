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
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

abstract class AbstractURIGenerator<T> extends AbstractGenerator<T> {
    private static final int DEFAULT_PORT = -1;
    private static final int WITH_RANDOM_PORT = -2;
    private static final int PORT_MIN = 1;
    private static final int PORT_MAX = 65_535;
    private static final int HOST_MIN_LENGTH = 3;
    private static final int HOST_MAX_LENGTH = 12;
    private static final String DEFAULT_SCHEME = "http";

    private List<String> schemes = Collections.emptyList();
    private Generator<String> hostGenerator;
    private int port = DEFAULT_PORT;
    private Generator<String> pathGenerator;

    AbstractURIGenerator(final GeneratorContext context) {
        super(context);
    }

    final void withScheme(final String... schemes) {
        this.schemes = CollectionUtils.asList(schemes);
    }

    final void withHost(final Generator<String> hostGenerator) {
        this.hostGenerator = hostGenerator;
    }

    final void withPath(final Generator<String> pathGenerator) {
        this.pathGenerator = pathGenerator;
    }

    final void withPort(final int port) {
        this.port = port;
    }

    final void withRandomPort() {
        withPort(WITH_RANDOM_PORT);
    }

    final String getHost(final Random random) {
        return hostGenerator == null
                ? random.lowerCaseAlphabetic(random.intRange(HOST_MIN_LENGTH, HOST_MAX_LENGTH))
                : hostGenerator.generate(random);
    }

    final String getPath(final Random random, final String defaultValue) {
        return pathGenerator == null ? defaultValue : pathGenerator.generate(random);
    }

    final String getScheme(final Random random) {
        return schemes.isEmpty() ? AbstractURIGenerator.DEFAULT_SCHEME : random.oneOf(schemes);
    }

    final int getPort(final Random random) {
        return port == WITH_RANDOM_PORT
                ? random.intRange(PORT_MIN, PORT_MAX)
                : port;
    }
}
