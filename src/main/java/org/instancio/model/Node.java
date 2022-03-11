package org.instancio.model;

import org.instancio.util.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

public abstract class Node {
    private static final Logger LOG = LoggerFactory.getLogger(Node.class);

    static final String JAVA_PKG_PREFIX = "java";

    private final NodeContext nodeContext;
    private final Field field;
    private final Class<?> klass;
    private final Type genericType;
    private final Node parent;
    private List<Node> children;
    private final Map<TypeVariable<?>, Type> typeMap;

    Node(final NodeContext nodeContext,
         @Nullable final Field field,
         final Class<?> klass,
         @Nullable final Type genericType,
         @Nullable final Node parent) {

        this.nodeContext = Verify.notNull(nodeContext, "nodeContext is null");
        this.field = field;
        this.klass = Verify.notNull(klass, "klass is null");
        this.genericType = genericType;
        this.parent = parent;
        this.typeMap = initTypeMap(klass, genericType);
    }

    abstract List<Node> collectChildren();

    abstract String getNodeName(); // TODO delete

    public NodeContext getNodeContext() {
        return nodeContext;
    }

    public Field getField() {
        return field;
    }

    public Class<?> getKlass() {
        return klass;
    }

    public Type getGenericType() {
        return genericType;
    }

    public GenericType getEffectiveType() {
        if (genericType == null || (field != null && field.getGenericType() instanceof Class))
            return GenericType.of(klass);

        if (klass != Object.class)
            return GenericType.of(klass, genericType);

        if (genericType instanceof TypeVariable) {
            Type mappedType = typeMap.getOrDefault(genericType, genericType);

            if (mappedType instanceof Class) {
                return GenericType.of((Class<?>) mappedType, mappedType);
            }
            if (nodeContext.getRootTypeMap().containsKey(mappedType)) {
                return GenericType.of(nodeContext.getRootTypeMap().get(mappedType), mappedType);
            }
        } else if (genericType instanceof ParameterizedType) {
            final ParameterizedType pType = (ParameterizedType) genericType;
            final Type actualTypeArgument = pType.getActualTypeArguments()[0]; // XXX hardcoded

            if (actualTypeArgument instanceof Class) {
                return GenericType.of((Class<?>) actualTypeArgument);
            }
            if (actualTypeArgument instanceof TypeVariable) {
                final Class<?> rawType = getRootTypeMap().get(actualTypeArgument);
                return GenericType.of(rawType);
            }
            if (actualTypeArgument instanceof ParameterizedType) {
                final ParameterizedType nestedPType = (ParameterizedType) actualTypeArgument;
                final Type rawType = nestedPType.getRawType();
                return GenericType.of((Class<?>) rawType, actualTypeArgument);
            }

        } else if (genericType instanceof Class) {
            return GenericType.of((Class<?>) genericType, null);
        }

        throw new IllegalStateException("Unknown effective class for node: " + this);
    }

    public Node getParent() {
        return parent;
    }

    public Map<TypeVariable<?>, Type> getTypeMap() {
        return typeMap;
    }

    public List<Node> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
            List<Node> collected = collectChildren();

//            for (Node child : collected) {
//                if (nodeContext.isUnvisited(child)) {
//                    children.add(child);
//                    nodeContext.visited(child);
//                }
//            }
            // FIXME
            children = Collections.unmodifiableList(collected);
        }
        return children;
    }

    protected final Map<TypeVariable<?>, Class<?>> getRootTypeMap() {
        return nodeContext.getRootTypeMap();
    }

    private Map<TypeVariable<?>, Type> initTypeMap(Class<?> klass, @Nullable final Type genericType) {

        // FIXME test hack
        if (genericType instanceof ParameterizedType) {
            klass = (Class<?>) ((ParameterizedType) genericType).getRawType();
        }

        final Map<TypeVariable<?>, Type> map = new HashMap<>();
        final TypeVariable<?>[] typeVars = klass.getTypeParameters();

        if (genericType instanceof ParameterizedType) {

            ParameterizedType pType = (ParameterizedType) genericType;
            Type[] typeArgs = pType.getActualTypeArguments();

//            LOG.debug("pType: {}", pType);
//            LOG.debug("actualTypeArguments: {}", Arrays.toString(typeArgs));

            for (int i = 0; i < typeArgs.length; i++) {
                TypeVariable<?> tvar = typeVars[i];
                Type actualType = typeArgs[i];

                //LOG.debug(" --> tvar: {}, actualType: {}", tvar, actualType);
                if (actualType instanceof TypeVariable) {

                    map.put(tvar, actualType);

                    if (getRootTypeMap().containsKey(actualType)) {
                        map.put((TypeVariable<?>) actualType, getRootTypeMap().get(actualType));
                    }
                } else if (actualType instanceof ParameterizedType) {
                    Class<?> c = (Class<?>) ((ParameterizedType) actualType).getRawType();
                    ParameterizedType nestedPType = (ParameterizedType) actualType;

                    //map.put(tvar, c);
                    map.put(tvar, actualType);

                } else if (actualType instanceof Class) {
                    map.put(tvar, actualType);
                } else {
                    throw new IllegalStateException("Unhandled type: " + actualType);
                }
            }
        } else {
            LOG.debug("No generic info for: {}, genericType: {}", klass, genericType);
        }

        return map;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node other = (Node) o;
        return this.getKlass().equals(other.getKlass())
                && Objects.equals(this.getGenericType(), other.getGenericType())
                && Objects.equals(this.getParent(), other.getParent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKlass(), getGenericType(), getParent());
    }

    @Override
    public final String toString() {
        String s = "[" + this.getClass().getSimpleName() + ": " + getKlass().getSimpleName() + "\n";
        s += " -> effectiveType: " + getEffectiveType() + "\n"
                + " -> field: " + getField() + "\n"
                + " -> typeMap: " + getTypeMap() + "\n";

        if (getChildren() != null)
            s += " -> children (effective class): { " + getChildren().stream()
                    .map(it -> it.getEffectiveType().getRawType().getSimpleName())
                    .collect(joining(", ")) + " }\n";

        s += "]";
        return s;
    }

    // TODO delete after testing
    public void print() {
        System.out.println("-----------------------------------------");
        System.out.println(this);
        System.out.println(" ----> num children: " + getChildren().size());
        getChildren().forEach(Node::print);
    }

}
