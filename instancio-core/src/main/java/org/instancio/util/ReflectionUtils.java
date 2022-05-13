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
import org.instancio.exception.InstancioException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public final class ReflectionUtils {

    private ReflectionUtils() {
        // non-instantiable
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> E[] getEnumValues(final Class<E> enumClass) {
        try {
            Method m = enumClass.getDeclaredMethod("values");
            return (E[]) m.invoke(null);
        } catch (Exception ex) {
            throw new InstancioException("Error getting enum values for class: " + enumClass.getName(), ex);
        }
    }

    @SuppressWarnings(Sonar.ACCESSIBILITY_UPDATE_SHOULD_BE_REMOVED)
    public static void setField(final Object target, final Field field, final Object value) {
        if (target == null) {
            return;
        }
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalArgumentException ex) {
            throw new InstancioApiException(String.format("Could not set value to the field: %s.%nCaused by: %s",
                    field, ex.getMessage()), ex);
        } catch (Exception ex) {
            throw new InstancioException(String.format("Could not set value to the field: %s.%nCaused by: %s",
                    field, ex.getMessage()), ex);
        }
    }

    public static boolean isValidField(final Class<?> klass, final String fieldName) {
        try {
            klass.getDeclaredField(fieldName);
            return true;
        } catch (NoSuchFieldException ex) {
            return false;
        }
    }

    public static Field getField(final Class<?> klass, final String fieldName) {
        try {
            return klass.getDeclaredField(Verify.notNull(fieldName, "null field name"));
        } catch (NoSuchFieldException ex) {
            throw new InstancioApiException("Invalid field '" + fieldName + "' for " + klass, ex);
        } catch (SecurityException ex) {
            throw new InstancioApiException("Unable to access '" + fieldName + "' of " + klass, ex);
        }
    }

    public static boolean isConcrete(final Class<?> klass) {
        return !klass.isInterface() && !Modifier.isAbstract(klass.getModifiers());
    }

    public static Class<?> getClass(final String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException ex) {
            throw new InstancioApiException(String.format("Class not found: '%s'", name), ex);
        } catch (Exception ex) {
            throw new InstancioApiException(String.format("Unable to get class: '%s'", name), ex);
        }
    }

    public static List<Field> getAnnotatedFields(final Class<?> klass, final Class<? extends Annotation> annotation) {
        return Arrays.stream(klass.getDeclaredFields())
                .filter(field -> field.getAnnotation(annotation) != null)
                .collect(toList());
    }
}

