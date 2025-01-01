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

import java.util.function.Predicate;

/**
 * An assigment containing origin and destination selectors,
 * and a predicate that the origin value must satisfy
 * for the assignment to be applied.
 *
 * @since 3.0.0
 */
@ExperimentalApi
public interface ValueOfOriginDestinationPredicate extends Assignment {

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
