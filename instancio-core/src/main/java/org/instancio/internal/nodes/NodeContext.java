/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.internal.nodes;

import org.instancio.internal.context.SubtypeSelectorMap;
import org.instancio.internal.nodes.resolvers.NodeKindArrayResolver;
import org.instancio.internal.nodes.resolvers.NodeKindCollectionResolver;
import org.instancio.internal.nodes.resolvers.NodeKindContainerResolver;
import org.instancio.internal.nodes.resolvers.NodeKindMapResolver;
import org.instancio.internal.nodes.resolvers.NodeKindRecordResolver;
import org.instancio.internal.spi.InternalContainerFactoryProvider;

import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class NodeContext {

    private final Map<TypeVariable<?>, Class<?>> rootTypeMap;
    private final SubtypeSelectorMap subtypeSelectorMap;
    private final List<NodeKindResolver> nodeKindResolvers;

    public NodeContext(
            final Map<TypeVariable<?>, Class<?>> rootTypeMap,
            final SubtypeSelectorMap subtypeSelectorMap,
            final List<InternalContainerFactoryProvider> containerFactories) {

        this.rootTypeMap = Collections.unmodifiableMap(rootTypeMap);
        this.subtypeSelectorMap = subtypeSelectorMap;
        this.nodeKindResolvers = Arrays.asList(
                new NodeKindCollectionResolver(),
                new NodeKindMapResolver(),
                new NodeKindArrayResolver(),
                new NodeKindRecordResolver(),
                new NodeKindContainerResolver(Collections.unmodifiableList(containerFactories)));
    }

    public List<NodeKindResolver> getNodeKindResolvers() {
        return nodeKindResolvers;
    }

    public Map<TypeVariable<?>, Class<?>> getRootTypeMap() {
        return rootTypeMap;
    }

    Optional<Class<?>> getUserSuppliedSubtype(final Node node) {
        return subtypeSelectorMap.getSubtype(node);
    }
}
