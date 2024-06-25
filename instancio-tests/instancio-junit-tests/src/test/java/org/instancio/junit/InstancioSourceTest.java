/*
 *  Copyright 2022-2024 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.junit;

import org.instancio.schema.Schema;
import org.instancio.schema.SchemaResource;
import org.instancio.schema.SchemaSpec;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InstancioSourceTest {

    @InstancioSource
    @ParameterizedTest
    void zeroArg() {
        assertThat(0).isZero(); // NOSONAR
    }

    @InstancioSource
    @ParameterizedTest
    void oneArg(final String arg) {
        assertThat(arg).isNotBlank();
    }

    @InstancioSource
    @ParameterizedTest
    void twoArgsSameType(final String first, final String second) {
        assertThat(first).isNotBlank().isNotEqualTo(second);
    }

    @InstancioSource
    @ParameterizedTest
    void list(final List<String> list) {
        assertThat(list).isNotEmpty().allSatisfy(s -> assertThat(s).isNotBlank());
    }

    @InstancioSource
    @ParameterizedTest
    void map(final Map<String, Integer> map) {
        assertThat(map).isNotEmpty();
        assertThat(map.keySet()).allSatisfy(s -> assertThat(s).isNotBlank());
        assertThat(map.values()).allSatisfy(i -> assertThat(i).isNotZero());
    }

    @Nested
    class DifferentTypesWithSameFieldsTest {
        //@formatter:off
        private static class Entity { int id; boolean valid; String name; UUID group; }
        private static class Dto    { int id; boolean valid; String name; UUID group; }
        //@formatter:on

        @InstancioSource
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
        private static class First  { String foo; }
        private static class Second { String bar; }
        //@formatter:on

        @InstancioSource
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
            T first;
            List<E> second;
        }

        @InstancioSource
        @ParameterizedTest
        void customGeneric(final Generic<String, UUID> arg) {
            assertThat(arg).isNotNull();
            assertThat(arg.first).isNotBlank();
            assertThat(arg.second).isNotEmpty().doesNotContainNull();
        }
    }

    @Nested
    class SchemaTest {
        private static class Pojo {
            String value;
        }

        @SchemaResource(data = "id\n123")
        private interface SampleSpec extends Schema {
            SchemaSpec<Integer> id();
        }

        @InstancioSource
        @ParameterizedTest
        void dataSpec(final SampleSpec spec) {
            assertThat(spec.id().get()).isEqualTo(123);
        }

        @InstancioSource
        @ParameterizedTest
        void dataSpecAndPojo(final SampleSpec spec, final Pojo pojo) {
            assertThat(spec.id().get()).isEqualTo(123);
            assertThat(pojo.value).isNotBlank();
        }
    }
}
