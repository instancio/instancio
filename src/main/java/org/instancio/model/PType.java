package org.instancio.model;

import java.lang.reflect.ParameterizedType;
import java.util.StringJoiner;

class PType {
    private final Class<?> rawType;
    private final ParameterizedType parameterizedType;

    PType(final Class<?> rawType, final ParameterizedType parameterizedType) {
        this.rawType = rawType;
        this.parameterizedType = parameterizedType;
    }

    Class<?> getRawType() {
        return rawType;
    }

    ParameterizedType getParameterizedType() {
        return parameterizedType;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PType.class.getSimpleName() + "[", "]")
                .add("rawType=" + rawType)
                .add("parameterizedType=" + parameterizedType)
                .toString();
    }
}
