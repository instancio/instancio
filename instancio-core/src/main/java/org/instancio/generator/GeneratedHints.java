/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.generator;

import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

@Immutable
public class GeneratedHints {
    private static final GeneratedHints IGNORE_CHILDREN_HINT = GeneratedHints.builder().ignoreChildren(true).build();

    private final int dataStructureSize;
    private final boolean ignoreChildren;
    private final boolean nullableResult;
    private final boolean nullableElements;
    private final boolean nullableMapKeys;
    private final boolean nullableMapValues;
    private final List<Object> withElements;

    private GeneratedHints(final Builder builder) {
        dataStructureSize = builder.dataStructureSize;
        ignoreChildren = builder.ignoreChildren;
        nullableResult = builder.nullableResult;
        nullableElements = builder.nullableElements;
        nullableMapKeys = builder.nullableKeys;
        nullableMapValues = builder.nullableValues;
        withElements = builder.withElements == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(builder.withElements);
    }

    /**
     * Contains a single hint to ignore children.
     *
     * @return ignore children hint
     */
    public static GeneratedHints createIgnoreChildrenHint() {
        return IGNORE_CHILDREN_HINT;
    }

    public boolean ignoreChildren() {
        return ignoreChildren;
    }

    /**
     * @return a result indicating that the generated result had 'nullable' flag set to {@code true}.
     */
    public boolean nullableResult() {
        return nullableResult;
    }

    public boolean nullableElements() {
        return nullableElements;
    }

    public boolean nullableMapKeys() {
        return nullableMapKeys;
    }

    public boolean nullableMapValues() {
        return nullableMapValues;
    }

    public int getDataStructureSize() {
        return dataStructureSize;
    }

    public List<?> getWithElements() {
        return withElements;
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
        private List<Object> withElements;

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

        public Builder withElements(final List<Object> withElements) {
            this.withElements = withElements;
            return this;
        }

        public GeneratedHints build() {
            return new GeneratedHints(this);
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Hints[", "]")
                .add("dataStructureSize=" + dataStructureSize)
                .add("ignoreChildren=" + ignoreChildren)
                .add("nullableResult=" + nullableResult)
                .add("nullableElements=" + nullableElements)
                .add("nullableMapKeys=" + nullableMapKeys)
                .add("nullableMapValues=" + nullableMapValues)
                .add("withElements=" + withElements)
                .toString();
    }
}
