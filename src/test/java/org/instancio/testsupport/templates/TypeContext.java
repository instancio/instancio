package org.instancio.testsupport.templates;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Holds type information of a class under test, e.g.
 *
 * <pre>
 * Given type 'Map<Integer, String>' returns
 *   typeClass = Map.class
 *   typeArguments = Integer, String
 * </pre>
 */
public class TypeContext {
    private final Class<?> typeClass;
    private final Class<?>[] typeArguments;

    public TypeContext(final Class<?> klass) {
        final ParameterizedType genericSuperclass = (ParameterizedType) klass.getGenericSuperclass();
        final Type typeToCreate = genericSuperclass.getActualTypeArguments()[0];

        this.typeClass = typeToCreate instanceof ParameterizedType
                ? (Class<?>) ((ParameterizedType) typeToCreate).getRawType()
                : (Class<?>) typeToCreate;

        final Type[] actualTypeArgs = typeToCreate instanceof ParameterizedType
                ? ((ParameterizedType) typeToCreate).getActualTypeArguments()
                : new Type[0];

        this.typeArguments = Arrays.stream(actualTypeArgs)
                .map(it -> (Class<?>) it)
                .toArray(Class<?>[]::new);
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public Class<?>[] getTypeArguments() {
        return typeArguments;
    }
}
