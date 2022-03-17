package org.instancio.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeUtils {
    private TypeUtils() {
        // non-instantiable
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getRawType(Type type) {
        return type instanceof Class
                ? (Class<T>) type
                : (Class<T>) ((ParameterizedType) type).getRawType();

    }
}
