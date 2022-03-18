package org.instancio.model;

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
    private final Node parent;
    private List<Node> children;
    private final Map<Type, Type> typeMap;
    private final GenericType<?> effectiveType;

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
        this.effectiveType = initEffectiveType();
    }

    protected abstract List<Node> collectChildren();

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

    public GenericType getEffectiveType() {
        return effectiveType;
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

    // TODO review and clean up
    private GenericType initEffectiveType() {
        if (genericType == null || (field != null && field.getGenericType() instanceof Class))
            return GenericType.of(klass);

        if (klass != Object.class)
            return GenericType.of(klass, genericType);

        if (genericType instanceof TypeVariable) {
            Type mappedType = typeMap.getOrDefault(genericType, genericType);

            Node ancestor = parent;
            while ((mappedType == null || !nodeContext.getRootTypeMap().containsKey(mappedType)) && ancestor != null) {
                mappedType = ancestor.getTypeMap().getOrDefault(mappedType, mappedType);

                if (mappedType instanceof Class || mappedType instanceof ParameterizedType)
                    break;

                ancestor = ancestor.getParent();
            }

            if (mappedType instanceof Class) {
                return GenericType.of((Class<?>) mappedType, mappedType);
            }
            if (nodeContext.getRootTypeMap().containsKey(mappedType)) {
                return GenericType.of(nodeContext.getRootTypeMap().get(mappedType), mappedType);
            }
        } else if (genericType instanceof ParameterizedType) {

            if (field != null) {
                final Type fieldGenericType = field.getGenericType();
                final Type mappedType = typeMap.getOrDefault(fieldGenericType, fieldGenericType);
                if (mappedType instanceof Class) {
                    return GenericType.of((Class<?>) mappedType /*pass generic type? */);
                }
                if (getRootTypeMap().containsKey(mappedType)) {

                    return GenericType.of(getRootTypeMap().get(mappedType) /* pass generic type?? */);
                }
            }
        } else if (genericType instanceof Class) {
            return GenericType.of((Class<?>) genericType, null);
        }

        throw new IllegalStateException("Unknown effective class for node: " + this);
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
                + getEffectiveType() + ", field: " + fieldName + "]";
    }
}
