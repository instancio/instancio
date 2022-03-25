package org.instancio.internal;

import java.util.StringJoiner;

public class GeneratedHints {
    private final int dataStructureSize;
    private final boolean ignoreChildren;
    private final boolean nullableResult;

    private GeneratedHints(final Builder builder) {
        dataStructureSize = builder.dataStructureSize;
        ignoreChildren = builder.ignoreChildren;
        nullableResult = builder.nullableResult;
    }

    public boolean ignoreChildren() {
        return ignoreChildren;
    }

    public boolean nullableResult() {
        return nullableResult;
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
