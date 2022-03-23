package org.instancio.util;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public final class Verify {

    private Verify() {
    }

    public static <T> T notNull(final T object) {
        return Objects.requireNonNull(object);
    }

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

}
