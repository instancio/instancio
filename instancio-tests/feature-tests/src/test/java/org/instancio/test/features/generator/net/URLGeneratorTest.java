/*
 * Copyright 2022-2026 the original author or authors.
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
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.URL;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Percentage.withPercentage;
import static org.instancio.Select.root;

@FeatureTag(Feature.URL_GENERATOR)
@ExtendWith(InstancioExtension.class)
class URLGeneratorTest {
    private static final int SAMPLE_SIZE = 200;

    @Test
    void create() {
        final URL result = Instancio.create(URL.class);

        assertThat(result.getHost()).hasSizeBetween(3, 12);
        assertThat(result)
                .hasProtocol("http")
                .hasNoPath()
                .hasPort(-1);
    }

    @Test
    void createFull() {
        final URL result = Instancio.of(URL.class)
                .generate(root(), gen -> gen.net().url()
                        .protocol("ftp")
                        .host(random -> "host")
                        .port(1234)
                        .file(random -> "/filename"))
                .create();

        assertThat(result)
                .hasProtocol("ftp")
                .hasHost("host")
                .hasPort(1234)
                .hasPath("/filename");
    }

    @Test
    void withRandomPort() {
        final Set<Integer> results = Instancio.of(URL.class)
                .generate(root(), gen -> gen.net().url().randomPort())
                .stream()
                .limit(SAMPLE_SIZE)
                .map(URL::getPort)
                .collect(Collectors.toSet());

        assertThat(results.size()).isCloseTo(SAMPLE_SIZE, withPercentage(5));
    }

    @Test
    void withProtocols() {
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
                .hasMessageContaining(String.format("error generating a URL using parameters: %n" +
                        "  protocol: 'xyz'%n" +
                        "  host: 'example.com'%n" +
                        "  port: 123%n" +
                        "  file: ''"));
    }

    @Test
    void asString() {
        final String result = Instancio.of(String.class)
                .generate(root(), gen -> gen.net().url()
                        .protocol("ftp")
                        .host(random -> "host")
                        .port(1234)
                        .file(random -> "/filename")
                        .asString())
                .create();

        assertThat(result).isEqualTo("ftp://host:1234/filename");
    }

    @Test
    void nullable() {
        final Stream<URL> results = Instancio.of(URL.class)
                .generate(root(), gen -> gen.net().url().nullable())
                .stream()
                .limit(Constants.SAMPLE_SIZE_DD);

        assertThat(results)
                .containsNull()
                .anyMatch(Objects::nonNull);
    }
}