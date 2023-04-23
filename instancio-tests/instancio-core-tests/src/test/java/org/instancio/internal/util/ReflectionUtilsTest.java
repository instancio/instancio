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

import org.instancio.Instancio;
import org.instancio.exception.InstancioApiException;
import org.instancio.exception.InstancioException;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.pojo.basic.PrimitiveFields;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.pojo.person.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.util.AbstractList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReflectionUtilsTest {

    @Test
    void loadClass() {
        assertThat(ReflectionUtils.loadClass("foo")).isNull();
        assertThat(ReflectionUtils.loadClass("org.instancio.Instancio"))
                .isEqualTo(Instancio.class);
    }

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
                .hasMessageContaining("invalid field 'foo' for class org.instancio.test.support.pojo.person.Person");
    }

    @Test
    void getFieldValue() {
        final Person person = Person.builder().name("foo").build();

        assertThat(
                ReflectionUtils.getFieldValue(
                        ReflectionUtils.getField(Person.class, "name"),
                        person))
                .isEqualTo("foo");

        assertThat(
                ReflectionUtils.getFieldValue(
                        ReflectionUtils.getField(StaticFieldWithValue.class, "staticField"),
                        null))
                .isEqualTo("expected");

        final Field fieldNotDeclaredByPerson = ReflectionUtils.getField(IntegerHolder.class, "primitive");
        assertThatThrownBy(() -> ReflectionUtils.getFieldValue(fieldNotDeclaredByPerson, person))
                .isExactlyInstanceOf(InstancioException.class)
                .hasMessage("Unable to get value from: private int org.instancio.test.support.pojo.basic.IntegerHolder.primitive");
    }

    @Test
    void hasNonNullValue() {
        final Field nameField = ReflectionUtils.getField(Person.class, "name");
        assertThat(ReflectionUtils.hasNonNullValue(nameField, new Person())).isFalse();
        assertThat(ReflectionUtils.hasNonNullValue(nameField, Person.builder().name("foo").build())).isTrue();
    }

    @Test
    void hasNonNullOrNonDefaultPrimitiveValue() {
        final Field nameField = ReflectionUtils.getField(Person.class, "name");
        assertThat(ReflectionUtils.hasNonNullOrNonDefaultPrimitiveValue(nameField, new Person())).isFalse();
        assertThat(ReflectionUtils.hasNonNullOrNonDefaultPrimitiveValue(nameField, Person.builder().name("foo").build())).isTrue();
    }

    @ValueSource(strings = {"byteValue", "shortValue", "intValue", "longValue",
            "floatValue", "doubleValue", "booleanValue", "charValue",})
    @ParameterizedTest
    void hasNonNullOrNonDefaultPrimitiveValue_WithDefaultValues(final String fieldName) {
        final PrimitiveFields blank = PrimitiveFields.builder().build();
        final PrimitiveFields initialised = PrimitiveFields.builder()
                .byteValue((byte) 1)
                .shortValue((short) 1)
                .intValue(1)
                .longValue(1)
                .floatValue(0.000000001f)
                .doubleValue(0.000000000001)
                .booleanValue(true)
                .charValue('0')
                .build();


        assertThat(ReflectionUtils.hasNonNullOrNonDefaultPrimitiveValue(
                ReflectionUtils.getField(PrimitiveFields.class, fieldName), blank)).isFalse();

        assertThat(ReflectionUtils.hasNonNullOrNonDefaultPrimitiveValue(
                ReflectionUtils.getField(PrimitiveFields.class, fieldName), initialised)).isTrue();
    }

    @SuppressWarnings("all")
    private static class StaticFieldWithValue {
        private static String staticField = "expected";
    }
}
