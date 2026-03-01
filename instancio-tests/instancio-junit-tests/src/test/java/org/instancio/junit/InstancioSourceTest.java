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
package org.instancio.junit;

import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InstancioSourceTest {

    @InstancioSource(samples = 1)
    @ParameterizedTest
    void zeroArg() {
        assertThat(0).isZero(); // NOSONAR
    }

    @InstancioSource(samples = 5)
    @ParameterizedTest
    void oneArg(final String arg) {
        assertThat(arg).isNotBlank();
    }

    @InstancioSource(samples = 5)
    @ParameterizedTest
    void twoArgsSameType(final String first, final String second) {
        assertThat(first).isNotBlank().isNotEqualTo(second);
    }

    @InstancioSource(samples = 5)
    @ParameterizedTest
    void list(final List<String> list) {
        assertThat(list).isNotEmpty().allSatisfy(s -> assertThat(s).isNotBlank());
    }

    @InstancioSource(samples = 5)
    @ParameterizedTest
    void map(final Map<String, Integer> map) {
        assertThat(map).isNotEmpty();
        assertThat(map.keySet()).allSatisfy(s -> assertThat(s).isNotBlank());
        assertThat(map.values()).allSatisfy(i -> assertThat(i).isNotZero());
    }

    @SuppressWarnings("NullAway")
    @Nested
    class DifferentTypesWithSameFieldsTest {
        //@formatter:off
        private static class Entity { int id; boolean valid; String name; UUID group; }
        private static class Dto    { int id; boolean valid; String name; UUID group; }
        //@formatter:on

        @InstancioSource(samples = 5)
        @ParameterizedTest
        void differentTypesWithSameFields(final Entity entity, final Dto dto) {
            assertThat(entity).isNotNull();
            assertThat(dto).isNotNull();
            assertThat(entity).usingRecursiveComparison().isNotEqualTo(dto);
        }
    }

    @Nested
    class TwoArgsTest {
        //@formatter:off
        private static class First  { @Nullable String foo; }
        private static class Second { @Nullable String bar; }
        //@formatter:on

        @InstancioSource(samples = 5)
        @ParameterizedTest
        void twoArgs(final First first, final Second second) {
            assertThat(first).isNotNull();
            assertThat(second).isNotNull();
            assertThat(first.foo).isNotBlank();
            assertThat(second.bar).isNotBlank();
        }
    }

    @Nested
    class GenericsTest {
        private static class Generic<T, E> {
            @Nullable T first;
            @Nullable List<E> second;
        }

        @InstancioSource(samples = 5)
        @ParameterizedTest
        void customGeneric(final Generic<String, UUID> arg) {
            assertThat(arg).isNotNull();
            assertThat(arg.first).isNotBlank();
            assertThat(arg.second).isNotEmpty().doesNotContainNull();
        }
    }

    @Nested
    class FeedTest {
        private static class Pojo {
            @Nullable String value;
        }

        @Feed.Source(string = "id\n123")
        private interface SampleFeed extends Feed {
            FeedSpec<Integer> id();
        }

        @InstancioSource(samples = 5)
        @ParameterizedTest
        void feedSpec(final SampleFeed feed) {
            assertThat(feed.id().get()).isEqualTo(123);
        }

        @InstancioSource(samples = 5)
        @ParameterizedTest
        void feedSpecAndPojo(final SampleFeed feed, final Pojo pojo) {
            assertThat(feed.id().get()).isEqualTo(123);
            assertThat(pojo.value).isNotBlank();
        }
    }
}
