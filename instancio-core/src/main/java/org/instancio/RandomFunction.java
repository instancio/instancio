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

/**
 * A function that accepts an argument and produces a randomised result.
 *
 * @param <T> the input type
 * @param <R> the result type
 * @see RandomUnaryOperator
 * @since 5.0.0
 */
@ExperimentalApi
@FunctionalInterface
public interface RandomFunction<T, R> {

    /**
     * Applies this function to the given {@code input}.
     *
     * @param input  the function input
     * @param random instance for randomising the result
     * @return the function result
     * @since 5.0.0
     */
    @ExperimentalApi
    R apply(T input, Random random);
}
