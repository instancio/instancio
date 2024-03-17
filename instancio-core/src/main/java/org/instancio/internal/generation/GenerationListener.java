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
package org.instancio.internal.generation;

import org.instancio.documentation.InternalApi;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.nodes.InternalNode;

/**
 * Listener interface for classes interested in when an object has been created.
 *
 * @since 1.3.3
 */
@InternalApi
public interface GenerationListener {

    /**
     * Invoked when an object has been instantiated.
     * The object may not yet be fully-populated.
     *
     * @param node   for which the object has been created
     * @param result containing either the object that was created, or
     *               {@code null} if value is 'nullable' or could not
     *               be generated.
     * @since 1.3.3
     */
    void objectCreated(InternalNode node, GeneratorResult result);
}
