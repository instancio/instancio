/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.util;

import org.instancio.exception.InstancioApiException;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.pojo.person.Person;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReflectionUtilsTest {

    @Test
    void getEnumValues() {
        final Gender[] enumValues = ReflectionUtils.getEnumValues(Gender.class);
        assertThat(enumValues).containsExactly(Gender.values());
    }

    @Test
    void getField() {
        assertThat(ReflectionUtils.getField(Person.class, "name"))
                .isNotNull()
                .extracting(Field::getName)
                .isEqualTo("name");

        assertThatThrownBy(() -> ReflectionUtils.getField(Person.class, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("null field name");

        assertThatThrownBy(() -> ReflectionUtils.getField(Person.class, "foo"))
                .isInstanceOf(InstancioApiException.class)
                .hasMessage("Invalid field 'foo' for class org.instancio.test.support.pojo.person.Person");
    }

    @Test
    void getAnnotatedFields() {
        final List<Field> fields = ReflectionUtils.getAnnotatedFields(WithAnnotatedField.class, Nullable.class);

        assertThat(fields).hasSize(2)
                .extracting(Field::getName)
                .contains("foo", "bar");
    }

    @Test
    void getAnnotatedFieldsReturnsEmptyList() {
        assertThat(ReflectionUtils.getAnnotatedFields(StringHolder.class, Nullable.class)).isEmpty();
    }

    @SuppressWarnings({"unused", "NotNullFieldNotInitialized", "NullableProblems"})
    private static class WithAnnotatedField {
        @Nullable
        private String foo;
        @Nullable
        private Integer bar;
        @Nonnull
        private Integer baz;
    }
}
