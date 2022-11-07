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
import org.instancio.generator.specs.ArrayGeneratorSpec;
import org.instancio.generator.specs.CollectionGeneratorSpec;
import org.instancio.generator.specs.MapGeneratorSpec;
import org.instancio.generators.Generators;
import org.instancio.internal.PrimitiveWrapperBiLookup;
import org.instancio.util.TypeUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * A generator of values of a specific type.
 *
 * @param <T> type to generate
 * @since 1.0.1
 */
@FunctionalInterface
public interface Generator<T> extends GeneratorSpec<T> {

    /**
     * Returns a generated value.
     * <p>
     * If the generated value is random, it needs to be generated using the given
     * {@link Random} instance. This ensures the data is generated with
     * the same seed value and allows random data to be reproduced by specifying
     * the seed value.
     *
     * @param random provider for generating random values
     * @return generated value
     */
    T generate(Random random);

    /**
     * If {@code true}, then this generator delegate object instantiation
     * to another generator supplied via {@link #setDelegate(Generator)}.
     * <p>
     * A generator is delegating when it does not know the type it needs
     * to generate. For example, the collection generator could generate
     * an {@code ArrayList}, a {@code HashSet}, or some other collection type.
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
     * Returns the public API method name of this generator.
     * Will return an empty result if this instance is a lambda.
     *
     * @return spec name, if defined
     */
    default Optional<String> apiMethodName() {
        return Optional.ofNullable(Generators.getApiMethod(getClass()));
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
     * Checks whether this generator can generate given type.
     *
     * @param type to check
     * @return {@code true} if generator can
     */
    default boolean supports(Class<?> type) {
        if (type.isArray()) {
            return this instanceof ArrayGeneratorSpec;
        }
        if (Collection.class.isAssignableFrom(type)) {
            return this instanceof CollectionGeneratorSpec;
        }
        if (Map.class.isAssignableFrom(type)) {
            return this instanceof MapGeneratorSpec;
        }
        final Class<?> typeArg = TypeUtils.getGeneratorTypeArgument(getClass());
        return typeArg == null // couldn't determine type arg ('this' is probably a lambda)
                || typeArg.isAssignableFrom(type)
                || PrimitiveWrapperBiLookup.findEquivalent(typeArg)
                .filter(type::isAssignableFrom)
                .isPresent();
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
