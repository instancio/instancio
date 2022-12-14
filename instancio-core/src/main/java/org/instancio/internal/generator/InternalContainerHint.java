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

import org.instancio.generator.Hint;
import org.jetbrains.annotations.ApiStatus;

/**
 * Hint containing details on how to build a container. Container represents
 * a data structure that can be built using static methods.
 * <p>
 * An example of a container is {@link java.util.Optional}
 * or the following {@code ImmutableList}:
 *
 * <pre>{@code
 *     ImmutableList.<String>builder()
 *              .add("one")
 *              .add("tqo")
 *              .build();
 * }</pre>
 *
 * @see ContainerCreateFunction
 * @see ContainerAddFunction
 * @see ContainerBuildFunction
 * @since 2.0.0
 */
@ApiStatus.Internal
public final class InternalContainerHint implements Hint<InternalContainerHint> {
    private static final InternalContainerHint EMPTY_HINT = builder().build();

    private final ContainerCreateFunction<?> createFunction;
    private final ContainerAddFunction<?> addFunction;
    private final ContainerBuildFunction<?, ?> buildFunction;
    private final int generateEntries;

    /**
     * Returns an empty hint containing default values.
     *
     * @return empty hint
     * @since 2.0.0
     */
    public static InternalContainerHint empty() {
        return EMPTY_HINT;
    }

    /**
     * Returns the function for instantiating the container.
     *
     * @param <C> container type
     * @return function for instantiating the container
     */
    @SuppressWarnings("unchecked")
    public <C> ContainerCreateFunction<C> createFunction() {
        return (ContainerCreateFunction<C>) createFunction;
    }

    /**
     * Returns the function for adding items to the container.
     *
     * @param <C> container type
     * @return function for adding items to the container
     */
    @SuppressWarnings("unchecked")
    public <C> ContainerAddFunction<C> addFunction() {
        return (ContainerAddFunction<C>) addFunction;
    }

    /**
     * Returns the function for building the container.
     *
     * @param <B> container's builder type
     * @param <C> container type
     * @return function for building the container.
     */
    @SuppressWarnings("unchecked")
    public <B, C> ContainerBuildFunction<B, C> buildFunction() {
        return (ContainerBuildFunction<B, C>) buildFunction;
    }

    /**
     * Indicates how many entries the engine should generate
     * and add to the container.
     *
     * @return number of entries to add
     */
    public int generateEntries() {
        return generateEntries;
    }

    private InternalContainerHint(final Builder builder) {
        createFunction = builder.createFunction;
        addFunction = builder.addFunction;
        buildFunction = builder.buildFunction;
        generateEntries = builder.generateEntries;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private ContainerCreateFunction<?> createFunction;
        private ContainerAddFunction<?> addFunction;
        private ContainerBuildFunction<?, ?> buildFunction;
        private int generateEntries;

        private Builder() {
        }

        /**
         * Specifies the function for instantiating the container.
         *
         * @param function for instantiating the container
         * @param <C>      container type
         * @return builder instance
         * @since 2.0.0
         */
        public <C> Builder createFunction(final ContainerCreateFunction<C> function) {
            this.createFunction = function;
            return this;
        }

        /**
         * Specifies the function for adding objects to the container.
         *
         * @param function for adding objects to the container
         * @param <C>      container type
         * @return builder instance
         * @since 2.0.0
         */
        public <C> Builder addFunction(final ContainerAddFunction<C> function) {
            this.addFunction = function;
            return this;
        }

        /**
         * Specifies the function for building the container.
         *
         * @param function for building the container
         * @param <B>      container's builder type
         * @param <C>      container type
         * @return builder instance
         * @since 2.0.0
         */
        public <B, C> Builder buildFunction(final ContainerBuildFunction<B, C> function) {
            this.buildFunction = function;
            return this;
        }

        /**
         * Indicates how many entries the engine should generate
         * and add to the container.
         *
         * @param generateEntries number of objects to generate
         * @return builder instance
         * @since 2.0.0
         */
        public Builder generateEntries(final int generateEntries) {
            this.generateEntries = generateEntries;
            return this;
        }

        public InternalContainerHint build() {
            return new InternalContainerHint(this);
        }
    }
}

