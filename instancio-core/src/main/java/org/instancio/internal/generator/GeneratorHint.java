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
package org.instancio.internal.generator;

import org.instancio.Generator;
import org.instancio.generator.Hint;
import org.instancio.generator.Hints;

import java.util.StringJoiner;

/**
 * Internal hint related to {@link Generator Generators}.
 * <p>
 * This class is not part of the public API.
 *
 * @see Hints
 * @see Generator
 * @since 1.7.0
 */
public final class GeneratorHint implements Hint<GeneratorHint> {
    private final Class<?> targetClass;
    private final boolean isDelegating;

    private GeneratorHint(final Builder builder) {
        targetClass = builder.targetClass;
        isDelegating = builder.isDelegating;
    }

    /**
     * A hint indicating the type of object to generate.
     * <p>
     * Some generators may request a specific type to generate.
     * For example, collection generators may accept types via
     * {@code subtype()} method.
     *
     * @return target class
     */
    public Class<?> targetClass() {
        return targetClass;
    }

    /**
     * A hint indicating whether a generator delegates instantiation
     * to another generator. This occurs when the generator does not
     * have enough information to determine which type to instantiate.
     *
     * @return {@code true} a generator is delegating, {@code false} otherwise
     */
    public boolean isDelegating() {
        return isDelegating;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "GeneratorHint[", "]")
                .add("targetClass=" + (targetClass == null ? null : targetClass.getTypeName()))
                .add("isDelegating=" + isDelegating)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Class<?> targetClass;
        private boolean isDelegating;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder targetClass(final Class<?> targetClass) {
            this.targetClass = targetClass;
            return this;
        }

        public Builder delegating(final boolean isDelegating) {
            this.isDelegating = isDelegating;
            return this;
        }

        /**
         * Builds the object.
         *
         * @return the built instance.
         */
        public GeneratorHint build() {
            return new GeneratorHint(this);
        }
    }
}
