/*
 *  Copyright 2022-2023 the original author or authors.
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
package org.instancio.internal;

import org.instancio.Model;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.nodes.NodeContext;
import org.instancio.internal.nodes.NodeFactory;

final class InternalModel<T> implements Model<T> {

    private final ModelContext<T> modelContext;
    private final Node rootNode;

    InternalModel(ModelContext<T> modelContext) {
        this.modelContext = modelContext;
        this.rootNode = createRootNode();
    }

    ModelContext<T> getModelContext() {
        return modelContext;
    }

    Node getRootNode() {
        return rootNode;
    }

    private Node createRootNode() {
        final NodeContext nodeContext = NodeContext.builder()
                .maxDepth(modelContext.getMaxDepth())
                .rootTypeMap(modelContext.getRootTypeMap())
                .ignoredSelectorMap(modelContext.getIgnoredSelectorMap())
                .subtypeSelectorMap(modelContext.getSubtypeSelectorMap())
                .subtypeMappingFromSettings(modelContext.getSettings().getSubtypeMap())
                .containerFactories(modelContext.getContainerFactories())
                .build();

        final NodeFactory nodeFactory = new NodeFactory(nodeContext);
        return nodeFactory.createRootNode(modelContext.getRootType());
    }

    @Override
    public String toString() {
        return String.format("Model<%s>", rootNode.getType().getTypeName());
    }
}
