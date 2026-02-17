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

import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public final class ObjectUtils {

    private static final Integer DEFAULT_INT = 0;
    private static final Long DEFAULT_LONG = 0L;
    private static final Boolean DEFAULT_BOOLEAN = Boolean.FALSE;
    private static final Double DEFAULT_DOUBLE = 0d;
    private static final Float DEFAULT_FLOAT = 0f;
    private static final Character DEFAULT_CHAR = '\u0000';
    private static final Byte DEFAULT_BYTE = (byte) 0;
    private static final Short DEFAULT_SHORT = (short) 0;

    @SuppressWarnings({"unchecked", Sonar.COGNITIVE_COMPLEXITY_OF_METHOD})
    public static <T> @Nullable T defaultValue(final Class<T> type) {
        if (type.isPrimitive()) {
            if (type == int.class) return (T) DEFAULT_INT;
            if (type == long.class) return (T) DEFAULT_LONG;
            if (type == boolean.class) return (T) DEFAULT_BOOLEAN;
            if (type == double.class) return (T) DEFAULT_DOUBLE;
            if (type == float.class) return (T) DEFAULT_FLOAT;
            if (type == char.class) return (T) DEFAULT_CHAR;
            if (type == byte.class) return (T) DEFAULT_BYTE;
            if (type == short.class) return (T) DEFAULT_SHORT;
        }
        return null;
    }

    public static <T> T defaultIfNull(@Nullable final T value, final T defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static <T> T defaultIfNull(@Nullable final T value, final Supplier<T> defaultValue) {
        return value == null ? defaultValue.get() : value;
    }

    private ObjectUtils() {
        //non-instantiable
    }
}
