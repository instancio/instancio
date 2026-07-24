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

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;

public final class RecordUtils {

    private static Class<?>[] getComponentTypes(final Class<?> recordClass) {
        final RecordComponent[] components = recordClass.getRecordComponents();
        final Class<?>[] args = new Class<?>[components.length];
        for (int i = 0; i < args.length; i++) {
            args[i] = components[i].getType();
        }
        return args;
    }

    public static <T> Constructor<T> getCanonicalConstructor(final Class<T> recordClass) {
        final Class<?>[] componentTypes = getComponentTypes(recordClass);

        try {
            final Constructor<T> constructor = recordClass.getDeclaredConstructor(componentTypes);
            return ReflectionUtils.setAccessible(constructor);
        } catch (Exception ex) {
            throw Fail.withInternalError(ex);
        }
    }

    private RecordUtils() {
        // non-instantiable
    }
}
