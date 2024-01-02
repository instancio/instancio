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

/**
 * Allows specifying target depth.
 *
 * @see DepthPredicateSelector
 * @see Selector
 * @since 2.14.0
 */
public interface DepthSelector {

    /**
     * Restricts this selector's target(s) to the specified depth.
     *
     * <p>When a selector {@code atDepth(N)} is converted {@code toScope()},
     * the semantics of {@link Selector#within(Scope...)} method still hold,
     * meaning: at depth equal to <b>or greater than</b> {@code N}.
     *
     * @param depth the depth at which selector applies
     * @return selector restricted to the specified depth
     * @since 2.14.0
     */
    @ExperimentalApi
    GroupableSelector atDepth(int depth);

}
