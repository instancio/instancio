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
package org.instancio.internal.generation;

import org.instancio.documentation.InternalApi;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.nodes.InternalNode;

@InternalApi
interface NodeHandler {

    NodeHandler NOOP_HANDLER = new NodeHandler() {
        @Override
        public GeneratorResult getResult(final InternalNode node) {
            return GeneratorResult.emptyResult();
        }
    };

    GeneratorResult getResult(InternalNode node);

}
