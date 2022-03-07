package org.instancio.util;

public class ObjectUtils {
    private ObjectUtils() {
        //non-instantiable
    }

    public static <T> T defaultIfNull(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }
}
