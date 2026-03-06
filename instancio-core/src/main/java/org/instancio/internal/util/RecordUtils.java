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
package org.instancio.internal.util;

import org.instancio.exception.InstancioException;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;

public final class RecordUtils {

    public static <T> T instantiate(final Class<T> recordClass, @Nullable final Object... args) {
        try {
            Verify.isTrue(recordClass.isRecord(), "Class '%s' is not a record!", recordClass.getName());
            final Constructor<T> ctor = getCanonicalConstructor(recordClass);
            ReflectionUtils.setAccessible(ctor);
            return ctor.newInstance(args);
        } catch (Exception ex) {
            throw new InstancioException("Error instantiating a record: " + recordClass, ex);
        }
    }

    public static Class<?>[] getComponentTypes(final Class<?> recordClass) {
        final RecordComponent[] components = recordClass.getRecordComponents();
        final Class<?>[] args = new Class<?>[components.length];
        for (int i = 0; i < args.length; i++) {
            args[i] = components[i].getType();
        }
        return args;
    }

    private static <T> Constructor<T> getCanonicalConstructor(final Class<T> recordClass) throws NoSuchMethodException {
        final Class<?>[] componentTypes = getComponentTypes(recordClass);
        return recordClass.getDeclaredConstructor(componentTypes);
    }

    private RecordUtils() {
        // non-instantiable
    }
}
