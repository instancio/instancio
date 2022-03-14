package org.instancio.testsupport.utils;

import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

public class TypeMapBuilder {

    private final Map<TypeVariable<?>, Class<?>> typeMap = new HashMap<>();
    private final Class<?> klass;

    private TypeMapBuilder(Class<?> klass) {
        this.klass = klass;
    }

    public static TypeMapBuilder forClass(Class<?> klass) {
        return new TypeMapBuilder(klass);
    }

    public TypeMapBuilder with(String typeVariable, Class<?> mappedType) {
        typeMap.put(getTypeVar(klass, typeVariable), mappedType);
        return this;
    }

    public Map<TypeVariable<?>, Class<?>> get() {
        return typeMap;
    }
}
