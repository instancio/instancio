package org.instancio.internal;

import javax.annotation.Nullable;
import java.util.StringJoiner;

class GeneratorResult<T> {
    private static final GeneratorResult<Void> NULL_RESULT = new GeneratorResult<>(null, true,
            GeneratedHints.builder().ignoreChildren(true).build());

    // FIXME ignore children is in GeneratorResult and GeneratedHints
    //  this needs cleanup
    private final T value;
    private final boolean ignoreChildren;
    private final GeneratedHints generatedHints;

    private GeneratorResult(@Nullable final T value,
                            final boolean ignoreChildren,
                            @Nullable final GeneratedHints generatedHints) {
        this.value = value;
        this.ignoreChildren = ignoreChildren;
        this.generatedHints = generatedHints;
    }

    private GeneratorResult(final Builder<T> builder) {
        value = builder.value;
        ignoreChildren = builder.ignoreChildren;
        generatedHints = builder.generatedHints;
    }

    /**
     * This result indicates that a null was generated, therefore the target's value should
     * be set to null. If the target is a field with a pre-initialised default value, it will be
     * overwritten with a null value.
     * <p>
     * An actual {@code null} {@code GeneratorResult} means that the target field will be ignored
     * (it would retain its default value, if any).
     *
     * @return null result
     */
    static GeneratorResult<Void> nullResult() {
        return NULL_RESULT;
    }

    static <T> Builder<T> builder(T value) {
        return new Builder<>(value);
    }

    static <T> GeneratorResult<T> build(T value) {
        return new Builder<>(value).build();
    }

    T getValue() {
        return value;
    }

    boolean ignoreChildren() {
        return ignoreChildren;
    }

    GeneratedHints getHints() {
        return generatedHints;
    }

    static final class Builder<T> {
        private final T value;
        private boolean ignoreChildren;
        private GeneratedHints generatedHints;

        private Builder(@Nullable final T value) {
            this.value = value;
        }

        public Builder<T> withHints(@Nullable final GeneratedHints generatedHints) {
            this.generatedHints = generatedHints;
            if (generatedHints != null) {
                this.ignoreChildren = generatedHints.ignoreChildren();
            }
            return this;
        }

        public GeneratorResult<T> build() {
            return new GeneratorResult<>(this);
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GeneratorResult.class.getSimpleName() + "[", "]")
                .add("value=" + value)
                .add("ignoreChildren=" + ignoreChildren)
                .add("hints=" + generatedHints)
                .toString();
    }
}
