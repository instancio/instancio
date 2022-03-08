package org.instancio.model;

import org.instancio.pojo.maps.IntegerStringMap;
import org.instancio.testsupport.tags.ModelTag;
import org.instancio.util.ReflectionUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Fail.fail;
import static org.instancio.testsupport.asserts.ClassNodeAssert.assertClassNode;
import static org.instancio.testsupport.asserts.FieldNodeAssert.assertFieldNode;
import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

@ModelTag
class FieldNode_IntegerStringMap_Test {

    private final NodeContext nodeContext = new NodeContext(Collections.emptyMap());

    @Test
    void mapField() {
        final FieldNode mapFieldNode = new FieldNode(nodeContext, ReflectionUtils.getField(IntegerStringMap.class, "mapField"));

        assertFieldNode(mapFieldNode)
                .hasActualFieldType(Map.class)
                .hasTypeMappedTo(getTypeVar(Map.class, "K"), Integer.class)
                .hasTypeMappedTo(getTypeVar(Map.class, "V"), String.class)
                .hasTypeMapWithSize(2)
                .hasChildrenOfSize(2);

        final ClassNode integerClassNode = (ClassNode) mapFieldNode
                .getChildren().stream()
                .filter(it -> it instanceof ClassNode)
                .map(it -> (ClassNode) it)
                .filter(it -> it.getKlass() == Integer.class)
                .findAny()
                .orElseGet(() -> fail("Expected Integer child node"));

        assertClassNode(integerClassNode)
                .hasKlass(Integer.class)
                .hasGenericType(null)
                .hasParent(mapFieldNode)
                .hasNoChildren();

        final ClassNode stringClassNode = NodeUtil.getChildClassNode(mapFieldNode, String.class);

        assertClassNode(stringClassNode)
                .hasKlass(String.class)
                .hasGenericType(null)
                .hasParent(mapFieldNode)
                .hasNoChildren();

    }

}