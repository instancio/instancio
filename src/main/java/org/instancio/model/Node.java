package org.instancio.model;

import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;

public abstract class Node {
    static final String JAVA_PKG_PREFIX = "java";

    private final NodeContext nodeContext;
    private final Node parent;
    private List<Node> children;

    public Node(final NodeContext nodeContext, final Node parent) {
        this.nodeContext = nodeContext;
        this.parent = parent;
    }

    public NodeContext getNodeContext() {
        return nodeContext;
    }

    public Node getParent() {
        return parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    protected final Map<TypeVariable<?>, Class<?>> getRootTypeMap() {
        return this.nodeContext.getRootTypeMap();
    }

    // TODO delete after testing
    public void print() {
        System.out.println("-----------------------------------------");
        System.out.println(this);
        System.out.println(" ----> num children: " + getChildren().size());
        getChildren().forEach(Node::print);
    }

}
