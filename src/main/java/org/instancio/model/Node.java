package org.instancio.model;

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Node {
    static final String JAVA_PKG_PREFIX = "java";

    private final NodeContext nodeContext;
    private final Node parent;
    private List<Node> children;

    // TODO delete.. tmp method
    abstract List<Node> collectChildren();

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
        if (children == null) {
            children = new ArrayList<>();
            final List<Node> collected = collectChildren();
            for (Node child : collected) {
                if (nodeContext.isUnvisited(child)) {
                    children.add(child);
                    nodeContext.visited(child);
                }
            }
            this.children = collected;
        }
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    protected final Map<TypeVariable<?>, Class<?>> getRootTypeMap() {
        return this.nodeContext.getRootTypeMap();
    }

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("Requires override");
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("Requires override");
    }

    // TODO delete after testing
    public void print() {
        System.out.println("-----------------------------------------");
        System.out.println(this);
        System.out.println(" ----> num children: " + getChildren().size());
        getChildren().forEach(Node::print);
    }

}
