package org.instancio.internal;

import java.util.StringJoiner;

public class GeneratedHints {
    private final int dataStructureSize;
    private final boolean ignoreChildren;
    private final boolean nullableResult;
    private final boolean nullableElements;
    private final boolean nullableKeys;
    private final boolean nullableValues;

    private GeneratedHints(final Builder builder) {
        dataStructureSize = builder.dataStructureSize;
        ignoreChildren = builder.ignoreChildren;
        nullableResult = builder.nullableResult;
        nullableElements = builder.nullableElements;
        nullableKeys = builder.nullableKeys;
        nullableValues = builder.nullableValues;
    }

    public boolean ignoreChildren() {
        return ignoreChildren;
    }

    /**
     * Indicates that the generated result had nullable setting enabled.
     */
    public boolean nullableResult() {
        return nullableResult;
    }

    public boolean nullableElements() {
        return nullableElements;
    }

    public boolean nullableKeys() {
        return nullableKeys;
    }

    public boolean nullableValues() {
        return nullableValues;
    }

    public int getDataStructureSize() {
        return dataStructureSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int dataStructureSize;
        private boolean ignoreChildren;
        private boolean nullableResult;
        private boolean nullableElements;
        private boolean nullableKeys;
        private boolean nullableValues;

        private Builder() {
        }

        public Builder dataStructureSize(final int size) {
            this.dataStructureSize = size;
            return this;
        }

        public Builder ignoreChildren(final boolean ignoreChildren) {
            this.ignoreChildren = ignoreChildren;
            return this;
        }

        public Builder nullableResult(final boolean nullableResult) {
            this.nullableResult = nullableResult;
            return this;
        }

        public Builder nullableElements(final boolean nullableElements) {
            this.nullableElements = nullableElements;
            return this;
        }

        public Builder nullableKeys(final boolean nullableKeys) {
            this.nullableKeys = nullableKeys;
            return this;
        }

        public Builder nullableValues(final boolean nullableValues) {
            this.nullableValues = nullableValues;
            return this;
        }

        public GeneratedHints build() {
            return new GeneratedHints(this);
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GeneratedHints.class.getSimpleName() + "[", "]")
                .add("dataStructureSize=" + dataStructureSize)
                .add("ignoreChildren=" + ignoreChildren)
                .add("nullableResult=" + nullableResult)
                .toString();
    }
}
