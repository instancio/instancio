package org.instancio;

import experimental.reflection.nodes.FieldNode;

import java.util.StringJoiner;

class CreateItem {

    private final FieldNode fieldNode;
    private final Object owner;

    public CreateItem(FieldNode fieldNode, Object owner) {
        this.fieldNode = fieldNode;
        this.owner = owner;
    }

    public FieldNode getFieldNode() {
        return fieldNode;
    }

    public Object getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CreateItem.class.getSimpleName() + "[", "]")
                .add("fieldNode=" + fieldNode)
                .add("owner=" + owner.getClass().getName())
                .toString();
    }
}
