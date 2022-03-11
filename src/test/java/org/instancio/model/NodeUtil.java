package org.instancio.model;

import static org.assertj.core.api.Fail.fail;

public class NodeUtil {

    // TODO delete
    static ClassNode getChildClassNode(Node parent, Class<?> classToMatch) {
        return parent.getChildren().stream()
                .filter(it -> it instanceof ClassNode)
                .map(it -> (ClassNode) it)
                .filter(it -> it.getKlass() == classToMatch)
                .findAny()
                .orElseGet(() -> fail("Expected %s child node", classToMatch.getSimpleName()));
    }

    static Node getChildNode(Node parent, String fieldName) {
        return parent.getChildren().stream()
                .filter(it -> it.getField() != null && it.getField().getName().equals(fieldName))
                .findAny()
                .orElseGet(() -> fail("Expected child field node '%s' ", fieldName));
    }
}
