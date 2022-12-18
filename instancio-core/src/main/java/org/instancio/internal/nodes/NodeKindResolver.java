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
package org.instancio.internal.nodes;

import org.instancio.documentation.InternalApi;

import java.util.Optional;

/**
 * Resolves {@link NodeKind} based on node's target class.
 *
 * @since 1.5.0
 */
@InternalApi
public interface NodeKindResolver {

    /**
     * Returns {@link NodeKind} for given target class,
     * or an empty result if it could not be resolved.
     *
     * @param targetClass to resolve
     * @return node kind or an empty result
     * @since 1.5.0
     */
    Optional<NodeKind> resolve(Class<?> targetClass);
}
