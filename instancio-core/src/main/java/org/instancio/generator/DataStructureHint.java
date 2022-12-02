/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.generator;

import org.instancio.internal.ApiValidator;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * Hints related to generating arrays, collections, and maps.
 *
 * @since 1.7.0
 */
public final class DataStructureHint implements Hint<DataStructureHint> {
    private final int dataStructureSize;
    private final boolean nullableElements;
    private final boolean nullableMapKeys;
    private final boolean nullableMapValues;
    private final List<?> withElements;

    private DataStructureHint(final Builder builder) {
        dataStructureSize = builder.dataStructureSize;
        nullableElements = builder.nullableElements;
        nullableMapKeys = builder.nullableMapKeys;
        nullableMapValues = builder.nullableMapValues;
        withElements = builder.withElements == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(builder.withElements);
    }

    /**
     * Returns the data structure size hint indicating the number of elements
     * to be inserted into an array, collection, or map.
     *
     * <p>This hint is ignored unless the generated object
     * is an array, collection, or map.</p>
     *
     * @return number of elements to generate
     * @since 1.7.0
     */
    public int dataStructureSize() {
        return dataStructureSize;
    }

    /**
     * The nullable elements hint is applicable only to arrays and collections.
     * Indicates whether elements can be {@code null}.
     *
     * <p>This hint is ignored unless the generated object
     * is an array or collection.</p>
     *
     * @return {@code true} if elements are nullable, {@code false} otherwise
     * @since 1.7.0
     */
    public boolean nullableElements() {
        return nullableElements;
    }

    /**
     * The nullable map keys hint is applicable only to maps.
     * Indicates whether map keys can be {@code null}.
     *
     * <p>This hint is ignored unless the generated object is a map.</p>
     *
     * @return {@code true} if keys are nullable, {@code false} otherwise
     * @since 1.7.0
     */
    public boolean nullableMapKeys() {
        return nullableMapKeys;
    }

    /**
     * The nullable map values hint is applicable only to maps.
     * Indicates whether map values can be {@code null}.
     *
     * <p>This hint is ignored unless the generated object is a map.</p>
     *
     * @return {@code true} if values are nullable, {@code false} otherwise
     * @since 1.7.0
     */
    public boolean nullableMapValues() {
        return nullableMapValues;
    }

    /**
     * Returns elements provided by the generator to the engine that are
     * to be inserted into a collection or array.
     *
     * <p>This hint is ignored unless the generated object
     * is an array or collection.</p>
     *
     * @param <T> element type
     * @return specific elements to be inserted into a collection or array
     * @since 1.7.0
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> withElements() {
        return (List<T>) withElements;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "DataStructureHint[", "]")
                .add("dataStructureSize=" + dataStructureSize)
                .add("nullableElements=" + nullableElements)
                .add("nullableMapKeys=" + nullableMapKeys)
                .add("nullableMapValues=" + nullableMapValues)
                .add("withElements=" + withElements)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int dataStructureSize;
        private boolean nullableElements;
        private boolean nullableMapKeys;
        private boolean nullableMapValues;
        private List<?> withElements;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        /**
         * A hint used only for generating collections and arrays.
         * Indicates how many elements the engine should generate
         * and insert into the data structure.
         *
         * @param dataStructureSize size of the data structure
         * @return builder instance
         * @since 1.7.0
         */
        public Builder dataStructureSize(final int dataStructureSize) {
            ApiValidator.isTrue(dataStructureSize >= 0,
                    "Data structure size must not be negative");
            this.dataStructureSize = dataStructureSize;
            return this;
        }

        /**
         * A hint used only for generating collections and arrays.
         * Indicates whether {@code null} values are allowed to be inserted.
         *
         * @param nullableElements {@code null} elements allowed if {@code true}
         * @return builder instance
         * @since 1.7.0
         */
        public Builder nullableElements(final boolean nullableElements) {
            this.nullableElements = nullableElements;
            return this;
        }

        /**
         * A hint used only for generating maps.
         * Indicates whether {@code null}s are allowed to be inserted
         * as map keys.
         *
         * @param nullableMapKeys {@code null} keys allowed if {@code true}
         * @return builder instance
         * @since 1.7.0
         */
        public Builder nullableMapKeys(final boolean nullableMapKeys) {
            this.nullableMapKeys = nullableMapKeys;
            return this;
        }

        /**
         * A hint used only for generating maps. Indicates whether
         * {@code null}s are allowed to be inserted as map values.
         *
         * @param nullableMapValues {@code null} values allowed if {@code true}
         * @return builder instance
         * @since 1.7.0
         */
        public Builder nullableMapValues(final boolean nullableMapValues) {
            this.nullableMapValues = nullableMapValues;
            return this;
        }

        /**
         * A hint used only for generating collections and arrays.
         * The specified elements will be inserted into the data structure
         * by the engine during the population process. The elements
         * are shuffled (in a reproducible manner).
         *
         * @param withElements elements to be inserted into the data structure
         *                     in addition to the random elements generated
         *                     by the engine
         * @return builder instance
         * @since 1.7.0
         */
        public Builder withElements(final List<?> withElements) {
            this.withElements = withElements;
            return this;
        }

        /**
         * Builds the object.
         *
         * @return the built instance.
         */
        public DataStructureHint build() {
            return new DataStructureHint(this);
        }
    }
}
