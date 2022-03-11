package org.instancio.model;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.StringJoiner;

public class GenericType {
    private final Class<?> rawType;
    private final Type genericType;

    private GenericType(final Class<?> rawType, @Nullable final Type genericType) {
        this.rawType = rawType;
        this.genericType = genericType;
    }

    public static GenericType of(final Class<?> rawType, @Nullable final Type genericType) {
        return new GenericType(rawType, genericType);
    }

    public static GenericType of(final Class<?> rawType) {
        return new GenericType(rawType, null);
    }

    public Class<?> getRawType() {
        return rawType;
    }

    public Type getGenericType() {
        return genericType;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenericType that = (GenericType) o;
        return Objects.equals(rawType, that.rawType) && Objects.equals(genericType, that.genericType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawType, genericType);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GenericType.class.getSimpleName() + "[", "]")
                .add(rawType.getSimpleName())
                .add(genericType == null ? null : genericType.toString())
                .toString();
    }
}
