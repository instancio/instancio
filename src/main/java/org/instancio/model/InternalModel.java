package org.instancio.model;

import org.instancio.Model;

public class InternalModel<T> implements Model<T> {

    private final ModelContext<T> modelContext;
    private final Node rootNode;

    public InternalModel(ModelContext<T> modelContext) {
        this.modelContext = modelContext;
        this.rootNode = createRootNode();
    }

    public ModelContext<T> getModelContext() {
        return modelContext;
    }

    public Node getRootNode() {
        return rootNode;
    }

    private Node createRootNode() {
        final NodeFactory nodeFactory = new NodeFactory();
        final NodeContext nodeContext = new NodeContext(modelContext.getRootTypeMap());
        return nodeFactory.createRootNode(nodeContext,
                modelContext.getRootClass(), modelContext.getRootType());
    }
}
