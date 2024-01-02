/*
 * Copyright 2022-2024 the original author or authors.
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

import org.instancio.exception.InstancioException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public final class ReflectionUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ReflectionUtils.class);

    private ReflectionUtils() {
        // non-instantiable
    }

    public static Class<?> loadClass(final String fullyQualifiedName) {
        try {
            return Class.forName(fullyQualifiedName);
        } catch (Exception ex) {
            LOG.trace("Class not found: '{}'", fullyQualifiedName);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> E[] getEnumValues(final Class<E> enumClass) {
        try {
            Method m = enumClass.getDeclaredMethod("values");
            m.setAccessible(true); // NOSONAR needed when Enum.values() is an empty array
            return (E[]) m.invoke(null);
        } catch (Exception ex) {
            throw new InstancioException("Error getting enum values for: " + enumClass, ex);
        }
    }

    public static Type getSetMethodParameterType(final Method method) {
        final Parameter[] p = method.getParameters();

        Verify.isTrue(p.length == 1,
                "Expected exactly 1 parameter, but got: %s", p.length);

        return ObjectUtils.defaultIfNull(p[0].getParameterizedType(), p[0].getType());
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    public static Method getSetterMethod(final Class<?> klass, final String methodName, final Class<?> parameterType) {
        if (parameterType == null) {

            // getDeclaredMethods() order is not guaranteed.
            // Need to sort to ensure reproducibility for a given seed.
            final Optional<Method> first = Arrays.stream(klass.getDeclaredMethods())
                    .filter(m -> m.getParameterCount() == 1 && m.getName().equals(methodName))
                    .min(new SetterMethodComparator());

            if (first.isPresent()) {
                return first.get();
            }

        } else {
            try {
                return klass.getDeclaredMethod(methodName, parameterType);
            } catch (NoSuchMethodException ex) {
                // fail at the end
            }
        }

        final String method = parameterType == null
                ? methodName
                : String.format("%s(%s)", methodName, parameterType.getSimpleName());

        throw Fail.withUsageError("Could not find method method '%s' declared by %s", method, klass);
    }

    public static Field getField(final Class<?> klass, final String fieldName) {
        try {
            return klass.getDeclaredField(Verify.notNull(fieldName, "null field name"));
        } catch (NoSuchFieldException ex) {
            throw Fail.withUsageError("invalid field '" + fieldName + "' for " + klass, ex);
        } catch (SecurityException ex) {
            throw new InstancioException("Unable to access '" + fieldName + "' of " + klass, ex);
        }
    }

    @Nullable
    public static Field getFieldOrNull(final Class<?> klass, final String fieldName) {
        try {
            return klass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException ex) {
            return null;
        } catch (SecurityException ex) {
            throw new InstancioException("Unable to access '" + fieldName + "' of " + klass, ex);
        }
    }

    @SuppressWarnings(Sonar.ACCESSIBILITY_UPDATE_SHOULD_BE_REMOVED)
    public static Object getFieldValue(final Field field, final Object target) {
        try {
            field.setAccessible(true);
            return field.get(target);
        } catch (Exception ex) {
            throw new InstancioException("Unable to get value from: " + field, ex);
        }
    }

    public static boolean hasNonNullValue(final Field field, final Object object) {
        return getFieldValue(field, object) != null;
    }

    @SuppressWarnings(Sonar.ACCESSIBILITY_UPDATE_SHOULD_BE_REMOVED)
    public static boolean hasNonNullOrNonDefaultPrimitiveValue(final Field field, final Object object) {
        final Object fieldValue = getFieldValue(field, object);
        return neitherNullNorPrimitiveWithDefaultValue(field.getType(), fieldValue);
    }

    public static boolean isArrayOrConcrete(final Class<?> klass) {
        return klass.isArray() || (!klass.isInterface() && !Modifier.isAbstract(klass.getModifiers()));
    }

    public static boolean neitherNullNorPrimitiveWithDefaultValue(final Class<?> type, @Nullable final Object value) {
        return !Objects.equals(ObjectUtils.defaultValue(type), value);
    }

    public static Class<?> getClass(final String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException ex) {
            throw Fail.withUsageError("class not found: '%s'", name, ex);
        } catch (Exception ex) {
            throw Fail.withUsageError("failed loading class: '%s'", name, ex);
        }
    }
}

