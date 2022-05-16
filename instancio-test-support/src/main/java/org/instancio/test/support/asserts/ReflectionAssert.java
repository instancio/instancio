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
package org.instancio.test.support.asserts;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Fail;
import org.assertj.core.api.SoftAssertions;
import org.instancio.test.support.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnusedReturnValue")
public class ReflectionAssert extends AbstractAssert<ReflectionAssert, Object> {
    private static final Logger LOG = LoggerFactory.getLogger(ReflectionAssert.class);

    private final SoftAssertions softly = new SoftAssertions();

    private ReflectionAssert(Object actual) {
        super(actual, ReflectionAssert.class);
    }

    public static ReflectionAssert assertThatObject(Object actual) {
        return new ReflectionAssert(actual);
    }

    /**
     * Verifies that all fields with the given type declared by object's class are null.
     *
     * @return this reflection assert instance
     */
    public ReflectionAssert hasAllFieldsOfTypeSetToNull(final Class<?> fieldType) {
        collectDeclaredFieldsOfType(fieldType).forEach(field -> {
            final Object fieldValue = getFieldValue(field);
            assertThat(fieldValue)
                    .as(String.format("Expected '%s' to be null, but was: '%s'", format(field), fieldValue))
                    .isNull();
        });
        return this;
    }

    public ReflectionAssert hasAllFieldsOfTypeEqualTo(final Class<?> fieldType, final String value) {
        collectDeclaredFieldsOfType(fieldType).forEach(field -> {
            final Object fieldValue = getFieldValue(field);
            assertThat(fieldValue)
                    .as(String.format("Expected '%s' to be equal to '%s', but was: '%s'", format(field), value, fieldValue))
                    .isEqualTo(value);
        });
        return this;
    }

    public ReflectionAssert hasAllFieldsOfTypeNotEqualTo(final Class<?> fieldType, final String value) {
        collectDeclaredFieldsOfType(fieldType).forEach(field -> {
            final Object fieldValue = getFieldValue(field);
            assertThat(fieldValue)
                    .as(String.format("Expected '%s' to NOT be equal to '%s', but was: '%s'", format(field), value, fieldValue))
                    .isNotEqualTo(value);
        });
        return this;
    }

    /**
     * Recursively verifies that all fields are not null and all arrays/maps/collections are non-empty.
     *
     * @return this reflection assert instance
     */
    public ReflectionAssert isFullyPopulated() {
        as("Object contains a null value: %s", actual).isNotNull();

        if (Collection.class.isAssignableFrom(actual.getClass())) {
            assertCollection((Collection<?>) actual, null);
            return this;
        } else if (Map.class.isAssignableFrom(actual.getClass())) {
            assertMap((Map<?, ?>) actual, null);
            return this;
        } else if (actual.getClass().isArray()) {
            assertArray(actual, null);
            return this;
        }

        if (actual.getClass().getPackage() == null || actual.getClass().getPackage().getName().startsWith("java")) {
            return this;
        }

        softly.assertThat(actual).hasNoNullFieldsOrProperties();

        LOG.trace("ASSERT OBJ " + actual.getClass());

        final List<Method> methods = Arrays.stream(actual.getClass().getDeclaredMethods())
                .filter(it -> it.getName().startsWith("get") && it.getParameterCount() == 0)
                .filter(it -> Modifier.isPublic(it.getModifiers()) && !Modifier.isStatic(it.getModifiers()))
                .collect(toList());

        for (Method method : methods) {

            try {
                LOG.trace("calling method: " + method);

                Object result = method.invoke(actual);
                softly.assertThat(result)
                        .as("Method '%s' returned a null", format(method))
                        .isNotNull();

                // False positive: 'result != null' is not always true due to soft assertions
                // noinspection ConstantConditions
                if (result != null) {
                    if (Collection.class.isAssignableFrom(result.getClass())) {
                        assertCollection((Collection<?>) result, method);
                    } else if (Map.class.isAssignableFrom(result.getClass())) {
                        assertMap((Map<?, ?>) result, method);
                    } else if (result.getClass().isArray()) {
                        assertArray(result, method);
                    } else {
                        assertThatObject(result).isFullyPopulated(); // recurse
                    }
                }

            } catch (IllegalAccessException | InvocationTargetException ex) {
                Fail.fail("Invocation of method '%s' failed with: '%s'", method.getName(), ex.getMessage());
            }
        }

        softly.assertAll();
        return this;
    }

    private void assertMap(Map<?, ?> map, Method method) {
        softly.assertThat(map)
                .as("Method '%s' returned unexpected result", format(method))
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            assertThatObject(entry.getKey()).isFullyPopulated();
            assertThatObject(entry.getValue()).isFullyPopulated();
        }
    }

    private void assertCollection(Collection<?> collection, Method method) {
        softly.assertThat(collection)
                .as("Method '%s' returned unexpected result", format(method))
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .allSatisfy(it -> assertThatObject(it).isFullyPopulated());
    }

    private void assertArray(Object array, Method method) {
        final int size = Array.getLength(array);

        softly.assertThat(size)
                .as("Method '%s' returned unexpected result", format(method))
                .isBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);

        for (int i = 0; i < size; i++) {
            Object element = Array.get(array, i);
            assertThatObject(element).isFullyPopulated();
        }
    }

    private Object getFieldValue(final Field field) {
        try {
            field.setAccessible(true);
            return field.get(actual);
        } catch (IllegalAccessException ex) {
            throw new AssertionError("Could not verify field value: " + field, ex);
        }
    }

    private List<Field> collectDeclaredFieldsOfType(final Class<?> fieldType) {
        return Arrays.stream(actual.getClass().getDeclaredFields())
                .filter(f -> f.getType() == fieldType
                        && !Modifier.isStatic(f.getModifiers())
                        && !Modifier.isFinal(f.getModifiers()))
                .collect(toList());
    }

    private static String format(final Field field) {
        return String.format("%s.%s", field.getDeclaringClass().getSimpleName(), field.getName());
    }

    private static String format(final Method method) {
        return method == null
                ? "n/a"
                : String.format("%s.%s", method.getDeclaringClass().getSimpleName(), method.getName());
    }
}