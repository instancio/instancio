/*
 * Copyright 2022-2023 the original author or authors.
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
 * A required action that must follow the {@code When.valueOf()} method.
 *
 * @see Conditional
 * @see When#valueOf(TargetSelector)
 * @since 3.0.0
 */
@ExperimentalApi
public interface ConditionalValueOfRequiredAction {

    /**
     * Generates values using built-in generators provided by the {@code gen}
     * parameter, of type {@link Generators}.
     *
     * <p>This method provides the same functionality as the top-level API:
     * {@link InstancioApi#generate(TargetSelector, GeneratorSpecProvider)}
     *
     * @param selector for fields and/or classes this method should be applied to
     * @param gen      provider of customisable built-in generators (also known as specs)
     * @param <T>      type of object to generate
     * @return action builder reference for chaining multiple actions
     * @since 3.0.0
     */
    <T> ConditionalValueOfAction generate(TargetSelector selector, GeneratorSpecProvider<T> gen);

    /**
     * Generates values using arbitrary generator specs.
     *
     * <p>This method provides the same functionality as the top-level API:
     * {@link InstancioApi#generate(TargetSelector, GeneratorSpec)}
     *
     * @param selector for fields and/or classes this method should be applied to
     * @param spec     generator spec for generating values
     * @param <T>      type of object to generate
     * @return action builder reference for chaining multiple actions
     * @since 3.0.0
     */
    <T> ConditionalValueOfAction generate(TargetSelector selector, GeneratorSpec<T> spec);

    /**
     * Sets a value to matching selector targets.
     *
     * <p>This method provides the same functionality as the top-level API:
     * {@link InstancioApi#set(TargetSelector, Object)}
     *
     * @param selector for fields and/or classes this method should be applied to
     * @param value    value to set
     * @param <T>      type of the value
     * @return action builder reference for chaining multiple actions
     * @since 3.0.0
     */
    <T> ConditionalValueOfAction set(TargetSelector selector, T value);

    /**
     * Supplies an object using a {@link Generator} to matching selector targets.
     *
     * <p>This method provides the same functionality as the top-level API:
     * {@link InstancioApi#supply(TargetSelector, Generator)}
     *
     * @param selector  for fields and/or classes this method should be applied to
     * @param generator for generating values
     * @param <T>       type of the value to generate
     * @return action builder reference for chaining multiple actions
     * @since 3.0.0
     */
    <T> ConditionalValueOfAction supply(TargetSelector selector, Generator<T> generator);

    /**
     * Supplies an object using a {@link Supplier}.
     *
     * <p>This method provides the same functionality as the top-level API:
     * {@link InstancioApi#supply(TargetSelector, Supplier)}
     *
     * @param selector for fields and/or classes this method should be applied to
     * @param supplier for supplying values
     * @param <T>      type of the supplied value
     * @return action builder reference for chaining multiple actions
     * @since 3.0.0
     */
    <T> ConditionalValueOfAction supply(TargetSelector selector, Supplier<T> supplier);
}
