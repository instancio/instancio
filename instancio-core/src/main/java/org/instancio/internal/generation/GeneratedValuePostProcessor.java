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
package org.instancio.internal.generation;

import org.instancio.documentation.InternalApi;
import org.instancio.generator.Generator;
import org.instancio.internal.nodes.InternalNode;

/**
 * Post-processor for generated values.
 *
 * @since 2.4.0
 */
@InternalApi
interface GeneratedValuePostProcessor {

    /**
     * Processes the specified value.
     *
     * @param value     that was generated
     * @param node      for which the value was generated
     * @param generator that generated the value
     * @return processed value, or the same value if no processing was done
     */
    Object process(Object value, InternalNode node, Generator<?> generator);
}
