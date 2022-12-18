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

import org.instancio.documentation.InternalApi;
import org.instancio.generator.Generator;
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
 * @since 2.0.0
 */
@InternalApi
public final class InternalGeneratorHint implements Hint<InternalGeneratorHint> {
    private final Class<?> targetClass;
    private final boolean isDelegating;
    private final boolean excludeFromCallbacks;
    private final boolean nullableResult;

    private InternalGeneratorHint(final Builder builder) {
        targetClass = builder.targetClass;
        isDelegating = builder.isDelegating;
        excludeFromCallbacks = builder.excludeFromCallbacks;
        nullableResult = builder.nullableResult;
    }

    /**
     * A hint indicating the type of object to generate.
     * <p>
     * Some generators may request a specific type to generate.
     * For example, collection generators may accept types via
     * {@code subtype()} method.
     *
     * @return target class
     * @since 2.0.0
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
     * @since 2.0.0
     */
    public boolean isDelegating() {
        return isDelegating;
    }

    /**
     * A hint indicating whether the generated object should be excluded
     * from {@code onComplete} callbacks.
     *
     * @return {@code true} if callbacks should not be invoked on generated object
     * @since 2.0.0
     */
    public boolean excludeFromCallbacks() {
        return excludeFromCallbacks;
    }

    /**
     * Indicates whether the value to generate is nullable.
     *
     * @return {@code true} if null is allowed to be generated
     * @since 2.0.0
     */
    public boolean nullableResult() {
        return nullableResult;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "GeneratorHint[", "]")
                .add("targetClass=" + (targetClass == null ? null : targetClass.getTypeName()))
                .add("isDelegating=" + isDelegating)
                .add("excludeFromCallbacks=" + excludeFromCallbacks)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Class<?> targetClass;
        private boolean isDelegating;
        private boolean excludeFromCallbacks;
        private boolean nullableResult;

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

        public Builder excludeFromCallbacks(final boolean excludeFromCallbacks) {
            this.excludeFromCallbacks = excludeFromCallbacks;
            return this;
        }

        public Builder nullableResult(final boolean nullableResult) {
            this.nullableResult = nullableResult;
            return this;
        }

        /**
         * Builds the object.
         *
         * @return the built instance.
         */
        public InternalGeneratorHint build() {
            return new InternalGeneratorHint(this);
        }
    }
}
