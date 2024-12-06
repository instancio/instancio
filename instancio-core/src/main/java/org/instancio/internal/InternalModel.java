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

import org.instancio.Model;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeFactory;

public final class InternalModel<T> implements Model<T> {

    private final ModelContext modelContext;
    private final InternalNode rootNode;

    InternalModel(ModelContext modelContext) {
        this.modelContext = modelContext;
        this.rootNode = createRootNode();
    }

    public ModelContext getModelContext() {
        return modelContext;
    }

    InternalNode getRootNode() {
        return rootNode;
    }

    private InternalNode createRootNode() {
        final NodeFactory nodeFactory = new NodeFactory(modelContext);
        return nodeFactory.createRootNode(modelContext.getRootType().getType());
    }

    @Override
    public String toString() {
        return String.format("Model<%s>", rootNode.getType().getTypeName());
    }
}
