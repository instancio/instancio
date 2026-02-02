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

import java.util.function.Predicate;

/**
 * Interface for specifying the depth of a selector's
 * target using a predicate.
 *
 * @see DepthSelector
 * @see PredicateSelector
 * @since 2.14.0
 */
public interface DepthPredicateSelector {

    /**
     * Restricts this selector's targets to a depth that
     * satisfies the given {@code predicate}.
     *
     * <p>For example, a predicate {@code atDepth(depth -> depth == 1)}
     * will restrict the selector to targets at depth 1 only.
     *
     * <p>The root object is considered to be at depth 0.
     *
     * @param predicate the predicate specifying the acceptable depth
     * @return a selector restricted to the specified depth
     * @see DepthSelector
     * @since 2.14.0
     */
    @ExperimentalApi
    ScopeableSelector atDepth(Predicate<Integer> predicate);

}
