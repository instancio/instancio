package org.instancio.internal;

import java.util.StringJoiner;

public class GeneratorSettings {
    private final int dataStructureSize;
    private final boolean ignoreChildren;

    private GeneratorSettings(final Builder builder) {
        dataStructureSize = builder.dataStructureSize;
        ignoreChildren = builder.ignoreChildren;
    }

    public boolean ignoreChildren() {
        return ignoreChildren;
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

        public GeneratorSettings build() {
            return new GeneratorSettings(this);
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GeneratorSettings.class.getSimpleName() + "[", "]")
                .add("dataStructureSize=" + dataStructureSize)
                .add("ignoreChildren=" + ignoreChildren)
                .toString();
    }
}
