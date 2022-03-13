package org.instancio.model;

import org.instancio.util.Verify;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CollectionNode extends Node {

    private final Node elementNode;

    public CollectionNode(final NodeContext nodeContext,
                          @Nullable final Field field,
                          final Class<?> klass,
                          @Nullable final Type genericType,
                          final Node elementNode,
                          @Nullable final Node parent) {

        super(nodeContext, field, klass, genericType, parent);

        Verify.isTrue(Collection.class.isAssignableFrom(klass), "Not a collection type: %s", klass.getName());

        this.elementNode = elementNode;
    }

    /**
     * Returns an empty list; children come from the {@link #getElementNode()}.
     */
    @Override
    List<Node> collectChildren() {
        return Collections.emptyList();
    }

    public Node getElementNode() {
        return elementNode;
    }

    @Override
    public String getNodeName() {
        return String.format("CollectionNode[%s, %s]", getKlass().getSimpleName(), elementNode.getNodeName());
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        CollectionNode other = (CollectionNode) o;
        return Objects.equals(this.getKlass(), other.getKlass())
                && Objects.equals(this.getElementNode(), other.getElementNode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKlass(), getElementNode());
    }
}
