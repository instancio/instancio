package org.instancio.model;

import org.instancio.pojo.generics.outermidinner.ListOfOuterMidInnerString;
import org.instancio.pojo.generics.outermidinner.Outer;
import org.instancio.util.ReflectionUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.instancio.testsupport.asserts.FieldNodeAssert.assertFieldNode;
import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

class FieldNode_ListOfOuterMidInnerString_Test {

    @Test
    void listOfOuter() {
        final String rootField = "listOfOuter";
        final FieldNode node = new FieldNode(ReflectionUtils.getField(ListOfOuterMidInnerString.class, rootField),
                Collections.emptyMap());

        System.out.println(node);

        assertFieldNode(node)
                .hasFieldName(rootField)
                .hasActualFieldType(List.class)
                .hasTypeMappedTo(getTypeVar(List.class, "E"), Outer.class)
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1);

    }
}