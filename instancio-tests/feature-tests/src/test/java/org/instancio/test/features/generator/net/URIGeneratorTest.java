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
package org.instancio.test.features.generator.net;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Percentage.withPercentage;
import static org.instancio.Select.root;

@FeatureTag(Feature.URI_GENERATOR)
class URIGeneratorTest {
    private static final int SAMPLE_SIZE = 200;

    @Test
    void create() {
        final URI result = Instancio.create(URI.class);

        assertThat(result.getHost()).hasSizeBetween(3, 12);
        assertThat(result)
                .hasScheme("http")
                .hasNoUserInfo()
                .hasPort(-1)
                .hasPath("")
                .hasNoQuery()
                .hasNoFragment();
    }

    @Test
    void createFull() {
        final URI result = Instancio.of(URI.class)
                .generate(root(), gen -> gen.net().uri()
                        .scheme("scheme")
                        .userInfo("userInfo")
                        .host(random -> "host")
                        .port(1234)
                        .path(random -> "/path")
                        .query(random -> "query")
                        .fragment(random -> "fragment"))
                .create();

        assertThat(result)
                .hasScheme("scheme")
                .hasUserInfo("userInfo")
                .hasHost("host")
                .hasPort(1234)
                .hasPath("/path")
                .hasQuery("query")
                .hasFragment("fragment");
    }

    @Test
    void withRandomPort() {
        final Set<Integer> results = Instancio.of(URI.class)
                .generate(root(), gen -> gen.net().uri().randomPort())
                .stream()
                .limit(SAMPLE_SIZE)
                .map(URI::getPort)
                .collect(Collectors.toSet());

        assertThat(results.size()).isCloseTo(SAMPLE_SIZE, withPercentage(5));
    }

    @Test
    void withSchemes() {
        final String[] schemes = {"foo", "bar", "baz"};
        final Set<String> result = Instancio.of(URI.class)
                .generate(root(), gen -> gen.net().uri().scheme(schemes))
                .stream()
                .limit(SAMPLE_SIZE)
                .map(URI::getScheme)
                .collect(Collectors.toSet());

        assertThat(result).containsExactlyInAnyOrder(schemes);
    }

    @Test
    void invalidScheme() {
        final String invalidScheme = "foo!";
        final InstancioApi<URI> api = Instancio.of(URI.class)
                .generate(root(), gen -> gen.net().uri()
                        .port(123)
                        .host(random -> "example.com")
                        .scheme(invalidScheme));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining(String.format("error generating a URI using parameters: %n" +
                        "  scheme: 'foo!'%n" +
                        "  userInfo: null%n" +
                        "  host: 'example.com'%n" +
                        "  port: 123%n" +
                        "  path: null%n" +
                        "  query: null%n" +
                        "  fragment: null"));
    }

    @Test
    void asString() {
        final String result = Instancio.of(String.class)
                .generate(root(), gen -> gen.net().uri()
                        .scheme("scheme")
                        .userInfo("userInfo")
                        .host(random -> "host")
                        .port(1234)
                        .path(random -> "/path")
                        .query(random -> "query")
                        .fragment(random -> "fragment")
                        .asString())
                .create();

        assertThat(result).isEqualTo("scheme://userInfo@host:1234/path?query#fragment");
    }

    @Test
    void nullable() {
        final Stream<URI> results = Instancio.of(URI.class)
                .generate(root(), gen -> gen.net().uri().nullable())
                .stream()
                .limit(Constants.SAMPLE_SIZE_DD);

        assertThat(results)
                .containsNull()
                .anyMatch(Objects::nonNull);
    }
}