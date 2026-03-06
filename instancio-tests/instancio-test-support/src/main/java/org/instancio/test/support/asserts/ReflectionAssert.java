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
package org.instancio.test.support.asserts;

import org.apache.commons.lang3.Strings;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.SoftAssertions;
import org.instancio.test.support.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
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

    /**
     * Verifies that given object, including nested objects,
     * collection elements, and so on, are fully-populated.
     *
     * <p><b>Note:</b> this method is quite slow!
     */
    public static ReflectionAssert assertThatObject(Object actual) {
        return new ReflectionAssert(actual);
    }

    /**
     * Verifies that all fields with the given type declared by object's class are null.
     * This method does not verify nested objects, collections, etc.
     *
     * @return this reflection assert instance
     */
    public ReflectionAssert hasAllFieldsOfTypeSetToNull(final Class<?> fieldType) {
        collectDeclaredFieldsOfType(fieldType).forEach(field -> {
            final Object fieldValue = getFieldValue(field, actual);
            assertThat(fieldValue)
                    .as(String.format("Expected '%s' to be null, but was: '%s'", format(field), fieldValue))
                    .isNull();
        });
        return this;
    }

    /**
     * Verifies that this object has all fields with the given type set to the specified value.
     * This method does not verify nested objects, collections, etc.
     *
     * @return this reflection assert instance
     */
    public ReflectionAssert hasAllFieldsOfTypeEqualTo(final Class<?> fieldType, final Object value) {
        collectDeclaredFieldsOfType(fieldType).forEach(field -> {
            final Object fieldValue = getFieldValue(field, actual);
            assertThat(fieldValue)
                    .as(String.format("Expected '%s' to be equal to '%s', but was: '%s'", format(field), value, fieldValue))
                    .isEqualTo(value);
        });
        return this;
    }

    /**
     * Verifies that this object does NOT have all fields with the given type set to the specified value.
     * This method does not verify nested objects, collections, etc.
     *
     * @return this reflection assert instance
     */
    public ReflectionAssert doesNotHaveAllFieldsOfTypeEqualTo(final Class<?> fieldType, final String value) {
        collectDeclaredFieldsOfType(fieldType).forEach(field -> {
            final Object fieldValue = getFieldValue(field, actual);
            assertThat(fieldValue)
                    .as(String.format("Expected '%s' to NOT be equal to '%s''", format(field), value))
                    .isNotEqualTo(value);
        });
        return this;
    }

    /**
     * Recursively verifies that all fields are not null and all arrays/maps/collections are
     * within {@link Constants#MIN_SIZE} and {@link Constants#MIN_SIZE} (as per default settings).
     *
     * @return this reflection assert instance
     */
    public ReflectionAssert isFullyPopulated() {
        isNotNull(); // fail fast if 'actual' is null

        if (Collection.class.isAssignableFrom(actual.getClass())) {
            assertCollection((Collection<?>) actual, null);
            softly.assertAll();
            return this;
        } else if (Map.class.isAssignableFrom(actual.getClass())) {
            assertMap((Map<?, ?>) actual, null);
            softly.assertAll();
            return this;
        } else if (actual.getClass().isArray()) {
            assertArray(actual, null);
            softly.assertAll();
            return this;
        }

        if (actual.getClass().getPackage() == null || actual.getClass().getPackage().getName().startsWith("java")) {
            return this;
        }

        LOG.trace("ASSERT OBJ {}", actual.getClass());

        final List<Field> fields = getDeclaredAndInheritedFields(actual.getClass());

        for (Field field : fields) {

            LOG.trace("Verifying: {}", field);

            final Object value = getFieldValue(field, actual);
            softly.assertThat(value)
                    .as("Field '%s' is null", format(field))
                    .isNotNull();

            // False positive warning: value could be null since above isNotNull() check is using soft assertion
            // noinspection ConstantConditions
            if (value != null) {
                if (Collection.class.isAssignableFrom(value.getClass())) {
                    assertCollection((Collection<?>) value, field);
                } else if (Map.class.isAssignableFrom(value.getClass())) {
                    assertMap((Map<?, ?>) value, field);
                } else if (value.getClass().isArray()) {
                    assertArray(value, field);
                } else {
                    assertThatObject(value).isFullyPopulated(); // recurse
                }
            }
        }

        softly.assertAll();
        return this;
    }

    private void assertMap(Map<?, ?> map, Field field) {
        softly.assertThat(map)
                .as("Unexpected map results; field: %s", format(field))
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            assertThatObject(entry.getKey()).isFullyPopulated();
            assertThatObject(entry.getValue()).isFullyPopulated();
        }
    }

    private void assertCollection(Collection<?> collection, Field field) {
        softly.assertThat(collection)
                .as("Unexpected collection results; field: %s", format(field))
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .allSatisfy(it -> assertThatObject(it).isFullyPopulated());
    }

    private void assertArray(Object array, Field field) {
        final int size = Array.getLength(array);

        softly.assertThat(size)
                .as("Unexpected array results; field: %s", format(field))
                .isBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);

        for (int i = 0; i < size; i++) {
            Object element = Array.get(array, i);
            assertThatObject(element).isFullyPopulated();
        }
    }

    private static String format(final Field field) {
        return field == null ? "n/a" : String.format("%s.%s", field.getDeclaringClass().getSimpleName(), field.getName());
    }

    private static Object getFieldValue(final Field field, final Object obj) {
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException ex) {
            throw new AssertionError("Could not verify field value: " + field, ex);
        }
    }

    private List<Field> collectDeclaredFieldsOfType(final Class<?> fieldType) {
        return Arrays.stream(actual.getClass().getDeclaredFields())
                .filter(f -> f.getType() == fieldType && !Modifier.isStatic(f.getModifiers()))
                .collect(toList());
    }

    private static List<Field> getDeclaredAndInheritedFields(final Class<?> klass) {
        Class<?> next = klass;
        final List<Field> collected = new ArrayList<>();
        while (shouldCollectFrom(next)) {
            for (Field field : next.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    collected.add(field);
                }
            }
            next = next.getSuperclass();
        }

        return collected;
    }

    private static boolean shouldCollectFrom(final Class<?> next) {
        return next != null
               && !next.isArray()
               && next.getPackage() != null
               && !Strings.CI.startsWithAny(next.getPackage().getName(), "java.", "javax.", "com.sun.", "sun.");
    }
}