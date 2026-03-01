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

import org.instancio.documentation.Contract;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@SuppressWarnings(Sonar.CALL_LEADS_TO_ILLEGAL_ARGUMENT_EXCEPTION)
public final class Verify {

    @Contract("null -> fail; _ -> param1")
    public static <T> T notNull(@Nullable final T object, final String message, final Object... values) {
        return requireNonNull(object, () -> String.format(message, values));
    }

    @Contract("null, _, _ -> fail; !null, _, _ -> param1")
    public static <T extends @Nullable Object> T[] notEmpty(
            final T @Nullable [] array,
            final String message,
            final Object... values) {

        isTrue(array != null && array.length > 0, message, values);
        return array;
    }

    @Contract("null, _, _ -> fail; !null, _, _ -> param1")
    public static <T extends @Nullable Object> Collection<T> notEmpty(
            @Nullable final Collection<T> collection,
            final String message,
            final Object... values) {

        isTrue(collection != null && !collection.isEmpty(), message, values);
        return collection;
    }

    public static void closedRange(final long min, final long max) {
        if (min > max) {
            throw new IllegalArgumentException(String.format("Lower must be less than upper: %s, %s", min, max));
        }
    }

    @Contract("false, _, _ -> fail")
    public static void isTrue(final boolean condition, final String message, final Object... values) {
        if (!condition) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    @Contract("true, _, _ -> fail")
    public static void isFalse(final boolean condition, final String message, final Object... values) {
        if (condition) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    @Contract("false, _, _ -> fail")
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
