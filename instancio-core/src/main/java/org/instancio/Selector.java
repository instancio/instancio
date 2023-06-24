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

/**
 * A selector that can be:
 *
 * <ul>
 *   <li>grouped with other selectors</li>
 *   <li>narrowed down using {@link Scope}</li>
 *   <li>converted to {@link Scope}</li>
 * </ul>
 *
 * @see Select
 * @see TargetSelector
 * @since 1.2.0
 */
public interface Selector extends DepthSelector, GroupableSelector, ScopeableSelector, ConvertibleToScope {

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    ScopeableSelector atDepth(int depth);

    /**
     * {@inheritDoc}
     *
     * @since 1.3.0
     */
    @Override
    GroupableSelector within(Scope... scopes);
}