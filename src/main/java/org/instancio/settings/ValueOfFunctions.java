package org.instancio.settings;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

class ValueOfFunctions {
    private static final Map<Class<?>, Function<String, Object>> VALUEOF_FUNCTIONS = createMap();

    private static Map<Class<?>, Function<String, Object>> createMap() {
        final Map<Class<?>, Function<String, Object>> fnMap = new HashMap<>();
        fnMap.put(Boolean.class, Boolean::valueOf);
        fnMap.put(Byte.class, Byte::valueOf);
        fnMap.put(Short.class, Short::valueOf);
        fnMap.put(Integer.class, Integer::valueOf);
        fnMap.put(Long.class, Long::valueOf);
        fnMap.put(Float.class, Float::valueOf);
        fnMap.put(Double.class, Double::valueOf);
        return Collections.unmodifiableMap(fnMap);
    }

    static Function<String, Object> getFunction(final Class<?> type) {
        return VALUEOF_FUNCTIONS.get(type);
    }

    private ValueOfFunctions() {
        // non-instantiable
    }
}
