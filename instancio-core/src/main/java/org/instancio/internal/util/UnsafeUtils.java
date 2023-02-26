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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public final class UnsafeUtils {
    private static final Logger LOG = LoggerFactory.getLogger(UnsafeUtils.class);

    private static final Unsafe UNSAFE = getUnsafe();

    @SuppressWarnings("unchecked")
    public static <T> T allocateInstance(final Class<T> klass) {
        if (UNSAFE == null) {
            return null;
        }
        try {
            return (T) UNSAFE.allocateInstance(klass);
        } catch (Exception ex) {
            ExceptionHandler.logException("Error instantiating %s using Unsafe", ex, klass);
            return null;
        }
    }

    private static Unsafe getUnsafe() {
        try {
            final Field[] fields = Unsafe.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true); // NOSONAR
                final Object obj = field.get(null);
                if (obj instanceof Unsafe) {
                    return (Unsafe) obj;
                }
            }
        } catch (Throwable t) { // NOSONAR
            ExceptionHandler.logException("Error getting Unsafe", t);
        }
        return null;
    }

    private UnsafeUtils() {
        // non-instantiable
    }
}
