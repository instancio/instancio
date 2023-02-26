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
package org.instancio.internal.instantiation;

import org.instancio.internal.util.ExceptionHandler;

import java.lang.reflect.Field;

@SuppressWarnings("all")
final class UnsafeHelper {

    // avoid import to prevent PMD/Checkstyle warnings
    private final sun.misc.Unsafe unsafe;

    private UnsafeHelper() {
        this.unsafe = getUnsafe();
    }

    static UnsafeHelper getInstance() {
        return UnsafeHelper.Holder.INSTANCE;
    }

    <T> T allocateInstance(final Class<T> klass) {
        if (unsafe == null) {
            return null;
        }
        try {
            return (T) unsafe.allocateInstance(klass);
        } catch (Exception ex) {
            ExceptionHandler.logException("Error instantiating {} using Unsafe", ex, klass);
            return null;
        }
    }

    private sun.misc.Unsafe getUnsafe() {
        try {
            final Field[] fields = sun.misc.Unsafe.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                final Object obj = field.get(null);
                if (obj instanceof sun.misc.Unsafe) {
                    return (sun.misc.Unsafe) obj;
                }
            }
        } catch (Throwable t) {
            ExceptionHandler.logException("Error getting Unsafe", t);
        }
        return null;
    }

    private static class Holder {
        private static final UnsafeHelper INSTANCE = new UnsafeHelper();
    }
}
