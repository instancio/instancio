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
package org.instancio.junit.internal;

import org.instancio.internal.util.Sonar;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public final class ReflectionUtils {

    private ReflectionUtils() {
        // non-instantiable
    }

    public static <T> T newInstance(final Class<T> klass) {
        return org.instancio.internal.util.ReflectionUtils.newInstance(klass);
    }

    public static List<Annotation> collectionAnnotations(final AnnotatedElement element) {
        final Annotation[] annotations = element.getDeclaredAnnotations();
        final List<Annotation> results = new ArrayList<>();
        for (Annotation annotation : annotations) {
            final Class<? extends Annotation> type = annotation.annotationType();

            if (!type.getPackage().getName().startsWith("java.")) {
                results.add(annotation);
                results.addAll(collectionAnnotations(type));
            }
        }
        return results;
    }

    static List<Field> getAnnotatedFields(final Class<?> klass, final Class<? extends Annotation> annotation) {
        return Arrays.stream(klass.getDeclaredFields())
                .filter(field -> field.getAnnotation(annotation) != null)
                .collect(toList());
    }

    @Nullable
    @SuppressWarnings("java:S3011")
    static Object getFieldValue(final Field field, @Nullable final Object target) {
        try {
            field.setAccessible(true);
            return field.get(target);
        } catch (Exception ex) {
            return null;
        }
    }

    @SuppressWarnings(Sonar.ACCESSIBILITY_UPDATE_SHOULD_BE_REMOVED)
    public static <T extends AccessibleObject> T setAccessible(final T object) {
        return org.instancio.internal.util.ReflectionUtils.setAccessible(object);
    }
}
