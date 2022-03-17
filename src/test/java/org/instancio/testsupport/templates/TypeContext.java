package org.instancio.testsupport.templates;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * TODO: refactor
 */
public class TypeContext {
    private final Type genericType;
    private final Class<?> typeClass;

    public TypeContext(final Class<?> klass) {
        final ParameterizedType genericSuperclass = (ParameterizedType) klass.getGenericSuperclass();
        this.genericType = genericSuperclass.getActualTypeArguments()[0];
        this.typeClass = genericType instanceof Class
                ? (Class<?>) genericType
                : (Class<?>) ((ParameterizedType) genericType).getRawType();
    }

    public Type getGenericType() {
        return genericType;
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }
}
