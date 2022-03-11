package org.instancio.model;

import org.instancio.testsupport.tags.ModelTag;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Fail.fail;

@ModelTag
class FieldNode_IntegerStringMap_Test {

    private final NodeContext nodeContext = new NodeContext(Collections.emptyMap());

    @Test
    void mapField() {
//        final FieldNode mapFieldNode = new FieldNode(nodeContext, ReflectionUtils.getField(IntegerStringMap.class, "mapField"));
//
//        assertFieldNode(mapFieldNode)
//                .hasActualFieldType(Map.class)
//                .hasTypeMappedTo(getTypeVar(Map.class, "K"), Integer.class)
//                .hasTypeMappedTo(getTypeVar(Map.class, "V"), String.class)
//                .hasTypeMapWithSize(2)
//                .hasChildrenOfSize(1);
//
//        final MapNode mapNode = mapFieldNode
//                .getChildren().stream()
//                .filter(it -> it instanceof MapNode)
//                .map(it -> (MapNode) it)
//                .findAny()
//                .orElseGet(() -> fail("Expected Integer child node"));

        fail("TODO");
//        assertClassNode(mapNode.getKeyNode())
//                .hasKlass(Integer.class)
//                .hasGenericType(null)
//                .hasParent(mapFieldNode)
//                .hasNoChildren();
//
//        assertClassNode(mapNode.getValueNode())
//                .hasKlass(String.class)
//                .hasGenericType(null)
//                .hasParent(mapFieldNode)
//                .hasNoChildren();

    }

}