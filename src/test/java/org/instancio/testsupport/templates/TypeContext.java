package org.instancio.testsupport.templates;

import org.instancio.util.TypeUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Extracts type information from a class.
 */
public class TypeContext {
    private final Type genericType;
    private final Class<?> typeClass;

    public TypeContext(final Class<?> klass) {
        final ParameterizedType genericSuperclass = (ParameterizedType) klass.getGenericSuperclass();
        this.genericType = genericSuperclass.getActualTypeArguments()[0];
        this.typeClass = TypeUtils.getRawType(genericType);
    }

    public Type getGenericType() {
        return genericType;
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }
}
