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

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Represents an origin selector of a conditional assignment.
 *
 * @see Assignment
 * @see Assign#given(TargetSelector)
 * @since 3.0.0
 */
@ExperimentalApi
public interface GivenOrigin {

    /**
     * Checks if the origin value matched satisfies given {@code predicate}.
     *
     * @param predicate that must be satisfied by value matched
     *                  by the origin selector
     * @param <T>       type the condition is evaluated against
     * @return assignment builder reference
     * @since 3.0.0
     */
    <T> GivenOriginPredicate satisfies(Predicate<T> predicate);

    /**
     * Checks if the origin value is equal to given {@code value}
     * using {@link Objects#equals(Object, Object)}.
     *
     * @param value the value the origin selector's match must be equal to
     * @param <T>   type the condition is evaluated against
     * @return assignment builder reference
     * @since 3.0.0
     */
    <T> GivenOriginPredicate is(T value);

    /**
     * Checks if the origin value is not equal to given {@code value}
     * using {@link Objects#equals(Object, Object)}.
     *
     * @param value the value the origin selector's match must NOT be equal to
     * @param <T>   type the condition is evaluated against
     * @return assignment builder reference
     * @since 3.0.0
     */
    <T> GivenOriginPredicate isNot(T value);

    /**
     * Checks if the origin value is equal to any of given {@code values}
     * using {@link Objects#equals(Object, Object)}.
     *
     * @param values the values to compare origin selector's match against
     * @param <T>    type the condition is evaluated against
     * @return assignment builder reference
     * @since 3.0.0
     */
    <T> GivenOriginPredicate isIn(T... values);
}
