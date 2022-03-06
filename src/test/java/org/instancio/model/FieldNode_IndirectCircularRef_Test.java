package org.instancio.model;

import org.instancio.pojo.circular.IndirectCircularRef;
import org.instancio.util.ReflectionUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.instancio.testsupport.asserts.FieldNodeAssert.assertFieldNode;

class FieldNode_IndirectCircularRef_Test {

    private final NodeContext nodeContext = new NodeContext(Collections.emptyMap());

    @Test
    void b() {
        final String rootField = "b";
        final FieldNode bFieldNode = new FieldNode(nodeContext, ReflectionUtils.getField(IndirectCircularRef.A.class, rootField));

        assertFieldNode(bFieldNode)
                .hasFieldName(rootField)
                .hasActualFieldType(IndirectCircularRef.B.class)
                .hasEmptyTypeMap()
                .hasChildrenOfSize(1);

        final FieldNode cFieldNode = bFieldNode.getChildByFieldName("c");
        assertFieldNode(cFieldNode)
                .hasFieldName("c")
                .hasActualFieldType(IndirectCircularRef.C.class)
                .hasEmptyTypeMap()
                .hasChildrenOfSize(1);

        final FieldNode aFieldNode = cFieldNode.getChildByFieldName("a");
        assertFieldNode(aFieldNode)
                .hasFieldName("a")
                .hasActualFieldType(IndirectCircularRef.A.class)
                .hasEmptyTypeMap()
                .hasChildrenOfSize(1);

        final FieldNode cyclicBFieldNode = aFieldNode.getChildByFieldName(rootField);
        assertFieldNode(cyclicBFieldNode)
                .hasFieldName(rootField) // back to root field
                .hasActualFieldType(IndirectCircularRef.B.class)
                .hasEmptyTypeMap()
                .hasNoChildren();  // but no children this time
    }
}