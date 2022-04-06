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
package org.instancio;

import org.instancio.generator.GeneratedHints;
import org.instancio.generator.GeneratorSpec;

import java.util.Optional;

/**
 * A generator of values of a specific type.
 *
 * @param <T> type to generate
 */
@FunctionalInterface
public interface Generator<T> extends GeneratorSpec<T> {

    /**
     * Returns a generated value. By default, generated values are random.
     * <p>
     * Generation parameters for common types such as strings, numbers, collections, etc.
     * can be customised by passing in custom generators.
     *
     * @return generated value
     */
    T generate();

    /**
     * If {@code true}, then this generator delegate object instantiation
     * to another generator supplied via {@link #setDelegate(Generator)}.
     *
     * @return {@code true} if this is a delegating generator
     */
    default boolean isDelegating() {
        return false;
    }

    /**
     * Set a delegate that will be responsible for instantiating an object
     * on behalf of this generator.
     *
     * @param delegate that will create the target object
     */
    default void setDelegate(Generator<?> delegate) {
        // no-op by default
    }

    /**
     * Target class to generate.
     * <p>
     * If {@link Optional#empty()} is returned, it will default to the field type
     * for fields, and element type for collection and array elements.
     * <p>
     * If the type is an interface, such as {@link java.util.Set}, will generate a default
     * implementation class such as {@link java.util.HashSet}.
     *
     * @return target class
     */
    default Optional<Class<?>> targetClass() {
        return Optional.empty();
    }

    /**
     * Returns hints, including collection sizes and whether values are nullable.
     *
     * @return generated hints
     */
    default GeneratedHints getHints() {
        // ignore children by default to ensure values created
        // from user-supplied generators are not modified
        return GeneratedHints.createIgnoreChildrenHint();
    }
}
