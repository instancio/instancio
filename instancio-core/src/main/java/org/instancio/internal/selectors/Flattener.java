/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.internal.selectors;

import org.instancio.TargetSelector;

import java.util.List;

/**
 * Flattens selectors into a list, similar to {@code stream().flatMap()}.
 * <p>
 * This interface is an internal interface, not part of the public API.
 *
 * @since 1.3.0
 */
public interface Flattener {

    /**
     * Flattens selectors into a list.
     *
     * @return flat list of selectors
     * @since 1.3.0
     */
    List<TargetSelector> flatten();
}
