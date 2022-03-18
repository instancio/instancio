package org.instancio.model;

import org.instancio.util.Verify;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.StringJoiner;

public class GenericType<T> {
    private final Class<T> rawType;
    private final Type type;

    private GenericType(final Class<T> rawType, @Nullable final Type type) {
        this.rawType = Verify.notNull(rawType, "rawType must not be null");
        this.type = type;
    }

    public static <T> GenericType<T> of(final Class<T> rawType, @Nullable final Type genericType) {
        return new GenericType<>(rawType, genericType);
    }

    public static <T> GenericType<T> of(final Class<T> rawType) {
        return new GenericType<>(rawType, null);
    }

    public Class<T> getRawType() {
        return rawType;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenericType<?> that = (GenericType<?>) o;
        return Objects.equals(rawType, that.rawType) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawType, type);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "(", ")")
                .add(rawType.getSimpleName())
                .add(type == null ? null : type.toString())
                .toString();
    }
}
