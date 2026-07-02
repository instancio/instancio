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

/**
 * A selector returned after specifying an index on an
 * {@link ElementOfSelector} (e.g. {@link ElementOfSelector#at(int) at},
 * {@link ElementOfSelector#first() first},
 * {@link ElementOfSelector#last() last}).
 *
 * <p>It can be used directly as a {@link TargetSelector} to target the entire
 * element, or further narrowed via {@link #field(GetMethodSelector)} or
 * {@link #target(TargetSelector)}.
 *
 * @see ElementOfSelector
 * @since 6.0.0
 */
@ExperimentalApi
public interface IndexedElementSelector extends GroupableSelector {

    /**
     * Selects a field within the element(s) identified by the preceding
     * index selection.
     *
     * <p>The method reference must resolve to a field of the element type.
     * For records, this corresponds to a record component.
     *
     * @param methodRef getter method reference for the target field
     * @param <T>       the type declaring the method
     * @param <R>       the return type of the method
     * @return a target selector for the specified field within the element(s)
     * @since 6.0.0
     */
    @ExperimentalApi
    <T, R> GroupableSelector field(GetMethodSelector<T, R> methodRef);

    /**
     * Selects targets matched by the given selector within the element(s)
     * identified by the preceding index selection.
     *
     * <p>The inner selector is evaluated against each target inside the
     * element subtree, so selectors such as {@link Select#allStrings()}
     * or {@link Select#types()} match anywhere within the selected
     * element(s). The selector must not itself be an
     * {@link ElementOfSelector}.
     *
     * @param selector the selector to apply within the element(s)
     * @return a selector for the matched targets within the element(s)
     * @since 6.0.0
     */
    @ExperimentalApi
    GroupableSelector target(TargetSelector selector);

    /**
     * {@inheritDoc}
     *
     * @since 6.0.0
     */
    @Override
    @ExperimentalApi
    TargetSelector lenient();
}
