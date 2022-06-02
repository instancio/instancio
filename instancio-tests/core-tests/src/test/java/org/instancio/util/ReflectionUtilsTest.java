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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.AbstractList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReflectionUtilsTest {

    @ValueSource(classes = {String.class, String[].class, List[].class})
    @ParameterizedTest
    void isArrayOrConcreteTrue(final Class<?> klass) {
        assertThat(ReflectionUtils.isArrayOrConcrete(klass)).isTrue();
    }

    @ValueSource(classes = {List.class, AbstractList.class})
    @ParameterizedTest
    void isArrayOrConcreteFalse(final Class<?> klass) {
        assertThat(ReflectionUtils.isArrayOrConcrete(klass)).isFalse();
    }

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
        final List<Field> fields = ReflectionUtils.getAnnotatedFields(WithAnnotatedField.class, AnnotationX.class);

        assertThat(fields).hasSize(2)
                .extracting(Field::getName)
                .contains("foo", "bar");
    }

    @Test
    void getAnnotatedFieldsReturnsEmptyList() {
        assertThat(ReflectionUtils.getAnnotatedFields(StringHolder.class, AnnotationX.class)).isEmpty();
    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnotationX {}

    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnotationY {}

    @SuppressWarnings({"unused", "NotNullFieldNotInitialized", "NullableProblems"})
    private static class WithAnnotatedField {
        @AnnotationX
        private String foo;
        @AnnotationX
        private Integer bar;
        @AnnotationY
        private Integer baz;
    }
}
