package org.instancio.model;

import org.instancio.util.Verify;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapNode extends Node {

    private final Node keyNode;
    private final Node valueNode;

    public MapNode(final NodeContext nodeContext,
                   @Nullable final Field field,
                   final Class<?> klass,
                   @Nullable final Type genericType,
                   final Node keyNode,
                   final Node valueNode,
                   @Nullable final Node parent) {

        super(nodeContext, field, klass, genericType, parent);

        Verify.isTrue(Map.class.isAssignableFrom(klass), "Not a map type: %s ", klass.getName());

        this.keyNode = Verify.notNull(keyNode, "keyNode is null");
        this.valueNode = Verify.notNull(valueNode, "valueNode is null");
    }

    /**
     * Children come from the {@link #getKeyNode()} and {@link #getValueNode()}.
     */
    @Override
    List<Node> collectChildren() {
        return Collections.emptyList();
    }

    public Node getKeyNode() {
        return keyNode;
    }

    public Node getValueNode() {
        return valueNode;
    }

    @Override
    public String getNodeName() {
        return String.format("MapNode[%s, %s]", keyNode.getNodeName(), valueNode.getNodeName());
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        MapNode other = (MapNode) o;
        return Objects.equals(this.getKeyNode(), other.getKeyNode())
                && Objects.equals(this.getValueNode(), other.getValueNode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKeyNode(), getValueNode());
    }

}
