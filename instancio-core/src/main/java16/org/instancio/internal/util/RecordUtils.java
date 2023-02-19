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

import org.instancio.exception.InstancioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;

public final class RecordUtils {
    private static final Logger LOG = LoggerFactory.getLogger(RecordUtils.class);

    public static <T> T instantiate(final Class<T> recordClass, final Object... args) {
        Verify.isTrue(recordClass.isRecord(), "Class '%s' is not a record!", recordClass.getName());

        try {
            final Constructor<?> ctor = getCanonicalConstructor(recordClass);
            if (ctor == null) {
                return null;
            }
            ctor.setAccessible(true); // NOSONAR
            return (T) ctor.newInstance(args);
        } catch (Exception ex) {
            throw new InstancioException("Error creating a record: " + recordClass, ex);
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

    private static Constructor<?> getCanonicalConstructor(final Class<?> recordClass) {
        final Class<?>[] componentTypes = getComponentTypes(recordClass);
        try {
            return recordClass.getDeclaredConstructor(componentTypes);
        } catch (NoSuchMethodException ex) {
            LOG.debug("Unable to resolve canonical constructor for record class '{}'", recordClass.getName());
            return null;
        }
    }

    private RecordUtils() {
        // non-instantiable
    }
}
