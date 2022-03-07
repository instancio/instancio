package org.instancio.model;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class Node {
    static final String JAVA_PKG_PREFIX = "java";

    private final NodeContext nodeContext;
    private final Class<?> klass;
    private final Type genericType;
    private final Node parent;
    private List<Node> children;

    public Node(
            final NodeContext nodeContext,
            final Class<?> klass,
            final Type genericType,
            final Node parent) {

        this.nodeContext = nodeContext;
        this.klass = klass;
        this.genericType = genericType;
        this.parent = parent;
    }

    public NodeContext getNodeContext() {
        return nodeContext;
    }

    public Class<?> getKlass() {
        return klass;
    }

    public Type getGenericType() {
        return genericType;
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

    Optional<Node> getChild(final Predicate<Node> predicate) {
        return getChildren().stream().filter(predicate).findAny();
    }
}
