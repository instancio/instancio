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
package org.instancio.internal;

import org.instancio.generator.AfterGenerate;
import org.instancio.internal.nodes.Node;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
interface NodePopulationFilter {

    /**
     * Determines whether given node should be populated or skipped.
     *
     * @param node          to evaluate
     * @param afterGenerate hint specifying action to take for the given object
     * @param obj           what the object is varies depending on filter implementation
     * @return {@code true} if node should be skipped, {@code false} otherwise
     */
    boolean shouldSkip(Node node, AfterGenerate afterGenerate, Object obj);

}
