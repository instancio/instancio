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

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A condition that must be specified by the value of an origin selector.
 *
 * @see Conditional
 * @see When#valueOf(TargetSelector)
 * @since 3.0.0
 */
@ExperimentalApi
public interface ConditionalValueOf {

    /**
     * Checks whether the value matched by the origin selector
     * satisfies the given predicate.
     *
     * @param predicate that must be satisfied by value matched
     *                  by the origin selector
     * @param <T>       type the condition is evaluated against
     * @return available actions to be performed if the condition is satisfied
     * @since 3.0.0
     */
    <T> ConditionalValueOfRequiredAction satisfies(Predicate<T> predicate);

    /**
     * Checks whether the value matched by the origin selector is equal
     * to the specified {@code value}. The comparison is performed
     * using {@link Objects#equals(Object, Object)}.
     *
     * @param value the value the origin selector's match must be equal to
     * @param <T>   type the condition is evaluated against
     * @return available actions to be performed if the condition is satisfied
     * @since 3.0.0
     */
    <T> ConditionalValueOfRequiredAction is(T value);

    /**
     * Checks whether the value matched by the origin selector is equal
     * to any of the specified {@code values}. The comparison is performed
     * for each value using {@link Objects#equals(Object, Object)}.
     *
     * @param values the values to compare origin selector's match against
     * @param <T>    type the condition is evaluated against
     * @return available actions to be performed if the condition is satisfied
     * @since 3.0.0
     */
    @SuppressWarnings("PMD.LinguisticNaming")
    <T> ConditionalValueOfRequiredAction isIn(T... values);
}
