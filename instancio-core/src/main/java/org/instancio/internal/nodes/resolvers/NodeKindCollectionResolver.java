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
package org.instancio.internal.nodes.resolvers;

import org.instancio.internal.nodes.NodeKind;
import org.instancio.internal.nodes.NodeKindResolver;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;

class NodeKindCollectionResolver implements NodeKindResolver {

    @Override
    public Optional<NodeKind> resolve(final Class<?> targetClass) {
        // If something is declared as an Iterable, also treat it as a collection
        return (Collection.class.isAssignableFrom(targetClass) || targetClass == Iterable.class)
                // EnumSet is classified as NodeKind.CONTAINER because it
                // does not provide a default constructor
                && targetClass != EnumSet.class
                ? Optional.of(NodeKind.COLLECTION)
                : Optional.empty();
    }
}
