package org.instancio;

import org.instancio.model.Node;

import java.util.StringJoiner;

class CreateItem {

    private final Node node;
    private final Object owner;

    public CreateItem(Node node, Object owner) {
        this.node = node;
        this.owner = owner;
    }

    public Node getNode() {
        return node;
    }

    public Object getOwner() {
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
