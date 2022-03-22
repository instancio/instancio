package org.instancio.internal.model;

import org.instancio.util.ObjectUtils;
import org.instancio.util.Verify;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class Node {
    static final String JAVA_PKG_PREFIX = "java";

    private final NodeContext nodeContext;
    private final Field field;
    private final Class<?> klass;
    private final Type genericType;
    private Node parent;
    private List<Node> children;
    private final Map<Type, Type> typeMap;

    Node(final NodeContext nodeContext,
         final Class<?> klass,
         @Nullable final Field field,
         @Nullable final Type genericType,
         @Nullable final Node parent) {

        this.nodeContext = Verify.notNull(nodeContext, "nodeContext is null");
        this.klass = Verify.notNull(klass, "klass is null");
        this.field = field;
        this.genericType = genericType;
        this.parent = parent;

        final TypeMapResolver typeMapResolver = new TypeMapResolver(
                nodeContext.getRootTypeMap(), ObjectUtils.defaultIfNull(genericType, klass));

        this.typeMap = Collections.unmodifiableMap(typeMapResolver.getTypeMap());
    }

    protected abstract List<Node> collectChildren();

    // FIXME exposed setter to allow overwriting parent field of ElementNode and Key/Value nodes
    //  in this class's constructor. Reason: CollectionNode constructor takes ElementNode
    //  as an argument. Therefore, can't set ElementNode's parent to CollectionNode because
    //  the latter needs to be instantiated first, but it takes the former as an constructor argument...
    void setParent(final Node parent) {
        this.parent = parent;
    }

    public NodeContext getNodeContext() {
        return nodeContext;
    }

    public Field getField() {
        return field;
    }

    // TODO rename to avoid confusion with getClass()
    public Class<?> getKlass() {
        return klass;
    }

    public Type getGenericType() {
        return genericType;
    }

    final Type resolveTypeVariable(TypeVariable<?> typeVariable) {
        Type mappedType = typeMap.get(typeVariable);

        Node ancestor = parent;
        if ((mappedType == null || mappedType instanceof TypeVariable) && ancestor != null) {
            mappedType = ancestor.getTypeMap().get(typeVariable);
        }

        if (mappedType instanceof Class || mappedType instanceof ParameterizedType) {
            return mappedType;
        }

        if (nodeContext.getRootTypeMap().containsKey(mappedType)) {
            return nodeContext.getRootTypeMap().get(mappedType);
        }

        throw new IllegalStateException("Failed resolving type variable: " + typeVariable);
    }


    public Node getParent() {
        return parent;
    }

    public Map<Type, Type> getTypeMap() {
        return typeMap;
    }

    public List<Node> getChildren() {
        if (children == null) {
            children = Collections.unmodifiableList(collectChildren());
        }
        return children;
    }

    protected final Map<TypeVariable<?>, Class<?>> getRootTypeMap() {
        return nodeContext.getRootTypeMap();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node other = (Node) o;

        return this.getKlass().equals(other.getKlass())
                && Objects.equals(this.getGenericType(), other.getGenericType())
                && Objects.equals(this.getField(), other.getField());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKlass(), getGenericType(), getField());
    }

    @Override
    public final String toString() {
        String fieldName = field == null ? "null" : field.getName();
        String numChildren = String.format("[%s]", (children == null ? 0 : children.size()));
        return this.getClass().getSimpleName() + numChildren + "["
                + klass.getSimpleName() + ", " + genericType + ", field: " + fieldName + "]";
    }
}
