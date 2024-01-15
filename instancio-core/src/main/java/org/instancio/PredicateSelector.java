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
 * A selector for matching targets using predicates.
 *
 * @see Select
 * @since 1.6.0
 */
public interface PredicateSelector
        extends GroupableSelector, DepthSelector, DepthPredicateSelector, WithinScope {

    /**
     * {@inheritDoc}
     *
     * @since 4.1.0
     */
    @Override
    @ExperimentalApi
    GroupableSelector within(Scope... scopes);
}
