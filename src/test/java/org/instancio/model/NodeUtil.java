package org.instancio.model;

import static org.assertj.core.api.Fail.fail;

public class NodeUtil {

    static ClassNode getChildClassNode(Node parent, Class<?> classToMatch) {
        return parent.getChildren().stream()
                .filter(it -> it instanceof ClassNode)
                .map(it -> (ClassNode) it)
                .filter(it -> it.getKlass() == classToMatch)
                .findAny()
                .orElseGet(() -> fail("Expected {} child node", classToMatch.getSimpleName()));
    }

    static FieldNode getChildFieldNode(Node parent, String fieldName) {
        return parent.getChildren().stream()
                .filter(it -> it instanceof FieldNode)
                .map(it -> (FieldNode) it)
                .filter(it -> it.getField().getName().equals(fieldName))
                .findAny()
                .orElseGet(() -> fail("Expected child field node '{}' ", fieldName));
    }
}
