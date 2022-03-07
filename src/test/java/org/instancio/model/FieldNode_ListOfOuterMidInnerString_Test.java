package org.instancio.model;

import org.instancio.pojo.generics.outermidinner.Inner;
import org.instancio.pojo.generics.outermidinner.ListOfOuterMidInnerString;
import org.instancio.pojo.generics.outermidinner.Mid;
import org.instancio.pojo.generics.outermidinner.Outer;
import org.instancio.util.ReflectionUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.testsupport.asserts.FieldNodeAssert.assertFieldNode;
import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

class FieldNode_ListOfOuterMidInnerString_Test {

    private final NodeContext nodeContext = new NodeContext(Collections.emptyMap());

    @Test
    void listOfOuter() {
        final String rootField = "listOfOuter";
        final FieldNode listOfOuterFieldNode = new FieldNode(nodeContext, ReflectionUtils.getField(ListOfOuterMidInnerString.class, rootField));

        assertFieldNode(listOfOuterFieldNode)
                .hasFieldName(rootField)
                .hasActualFieldType(List.class)
                .hasTypeMappedTo(getTypeVar(List.class, "E"), Outer.class)
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1);

        final List<ClassNode> listOfOuterFieldNodeClassNodeChildren = listOfOuterFieldNode.getChildren().stream()
                .filter(it -> it instanceof ClassNode)
                .map(it -> (ClassNode) it)
                .collect(toList());

        assertThat(listOfOuterFieldNodeClassNodeChildren).hasSize(1);

        final ClassNode outerClassNode = listOfOuterFieldNodeClassNodeChildren.get(0);

        assertThat(outerClassNode.getKlass()).isEqualTo(Outer.class);
        assertThat(outerClassNode.getGenericType().getTypeName()).isEqualTo(
                "org.instancio.pojo.generics.outermidinner." +
                        "Outer<org.instancio.pojo.generics.outermidinner." +
                        "Mid<org.instancio.pojo.generics.outermidinner." +
                        "Inner<java.lang.String>>>");

        assertThat(outerClassNode.getChildren()).hasSize(1);

        // Node representing 'List<T> outerList' field of Outer.class
        final FieldNode outerListFieldNode = (FieldNode) outerClassNode.getChildren().get(0);


        assertFieldNode(outerListFieldNode)
                .hasFieldName("outerList")
                .hasActualFieldType(List.class)
                .hasTypeMappedTo(getTypeVar(List.class, "E"), Mid.class)
//                .hasTypeMappedTo(getTypeVar(List.class, "E"), getTypeVar(Outer.class, "T"))
//                .hasTypeMappedTo(getTypeVar(Outer.class, "T"), Mid.class)
                .hasTypeMapWithSize(1) // 2
                .hasChildrenOfSize(1);

        final ClassNode midClassNode = (ClassNode) outerListFieldNode.getChildren().get(0);

        assertThat(midClassNode.getKlass()).isEqualTo(Mid.class);
        assertThat(midClassNode.getGenericType().getTypeName()).isEqualTo(
                "org.instancio.pojo.generics.outermidinner.Mid<org.instancio.pojo.generics.outermidinner.Inner<java.lang.String>>");

        assertThat(midClassNode.getChildren()).hasSize(1);

        // Node representing 'List<T> midList' field of Mid.class
        final FieldNode midListFieldNode = (FieldNode) midClassNode.getChildren().get(0);

        assertFieldNode(midListFieldNode)
                .hasFieldName("midList")
                .hasActualFieldType(List.class)
                .hasTypeMappedTo(getTypeVar(List.class, "E"), Inner.class)
                .hasTypeMapWithSize(1) // 2
                .hasChildrenOfSize(1);

        final ClassNode innerClassNode = (ClassNode) midListFieldNode.getChildren().get(0);

        assertThat(innerClassNode.getKlass()).isEqualTo(Inner.class);
        assertThat(innerClassNode.getGenericType().getTypeName()).isEqualTo(
                "org.instancio.pojo.generics.outermidinner.Inner<java.lang.String>");

        assertThat(innerClassNode.getChildren()).hasSize(1);

        // Node representing 'List<T> innerList' field of Inner.class
        final FieldNode innerListFieldNode = (FieldNode) innerClassNode.getChildren().get(0);

        assertFieldNode(innerListFieldNode)
                .hasFieldName("innerList")
                .hasActualFieldType(List.class)
                .hasTypeMappedTo(getTypeVar(List.class, "E"), String.class)
                .hasTypeMapWithSize(1) // 2
                .hasChildrenOfSize(1);

        final ClassNode listElementClassNode = (ClassNode) innerListFieldNode.getChildren().get(0);

        assertThat(listElementClassNode.getKlass()).isEqualTo(String.class);
        assertThat(listElementClassNode.getGenericType()).isNull();
        assertThat(listElementClassNode.getChildren()).isEmpty();
    }
}