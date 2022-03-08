package org.instancio.model;

import org.instancio.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

// ClassNode can only have type variables
public class ClassNode extends BaseNode {
    private static final Logger LOG = LoggerFactory.getLogger(ClassNode.class);

    public ClassNode(final NodeContext nodeContext,
                     final Class<?> klass,
                     final Type genericType,
                     final Node parent) {

        super(nodeContext, klass, genericType, parent);
    }

    @Override
    List<Node> collectChildren() {
        if (getKlass().getPackage() == null || getKlass().getPackage().getName().startsWith(JAVA_PKG_PREFIX)) {
            return Collections.emptyList();
        } else {
            return makeChildren(getNodeContext(), getKlass(), getGenericType());
        }
    }

    private List<Node> makeChildren(NodeContext nodeContext, Class<?> klass, Type genericType) {
        return Arrays.stream(klass.getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .map(field -> {
                    Type passedOnGenericType = ObjectUtils.defaultIfNull(genericType, field.getGenericType());
                    LOG.debug("Passing generic type to child field node: {}", passedOnGenericType);
                    return new FieldNode(nodeContext, field, field.getType(), passedOnGenericType, this);
                })
                .filter(it -> getNodeContext().isUnvisited(it))
                .collect(toList());
    }

    public static ClassNode createRootNode(final NodeContext nodeContext, final Class<?> klass) {
        return new ClassNode(nodeContext, klass, null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        ClassNode other = (ClassNode) o;
        return Objects.equals(this.getKlass(), other.getKlass())
                && Objects.equals(this.getGenericType(), other.getGenericType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKlass(), getGenericType());
    }

    @Override
    public String toString() {
        String s = "";
        s += "ClassNode: " + getKlass().getSimpleName() + "\n"
                + " -> typeMap: " + getNodeContext().getRootTypeMap() + "\n";
        return s;
    }


}
