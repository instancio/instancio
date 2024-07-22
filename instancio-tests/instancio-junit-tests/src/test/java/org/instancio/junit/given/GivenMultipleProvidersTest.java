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
package org.instancio.junit.given;

import org.instancio.junit.Given;
import org.instancio.junit.GivenProvider;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(InstancioExtension.class)
class GivenMultipleProvidersTest {

    private static final Set<String> results = new HashSet<>();

    @Order(1)
    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void shouldPickRandomProviders(@Given({Provider1.class, Provider2.class}) final String value) {
        assertThat(value).isNotBlank();
        results.add(value);
    }

    @Test
    void withSupplier(@Given({Provider1.class, Provider2.class}) final Supplier<String> supplier) {
        assertThat(Stream.generate(supplier).limit(Constants.SAMPLE_SIZE_DD))
                .containsOnly("foo", "bar");
    }

    @Test
    void withStream(@Given({Provider1.class, Provider2.class}) final Stream<String> stream) {
        assertThat(stream.limit(Constants.SAMPLE_SIZE_DD))
                .containsOnly("foo", "bar");
    }

    @Order(2)
    @Test
    void verify() {
        assertThat(results).containsOnly("foo", "bar");
    }

    private static class Provider1 implements GivenProvider {
        @Override
        public Object provide(final ElementContext context) {
            return "foo";
        }
    }

    private static class Provider2 implements GivenProvider {
        @Override
        public Object provide(final ElementContext context) {
            return "bar";
        }
    }
}
