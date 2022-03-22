package org.instancio.internal;

import org.instancio.internal.model.Node;

import java.util.StringJoiner;

class CreateItem {

    private final Node node;
    private final Object owner;

    CreateItem(final Node node, final Object owner) {
        this.node = node;
        this.owner = owner;
    }

    Node getNode() {
        return node;
    }

    Object getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CreateItem.class.getSimpleName() + "[", "]")
                .add(node.toString())
                .add("owner=" + owner.getClass().getSimpleName())
                .toString();
    }
}
