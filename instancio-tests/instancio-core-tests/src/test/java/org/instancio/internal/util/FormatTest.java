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
package org.instancio.internal.util;

import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.testsupport.fixtures.Types;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.scope;

class FormatTest {

    @SuppressWarnings("all")
    private static class Nested1 {
        private static class Nested2 {
            private String nested;

            public void setNested(final String nested) {
                this.nested = nested;
            }
        }
    }

    @Test
    void formatField() {
        assertThat(Format.formatField(ReflectionUtils.getField(Person.class, "name")))
                .isEqualTo("String Person.name");

        assertThat(Format.formatField(ReflectionUtils.getField(Nested1.Nested2.class, "nested")))
                .isEqualTo("String FormatTest$Nested1$Nested2.nested");

        assertThat(Format.formatField(null)).isNull();
    }

    @Test
    void formatMethod() throws NoSuchMethodException {
        assertThat(Format.formatMethod(Person.class.getMethod("setName", String.class)))
                .isEqualTo("Person.setName(String)");

        assertThat(Format.formatMethod(Nested1.Nested2.class.getMethod("setNested", String.class)))
                .isEqualTo("FormatTest$Nested1$Nested2.setNested(String)");

        assertThat(Format.formatMethod(null)).isNull();
    }

    @Test
    void formatScopes() {
        assertThat(Format.formatScopes(Collections.emptyList())).isEmpty();

        assertThat(Format.formatScopes(Collections.singletonList(scope(Person.class))))
                .isEqualTo("scope(Person)");

        assertThat(Format.formatScopes(Arrays.asList(scope(Person.class), scope(Address.class, "city"))))
                .isEqualTo("scope(Person), scope(Address, \"city\")");
    }

    @Test
    void withoutPackage() {
        assertThat(Format.withoutPackage(Types.FOO_LIST_INTEGER.get())).isEqualTo("Foo<List<Integer>>");
        assertThat(Format.withoutPackage(Types.MAP_INTEGER_STRING.get())).isEqualTo("Map<Integer, String>");
        assertThat(Format.withoutPackage(Types.LIST_LIST_STRING.get())).isEqualTo("List<List<String>>");
        assertThat(Format.withoutPackage(Nested1.Nested2.class)).isEqualTo("FormatTest$Nested1$Nested2");
        assertThat(Format.withoutPackage(Foo.class)).isEqualTo("Foo");
        assertThat(Format.withoutPackage(String.class)).isEqualTo("String");
        assertThat(Format.withoutPackage(List.class.getTypeParameters()[0])).isEqualTo("E");
        assertThat(Format.withoutPackage(int[].class)).isEqualTo("int[]");
    }

    @Test
    void getTypeVariablesCsv() {
        assertThat(Format.getTypeVariablesCsv(Object.class)).isEmpty();
        assertThat(Format.getTypeVariablesCsv(List.class)).isEqualTo("E");
        assertThat(Format.getTypeVariablesCsv(Map.class)).isEqualTo("K, V");
    }
}
