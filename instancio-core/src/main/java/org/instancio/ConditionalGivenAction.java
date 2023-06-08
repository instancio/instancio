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
 * Optional action(s) that can be added to a {@code When.given()} conditional.
 *
 * @see Conditional
 * @see When#given(TargetSelector, TargetSelector)
 * @since 3.0.0
 */
@ExperimentalApi
public interface ConditionalGivenAction extends ConditionalGivenRequiredAction, Conditional {

    /**
     * Generates values using built-in generators provided by the {@code gen}
     * parameter, of type {@link Generators}.
     *
     * @param gen provider of customisable built-in generators (also known as specs)
     * @param <T> the type of object to generate
     * @return a complete conditional
     * @since 3.0.0
     */
    <T> Conditional elseGenerate(GeneratorSpecProvider<T> gen);

    /**
     * Generates values using arbitrary generator specs.
     *
     * @param spec generator spec for generating values
     * @param <T>  the type of object to generate
     * @return a complete conditional
     * @since 3.0.0
     */
    <T> Conditional elseGenerate(GeneratorSpec<T> spec);

    /**
     * Sets a value to matching selector targets.
     *
     * @param value value to set
     * @param <T>   the type of object to generate
     * @return a complete conditional
     * @since 3.0.0
     */
    <T> Conditional elseSet(T value);

    /**
     * Supplies an object using a {@link Generator} to matching selector targets.
     *
     * @param generator for generating values
     * @param <T>       the type of object to generate
     * @return a complete conditional
     * @since 3.0.0
     */
    <T> Conditional elseSupply(Generator<T> generator);

    /**
     * Supplies an object using a {@link Supplier}.
     *
     * @param supplier for supplying values
     * @param <T>      the type of object to generate
     * @return a complete conditional
     * @since 3.0.0
     */
    <T> Conditional elseSupply(Supplier<T> supplier);
}
