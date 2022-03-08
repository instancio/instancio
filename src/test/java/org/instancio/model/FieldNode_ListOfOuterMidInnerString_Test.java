package org.instancio.model;

import org.instancio.pojo.generics.outermidinner.Inner;
import org.instancio.pojo.generics.outermidinner.ListOfOuterMidInnerString;
import org.instancio.pojo.generics.outermidinner.Mid;
import org.instancio.pojo.generics.outermidinner.Outer;
import org.instancio.testsupport.tags.ModelTag;
import org.instancio.util.ReflectionUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.instancio.testsupport.asserts.ClassNodeAssert.assertClassNode;
import static org.instancio.testsupport.asserts.FieldNodeAssert.assertFieldNode;
import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

@ModelTag
class FieldNode_ListOfOuterMidInnerString_Test {

    private final NodeContext nodeContext = new NodeContext(Collections.emptyMap());

    @Test
    void listOfOuter() {
        final String rootField = "listOfOuter";
        final FieldNode listOfOuterFieldNode = new FieldNode(nodeContext, ReflectionUtils.getField(ListOfOuterMidInnerString.class, rootField));

        assertFieldNode(listOfOuterFieldNode)
                .hasParent(null)
                .hasFieldName(rootField)
                .hasActualFieldType(List.class)
                .hasTypeMappedTo(getTypeVar(List.class, "E"), Outer.class)
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1);

        final ClassNode outerClassNode = NodeUtil.getChildClassNode(listOfOuterFieldNode, Outer.class);

        assertClassNode(outerClassNode)
                .hasParent(listOfOuterFieldNode)
                .hasKlass(Outer.class)
                .hasGenericTypeName("org.instancio.pojo.generics.outermidinner." +
                        "Outer<org.instancio.pojo.generics.outermidinner." +
                        "Mid<org.instancio.pojo.generics.outermidinner." +
                        "Inner<java.lang.String>>>")
                .hasChildrenOfSize(1);

        // Node representing 'List<T> outerList' field of Outer.class
        final FieldNode outerListFieldNode = (FieldNode) outerClassNode.getChildren().get(0);

        assertFieldNode(outerListFieldNode)
                .hasParent(outerClassNode)
                .hasFieldName("outerList")
                .hasActualFieldType(List.class)
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1);

        final ClassNode midClassNode = (ClassNode) outerListFieldNode.getChildren().get(0);

        assertClassNode(midClassNode)
                .hasParent(outerListFieldNode)
                .hasKlass(Mid.class)
                .hasGenericTypeName("org.instancio.pojo.generics.outermidinner." +
                        "Mid<org.instancio.pojo.generics.outermidinner." +
                        "Inner<java.lang.String>>")
                .hasChildrenOfSize(1);

        // Node representing 'List<T> midList' field of Mid.class
        final FieldNode midListFieldNode = (FieldNode) midClassNode.getChildren().get(0);

        assertFieldNode(midListFieldNode)
                .hasParent(midClassNode)
                .hasFieldName("midList")
                .hasActualFieldType(List.class)
                .hasTypeMappedTo(getTypeVar(Mid.class, "T"), Inner.class)
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1);

        final ClassNode innerClassNode = (ClassNode) midListFieldNode.getChildren().get(0);

        assertClassNode(innerClassNode)
                .hasParent(midListFieldNode)
                .hasKlass(Inner.class)
                .hasGenericTypeName("org.instancio.pojo.generics.outermidinner.Inner<java.lang.String>")
                .hasChildrenOfSize(1);

        // Node representing 'List<T> innerList' field of Inner.class
        final FieldNode innerListFieldNode = (FieldNode) innerClassNode.getChildren().get(0);

        assertFieldNode(innerListFieldNode)
                .hasParent(innerClassNode)
                .hasFieldName("innerList")
                .hasActualFieldType(List.class)
                .hasTypeMappedTo(getTypeVar(Inner.class, "T"), String.class)
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1);

        final ClassNode listElementClassNode = (ClassNode) innerListFieldNode.getChildren().get(0);

        assertClassNode(listElementClassNode)
                .hasParent(innerListFieldNode)
                .hasKlass(String.class)
                .hasGenericType(null)
                .hasNoChildren();
    }
}