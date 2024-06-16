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
package org.instancio;

import org.instancio.documentation.ExperimentalApi;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An assigment containing origin and destination selectors.
 *
 * @since 3.0.0
 */
@ExperimentalApi
public interface ValueOfOriginDestination extends Assignment {

    /**
     * Specifies a function for mapping the value of the
     * origin selector to another value.
     *
     * @param valueMapper a function for mapping the origin value
     * @param <T>         the origin value type
     * @param <R>         the destination value type
     * @return continuation of the builder for optionally specifying a predicate
     * @since 3.0.0
     * @see #as(RandomFunction)
     */
    <T, R> ValueOfOriginDestinationPredicate as(Function<T, R> valueMapper);

    /**
     * Specifies a function for mapping the value of the
     * origin selector to another (randomised) value.
     *
     * @param valueMapper a function for mapping the origin value
     * @param <T>         the origin value type
     * @param <R>         the destination value type
     * @return continuation of the builder for optionally specifying a predicate
     * @since 5.0.0
     * @see #as(Function)
     */
    <T, R> ValueOfOriginDestinationPredicate as(RandomFunction<T, R> valueMapper);

    /**
     * A predicate that must be satisfied by the value matched
     * by the origin selector.
     *
     * @param predicate to check the origin value against
     * @param <T>       the type of the value matched by the origin selector
     * @return an assignment
     * @since 3.0.0
     */
    <T> Assignment when(Predicate<T> predicate);
}
