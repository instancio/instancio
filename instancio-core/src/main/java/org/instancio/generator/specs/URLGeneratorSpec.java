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

import org.instancio.generator.AsStringGeneratorSpec;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;

import java.net.URL;

/**
 * Generator spec for {@link URL}.
 *
 * @since 2.3.0
 */
public interface URLGeneratorSpec extends GeneratorSpec<URL>, AsStringGeneratorSpec<URL> {

    /**
     * Generate a random protocol from the given choices.
     * If not specified, the default is HTTP.
     *
     * @param protocols one or more values from which
     *                  a random protocol will be selected
     * @return spec builder
     * @since 2.3.0
     */
    URLGeneratorSpec protocol(String... protocols);

    /**
     * Specifies the port number.
     * If not specified, default port {@code -1} will be used.
     *
     * @param port port number to use
     * @return spec builder
     * @since 2.3.0
     */
    URLGeneratorSpec port(int port);

    /**
     * Specifies that a random port number between
     * 1 and 65535 (inclusive) should be generated.
     *
     * @return spec builder
     * @since 2.3.0
     */
    URLGeneratorSpec randomPort();

    /**
     * Specifies a generator for the host name.
     * If not specified, a random host name will be generated.
     *
     * @param hostGenerator generator for the host name
     * @return spec builder
     * @since 2.3.0
     */
    URLGeneratorSpec host(Generator<String> hostGenerator);

    /**
     * Specifies a generator for the file name.
     * If not specified, blank file name will be used.
     *
     * @param fileGenerator generator for the file
     * @return spec builder
     * @since 2.3.0
     */
    URLGeneratorSpec file(Generator<String> fileGenerator);
}
