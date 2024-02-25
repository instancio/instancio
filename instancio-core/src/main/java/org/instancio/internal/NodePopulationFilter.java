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
package org.instancio.internal;

import org.instancio.documentation.InternalApi;
import org.instancio.generator.AfterGenerate;
import org.instancio.internal.nodes.InternalNode;

@InternalApi
interface NodePopulationFilter {

    /**
     * Determines whether given node should be populated or skipped.
     *
     * @param node          to evaluate
     * @param afterGenerate hint specifying action to take for the given object
     * @param owner         the object created from the parent of the {@code node}
     * @return result indicating what the action should be
     */
    NodeFilterResult filter(InternalNode node, AfterGenerate afterGenerate, Object owner);

    /**
     * The result determines what should happen to a given node.
     */
    enum NodeFilterResult {

        /**
         * Skip the node - no value will be generated.
         */
        SKIP,

        /**
         * Generate a value for a given node.
         */
        GENERATE,

        /**
         * Populate an initialised object.
         *
         * <p>Applicable only to field nodes that are:
         *
         * <ul>
         *   <li>not {@code null}, and</li>
         *   <li>the field's value is a POJO, collection, map, or array</li>
         * </ul>
         */
        POPULATE
    }
}
