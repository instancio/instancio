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

import org.junit.jupiter.params.ParameterizedTest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InstancioSourceTest {

    @InstancioSource
    @ParameterizedTest
    void zeroArg() {
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
    void twoArgs(final First first, final Second second) {
        assertThat(first).isNotNull();
        assertThat(second).isNotNull();
        assertThat(first.foo).isNotBlank();
        assertThat(second.bar).isNotBlank();
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

    @InstancioSource
    @ParameterizedTest
    void customGeneric(final Generic<String, UUID> arg) {
        assertThat(arg).isNotNull();
        assertThat(arg.first).isNotBlank();
        assertThat(arg.second).isNotEmpty().doesNotContainNull();
    }

    @InstancioSource
    @ParameterizedTest
    void differentTypesWithSameFields(final Entity entity, final Dto dto) {
        assertThat(entity).isNotNull();
        assertThat(dto).isNotNull();
        assertThat(entity).usingRecursiveComparison().isNotEqualTo(dto);
    }

    static class First {
        String foo;

        void setFoo(final String foo) {
            this.foo = foo;
        }
    }

    static class Second {
        String bar;

        void setBar(final String bar) {
            this.bar = bar;
        }
    }

    static class Generic<T, E> {
        T first;
        List<E> second;

        public void setFirst(final T first) {
            this.first = first;
        }

        public void setSecond(final List<E> second) {
            this.second = second;
        }
    }

    static class Entity {
        int id;
        boolean valid;
        String name;
        UUID group;

        public void setId(final int id) {
            this.id = id;
        }

        public void setValid(final boolean valid) {
            this.valid = valid;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public void setGroup(final UUID group) {
            this.group = group;
        }
    }

    static class Dto {
        int id;
        boolean valid;
        String name;
        UUID group;

        public void setId(final int id) {
            this.id = id;
        }

        public void setValid(final boolean valid) {
            this.valid = valid;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public void setGroup(final UUID group) {
            this.group = group;
        }
    }
}
