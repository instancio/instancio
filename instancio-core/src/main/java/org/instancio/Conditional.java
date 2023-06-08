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

/**
 * A conditional expression that can be passed to
 * {@link InstancioApi#when(Conditional...)}.
 *
 * <p>Conditionals can be constructed using one of the static
 * factory methods provided by the {@link When} class.
 *
 * <p>A complete conditional consists of:
 *
 * <ul>
 *   <li><b>origin</b> - a selector that must satisfy the condition</li>
 *   <li><b>condition</b> (predicate) that must be satisfied by the origin</li>
 *   <li><b>actions</b> - one or more actions that will be applied to
 *       destination selectors if the condition is satisfied</li>
 * </ul>
 *
 * @see When
 * @since 3.0.0
 */
@ExperimentalApi
public interface Conditional {
}
