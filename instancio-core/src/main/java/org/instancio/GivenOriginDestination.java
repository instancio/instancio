/*
 * Copyright 2022-2026 the original author or authors.
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

import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generators.Generators;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Represents an origin and a destination of a conditional assigment.
 *
 * @see Assign#given(TargetSelector, TargetSelector)
 * @see Assignment
 * @see GivenOriginDestinationAction
 * @since 3.0.0
 */
@ExperimentalApi
public interface GivenOriginDestination {

    /**
     * Generates values using built-in generators provided by the {@code gen}
     * parameter, of type {@link Generators}.
     *
     * <p>This method provides the same functionality as the top-level API:
     * {@link InstancioApi#generate(TargetSelector, GeneratorSpecProvider)}
     *
     * @param predicate that must be satisfied by a value matching the origin selector
     * @param gen       provider of customisable built-in generators (also known as specs)
     * @param <S>       the type of object the predicate is evaluated against
     * @param <T>       the type of object to generate
     * @return assignment builder reference
     * @since 3.0.0
     */
    <S, T> GivenOriginDestinationAction generate(Predicate<S> predicate, GeneratorSpecProvider<T> gen);

    /**
     * Generates values using arbitrary generator specs.
     *
     * <p>This method provides the same functionality as the top-level API:
     * {@link InstancioApi#generate(TargetSelector, GeneratorSpec)}
     *
     * @param predicate that must be satisfied by a value matching the origin selector
     * @param spec      generator spec for generating values
     * @param <S>       the type of object the predicate is evaluated against
     * @param <T>       the type of object to generate
     * @return assignment builder reference
     * @since 3.0.0
     */
    <S, T> GivenOriginDestinationAction generate(Predicate<S> predicate, GeneratorSpec<T> spec);

    /**
     * Sets a value to matching selector targets.
     *
     * <p>This method provides the same functionality as the top-level API:
     * {@link InstancioApi#set(TargetSelector, Object)}
     *
     * @param predicate that must be satisfied by a value matching the origin selector
     * @param value     value to set
     * @param <S>       the type of object the predicate is evaluated against
     * @param <T>       the type of object to set
     * @return assignment builder reference
     * @since 3.0.0
     */
    <S, T> GivenOriginDestinationAction set(Predicate<S> predicate, T value);

    /**
     * Supplies an object using a {@link Generator} to matching selector targets.
     *
     * <p>This method provides the same functionality as the top-level API:
     * {@link InstancioApi#supply(TargetSelector, Generator)}
     *
     * @param predicate that must be satisfied by a value matching the origin selector
     * @param generator for generating values
     * @param <S>       the type of object the predicate is evaluated against
     * @param <T>       the type of object to generate
     * @return assignment builder reference
     * @since 3.0.0
     */
    <S, T> GivenOriginDestinationAction supply(Predicate<S> predicate, Generator<T> generator);

    /**
     * Supplies an object using a {@link Supplier}.
     *
     * <p>This method provides the same functionality as the top-level API:
     * {@link InstancioApi#supply(TargetSelector, Supplier)}
     *
     * @param predicate that must be satisfied by a value matching the origin selector
     * @param supplier  for supplying values
     * @param <S>       the type of object the predicate is evaluated against
     * @param <T>       the type of object to supply
     * @return assignment builder reference
     * @since 3.0.0
     */
    <S, T> GivenOriginDestinationAction supply(Predicate<S> predicate, Supplier<T> supplier);

}
