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
package org.instancio.test.features.generator.net;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.root;

@FeatureTag(Feature.URL_GENERATOR)
class URLGeneratorTest {
    private static final int SAMPLE_SIZE = 200;

    @Test
    void create() {
        final URL result = Instancio.create(URL.class);
        assertThat(result).hasProtocol("http").hasNoPath();
        assertThat(result.getPort()).isBetween(1, 65535);
        assertThat(result.getHost()).hasSizeBetween(3, 12);
    }

    @Test
    void withPort() {
        final int expected = 1234;
        final URL result = Instancio.of(URL.class)
                .generate(root(), gen -> gen.net().url().port(expected))
                .create();

        assertThat(result).hasPort(expected);
    }

    @Test
    void withHost() {
        final String expected = Instancio.create(String.class);
        final URL result = Instancio.of(URL.class)
                .generate(root(), gen -> gen.net().url().host(random -> expected))
                .create();

        assertThat(result).hasHost(expected);
    }

    @Test
    void withSingleProtocol() {
        final String expected = "ftp";
        final URL result = Instancio.of(URL.class)
                .generate(root(), gen -> gen.net().url().protocol(expected))
                .create();

        assertThat(result).hasProtocol(expected);
    }

    @Test
    void withOneOfProtocols() {
        final String[] expected = {"ftp", "file", "https"};
        final Set<String> results = Instancio.of(URL.class)
                .generate(root(), gen -> gen.net().url().protocol(expected))
                .stream()
                .limit(SAMPLE_SIZE)
                .map(URL::getProtocol)
                .collect(Collectors.toSet());

        assertThat(results).containsExactlyInAnyOrder(expected);
    }

    @Test
    void invalidProtocol() {
        final InstancioApi<URL> api = Instancio.of(URL.class)
                .generate(root(), gen -> gen.net().url()
                        .port(123)
                        .host(random -> "example.com")
                        .protocol("xyz"));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage(String.format("Error generating a URL using parameters: %n" +
                        "  protocol: 'xyz'%n" +
                        "  host: 'example.com'%n" +
                        "  port: 123"));
    }
}