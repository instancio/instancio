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

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public final class Verify {

    public static <T> T notNull(final T object, final String message, final Object... values) {
        return Objects.requireNonNull(object, () -> String.format(message, values));
    }

    public static <T> T[] notEmpty(final T[] array, final String message, final Object... values) {
        isTrue(array != null && array.length > 0, message, values);
        return array;
    }

    public static <T> Collection<T> notEmpty(final Collection<T> collection, final String message, final Object... values) {
        isTrue(collection != null && !collection.isEmpty(), message, values);
        return collection;
    }

    public static void closedRange(final long min, final long max) {
        if (min > max) {
            throw new IllegalArgumentException(String.format("Lower must be less than upper: %s, %s", min, max));
        }
    }

    public static void isTrue(final boolean condition, final String message, final Object... values) {
        if (!condition) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void isFalse(final boolean condition, final String message, final Object... values) {
        if (condition) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void state(final boolean condition, final String message, final Object... values) {
        if (!condition) {
            throw new IllegalStateException(String.format(message, values));
        }
    }

    public static void isNotArrayCollectionOrMap(final Class<?> klass) {
        if (klass.isArray()
                || Collection.class.isAssignableFrom(klass)
                || Map.class.isAssignableFrom(klass)) {

            throw new IllegalArgumentException("Unexpected: " + klass);
        }
    }

    private Verify() {
        // non-instantiable
    }
}
