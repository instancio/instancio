/*
 * Copyright 2022-2025 the original author or authors.
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

import java.util.function.Supplier;

/**
 * Value of a target selector of an assignment.
 *
 * @see Assignment
 * @see Assign#valueOf(TargetSelector)
 * @since 3.0.0
 */
@ExperimentalApi
public interface ValueOf {

    /**
     * Specifies the destination selector for the assignment.
     *
     * @param destination selector
     * @return an assigment containing origin and destination selectors
     * @since 3.0.0
     */
    ValueOfOriginDestination to(TargetSelector destination);

    /**
     * Specifies the destination method reference for the assignment.
     * This is a shorthand API of {@link #to(TargetSelector)} allowing:
     *
     * <pre>{@code
     * to(field(Pojo::getValue))
     * }</pre>
     *
     * <p>to be specified as a slightly shorter version:
     *
     * <pre>{@code
     * to(Pojo::getValue)
     * }</pre>
     *
     * @param destination method reference
     * @param <T>         type declaring the method
     * @param <R>         return type of the method
     * @return an assigment containing origin and destination selectors
     * @since 3.0.0
     */
    <T, R> ValueOfOriginDestination to(GetMethodSelector<T, R> destination);

    /**
     * Generates values using built-in generators provided by the {@code gen}
     * parameter, of type {@link Generators}.
     *
     * <p>This method provides the same functionality as the top-level API:
     * {@link InstancioApi#generate(TargetSelector, GeneratorSpecProvider)}
     *
     * @param gen provider of customisable built-in generators (also known as specs)
     * @param <T> the type of object to generate
     * @return an assignment
     * @since 3.0.0
     */
    <T> Assignment generate(GeneratorSpecProvider<T> gen);

    /**
     * Generates values using arbitrary generator specs.
     *
     * <p>This method provides the same functionality as the top-level API:
     * {@link InstancioApi#generate(TargetSelector, GeneratorSpec)}
     *
     * @param spec generator spec for generating values
     * @param <T>  the type of object to generate
     * @return an assignment
     * @since 3.0.0
     */
    <T> Assignment generate(GeneratorSpec<T> spec);

    /**
     * Sets a value to matching selector targets.
     *
     * <p>This method provides the same functionality as the top-level API:
     * {@link InstancioApi#set(TargetSelector, Object)}
     *
     * @param value value to set
     * @param <T>   the type of object to set
     * @return an assignment
     * @since 3.0.0
     */
    <T> Assignment set(T value);

    /**
     * Supplies an object using a {@link Generator} to matching selector targets.
     *
     * <p>This method provides the same functionality as the top-level API:
     * {@link InstancioApi#supply(TargetSelector, Generator)}
     *
     * @param generator for generating values
     * @param <T>       the type of object to generate
     * @return an assignment
     * @since 3.0.0
     */
    <T> Assignment supply(Generator<T> generator);

    /**
     * Supplies an object using a {@link Supplier}.
     *
     * <p>This method provides the same functionality as the top-level API:
     * {@link InstancioApi#supply(TargetSelector, Supplier)}
     *
     * @param supplier for supplying values
     * @param <T>      the type of object to supply
     * @return an assignment
     * @since 3.0.0
     */
    <T> Assignment supply(Supplier<T> supplier);

}
