package org.instancio.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PrimitiveWrapperBiLookup {
    private static final Map<Class<?>, Class<?>> CORE_TYPES = Collections.unmodifiableMap(initCoreTypesMap());

    private static Map<Class<?>, Class<?>> initCoreTypesMap() {
        final Map<Class<?>, Class<?>> map = new HashMap<>();
        map.put(boolean.class, Boolean.class);
        map.put(char.class, Character.class);
        map.put(byte.class, Byte.class);
        map.put(short.class, Short.class);
        map.put(int.class, Integer.class);
        map.put(long.class, Long.class);
        map.put(float.class, Float.class);
        map.put(double.class, Double.class);
        // lookup both ways
        map.put(Boolean.class, boolean.class);
        map.put(Character.class, char.class);
        map.put(Byte.class, byte.class);
        map.put(Short.class, short.class);
        map.put(Integer.class, int.class);
        map.put(Long.class, long.class);
        map.put(Float.class, float.class);
        map.put(Double.class, double.class);
        return map;
    }

    public static Optional<Class<?>> getEquivalent(final Class<?> primitiveOrWrapper) {
        return Optional.ofNullable(CORE_TYPES.get(primitiveOrWrapper));
    }

    private PrimitiveWrapperBiLookup() {
        // non-instantiable
    }
}
