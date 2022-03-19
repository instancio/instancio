package org.instancio;

import javax.annotation.Nullable;
import java.util.StringJoiner;

class GeneratorResult<T> {
    private final T value;
    private final boolean ignoreChildren;

    public GeneratorResult(@Nullable final T value, final boolean ignoreChildren) {
        this.value = value;
        this.ignoreChildren = ignoreChildren;
    }

    public GeneratorResult(@Nullable final T value) {
        this(value, false);
    }

    public T getValue() {
        return value;
    }

    public boolean ignoreChildren() {
        return ignoreChildren;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GeneratorResult.class.getSimpleName() + "[", "]")
                .add("value=" + value)
                .add("ignoreChildren=" + ignoreChildren)
                .toString();
    }
}
