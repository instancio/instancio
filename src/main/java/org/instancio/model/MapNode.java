package org.instancio.model;

import org.instancio.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class MapNode extends Node {
    private static final Logger LOG = LoggerFactory.getLogger(MapNode.class);

    private final ClassNode keyNode;
    private final ClassNode valueNode;

    public MapNode(final NodeContext nodeContext,
                   final ClassNode keyNode,
                   final ClassNode valueNode,
                   final Node parent) {

        super(nodeContext, parent);

        this.keyNode = keyNode;
        this.valueNode = valueNode;

        // TODO MapNode children are redundant... verify and remove
        List<Node> children = new ArrayList<>();
        children.addAll(collectChildren(keyNode));
        children.addAll(collectChildren(valueNode));
        setChildren(children);
        // -----
    }

    private List<Node> collectChildren(ClassNode node) {
        Class<?> klass = node.getKlass();

        if (klass.getPackage() == null || klass.getPackage().getName().startsWith(JAVA_PKG_PREFIX)) {
            return Collections.emptyList();
        }

        return Arrays.stream(klass.getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .filter(getNodeContext()::isUnvisited)
                .map(field -> {
                    Type passedOnGenericType = ObjectUtils.defaultIfNull(node.getGenericType(), field.getGenericType());
                    LOG.debug("Passing generic type to child field node: {}", passedOnGenericType);
                    return new FieldNode(getNodeContext(), field, field.getType(), passedOnGenericType, this);
                })
                .collect(toList());
    }

    public ClassNode getKeyNode() {
        return keyNode;
    }

    public ClassNode getValueNode() {
        return valueNode;
    }

    @Override
    public String toString() {
        String s = "";
        s += "MapNode: key class: " + keyNode.getKlass().getSimpleName() + "\n"
                + " -> value class: " + valueNode.getKlass().getSimpleName() + "\n"
                + " -> typeMap: " + getNodeContext().getRootTypeMap() + "\n";
        return s;
    }


}
