package org.instancio.testsupport;

import org.instancio.util.Verify;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeReference<T> {

    private final Type type;
    private volatile Constructor<?> constructor;

    protected TypeReference() {
        Type superclass = getClass().getGenericSuperclass();
        Verify.isTrue(superclass instanceof ParameterizedType, "Missing type parameter");
        this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
    }

    /**
     * Instantiates a new instance of {@code T} using the default, no-arg
     * constructor.
     */
    @SuppressWarnings("unchecked")
    public T newInstance()
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        if (constructor == null) {
            Class<?> rawType = type instanceof Class<?>
                    ? (Class<?>) type
                    : (Class<?>) ((ParameterizedType) type).getRawType();
            constructor = rawType.getConstructor();
        }
        return (T) constructor.newInstance();
    }

    /**
     * Gets the referenced type.
     */
    public Type getType() {
        return this.type;
    }

    public static <T> T create(TypeReference<T> ref) throws Exception {
        return ref.newInstance();
    }
}