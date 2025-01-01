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
 * Interface for specifying the depth of a selector's target.
 *
 * @see DepthPredicateSelector
 * @see Selector
 * @since 2.14.0
 */
public interface DepthSelector {

    /**
     * Restricts this selector's targets to the specified depth.
     * The selector will only apply to targets at the specified depth.
     *
     * <p>When a selector with {@code atDepth(N)} is converted
     * using {@code toScope()}, the semantics of the
     * {@link Selector#within(Scope...)} method still apply,
     * meaning that the selection applies to targets at depth
     * equal to <b>or greater than</b> {@code N}.
     *
     * <p>The root object is considered to be at depth 0.
     *
     * @param depth the depth at which the selector should apply
     * @return a selector restricted to the specified depth
     * @see DepthPredicateSelector
     * @since 2.14.0
     */
    @ExperimentalApi
    GroupableSelector atDepth(int depth);

}
