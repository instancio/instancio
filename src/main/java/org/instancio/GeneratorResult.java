package org.instancio;

import java.util.StringJoiner;

class GeneratorResult<T> {
    private final T value;
    private final boolean ignoreChildren;

    public GeneratorResult(T value, boolean ignoreChildren) {
        this.value = value;
        this.ignoreChildren = ignoreChildren;
    }

    public GeneratorResult(T value) {
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
