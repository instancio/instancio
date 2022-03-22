package org.instancio.testsupport.utils;

import org.instancio.internal.model.Node;

import static org.assertj.core.api.Fail.fail;

public class NodeUtils {

    private NodeUtils() {
        // non-instantiable
    }

    public static Node getChildNode(Node parent, String fieldName) {
        return parent.getChildren().stream()
                .filter(it -> it.getField() != null && it.getField().getName().equals(fieldName))
                .findAny()
                .orElseGet(() -> fail("Expected child field node '%s' ", fieldName));
    }
}
