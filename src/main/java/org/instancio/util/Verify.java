package org.instancio.util;

import java.util.Objects;

public final class Verify {

    private Verify() {
    }

    public static <T> T notNull(final T object, final String message, final Object... values) {
        return Objects.requireNonNull(object, () -> String.format(message, values));
    }

    public static <T> T[] notEmpty(final T[] array, final String message, final Object... values) {
        isTrue(array != null && array.length > 0, message, values);
        return array;
    }

    public static void isTrue(final boolean condition, final String message, final Object... values) {
        if (!condition) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

}
