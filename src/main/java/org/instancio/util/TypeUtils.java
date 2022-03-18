package org.instancio.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeUtils {
    private TypeUtils() {
        // non-instantiable
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getRawType(final Type type) {
        if (type instanceof Class) {
            return (Class<T>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<T>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof GenericArrayType) {
            final Type genericComponentType = ((GenericArrayType) type).getGenericComponentType();
            return getRawType(genericComponentType);
        }
        throw new UnsupportedOperationException("Unhandled type: " + type.getClass().getSimpleName());
    }

}
