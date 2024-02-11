/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.generator.specs;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.GeneratorSpec;

import java.util.Map;

/**
 * A generator spec that emits given items to a selector's target.
 *
 * @since 2.12.0
 */
@ExperimentalApi
public interface EmitGeneratorSpec<T> extends GeneratorSpec<T> {

    /**
     * Emit given {@code items}.
     *
     * <p>This method can be invoked multiple times to add more items to emit.
     *
     * @param items to emit, {@code null} array is not allowed,
     *              but the array itself may contain {@code null} elements
     *              unless the target is a {@link Map} key
     * @return spec builder
     * @see #items(Iterable)
     * @see #item(Object, int)
     * @since 2.12.0
     */
    EmitGeneratorSpec<T> items(T... items);

    /**
     * Emit given {@code items}.
     *
     * <p>This method can be invoked multiple times to add more items to emit.
     *
     * @param items to emit, {@code null} iterable is not allowed,
     *              but the iterable itself may contain {@code null} elements
     *              unless the target is a {@link Map} key
     * @return spec builder
     * @see #items(Object[])
     * @see #item(Object, int)
     * @since 2.13.0
     */
    EmitGeneratorSpec<T> items(Iterable<? extends T> items);

    /**
     * Emit given {@code item} {@code n} number of times.
     *
     * <p>This method can be invoked multiple times and combined
     * with {@link #items(Object[])}.
     *
     * @param item to emit, {@code null} allowed
     *             unless the target is a {@link Map} key
     * @param n    number of times the item should be emitted;
     *             must not be negative
     * @return spec builder
     * @see #items(Object[])
     * @see #items(Iterable)
     * @since 2.12.0
     */
    EmitGeneratorSpec<T> item(T item, int n);

    /**
     * Specifies that items should be emitted in random order.
     *
     * @return spec builder
     * @since 2.12.0
     */
    EmitGeneratorSpec<T> shuffle();

    /**
     * Specifies that if any of the provided items were not emitted,
     * they should be ignored (the default is to throw an exception).
     *
     * @return spec builder
     * @since 2.12.0
     */
    EmitGeneratorSpec<T> ignoreUnused();

    /**
     * Specifies that {@code null} should be generated if an insufficient
     * number of items were provided.
     *
     * @return spec builder
     * @since 2.12.0
     */
    EmitGeneratorSpec<T> whenEmptyEmitNull();

    /**
     * Specifies that a random value should be generated if an insufficient
     * number of items were provided (default behaviour).
     *
     * @return spec builder
     * @since 2.12.0
     */
    EmitGeneratorSpec<T> whenEmptyEmitRandom();

    /**
     * Specifies that an exception should be raised if an insufficient
     * number of items were provided.
     *
     * @return spec builder
     * @since 2.12.0
     */
    EmitGeneratorSpec<T> whenEmptyThrowException();

    /**
     * Specifies that the same items should be re-emitted again
     * if provided items are exhausted.
     *
     * @return spec builder
     * @since 4.3.0
     */
    EmitGeneratorSpec<T> whenEmptyRecycle();

}
