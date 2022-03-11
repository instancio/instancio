package org.instancio.model;

import org.instancio.pojo.generics.outermidinner.ListOfOuterMidInnerString;
import org.instancio.pojo.generics.outermidinner.Mid;
import org.instancio.pojo.generics.outermidinner.Outer;
import org.instancio.testsupport.tags.ModelTag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

@ModelTag
class FieldNode_ListOfOuterMidInnerString_Test {

    private final NodeContext nodeContext = new NodeContext(Collections.emptyMap());

    // TODO cleanup
    @Test
    void rootList() {

        NodeFactory nodeFactory = new NodeFactory();

        final String rootField = "rootList";

        Node rootNode = nodeFactory.createNode(nodeContext, ListOfOuterMidInnerString.class, null, null, null);

        // List<Outer<Mid<Inner<String>>>> rootList
        assertNode(rootNode)
                .hasParent(null)
                //.hasFieldName(rootField)
                //.hasTypeMappedTo(getTypeVar(List.class, "E"), Outer.class)
                //.hasTypeMapWithSize(1)
                .hasChildrenOfSize(1);

        // list: List<Outer<Mid<Inner<String>>>> rootList
        final CollectionNode rootListCollectionNode = (CollectionNode) rootNode.getChildren().get(0);
        assertNode(rootListCollectionNode)
                .hasParent(rootNode)
                .hasFieldName("rootList")
                .hasEffectiveClass(List.class);

        // element: Outer
        final Node rootListElementNode = rootListCollectionNode.getElementNode();

        assertNode(rootListElementNode)
                .hasParent(rootNode)    // XXX correct parent?
                .hasKlass(Outer.class)
                .hasGenericTypeName("org.instancio.pojo.generics.outermidinner." +
                        "Outer<org.instancio.pojo.generics.outermidinner." +
                        "Mid<org.instancio.pojo.generics.outermidinner." +
                        "Inner<java.lang.String>>>")
                .hasChildrenOfSize(1);


        // class Outer { List<T> outerList }
        final CollectionNode outerListCollectionNode = (CollectionNode) rootListElementNode.getChildren().get(0);
        assertNode(outerListCollectionNode)
                /// .hasParent(rootListElementNode) FIXME parent is null
                .hasFieldName("outerList")
                .hasEffectiveClass(List.class);

        // Mid
        final Node midClassNode = outerListCollectionNode.getElementNode();

        assertNode(midClassNode)
                .hasParent(rootListElementNode)
                .hasKlass(Mid.class)
                // TODO should have a generic type!
//                .hasGenericTypeName("org.instancio.pojo.generics.outermidinner." +
//                        "Mid<org.instancio.pojo.generics.outermidinner." +
//                        "Inner<java.lang.String>>")
                .hasChildrenOfSize(1);

        // Node representing 'List<T> midList' field of Mid.class
        final CollectionNode midListCollectionNode = (CollectionNode) midClassNode.getChildren().get(0);

        assertNode(midListCollectionNode)
                .hasParent(midClassNode)
                .hasKlass(List.class)
                .hasNoChildren();

        final Node midListElementNode = midListCollectionNode.getElementNode();

        assertNode(midListElementNode)
                .hasParent(midClassNode)
                .hasFieldName("midList")
//                .hasActualFieldType(List.class)
//                .hasTypeMappedTo(getTypeVar(Mid.class, "T"), Inner.class)
//                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1);

        final CollectionNode innerListCollectionNode = (CollectionNode) midListElementNode.getChildren().get(0);

        assertNode(innerListCollectionNode)
                .hasParent(midListElementNode)
                .hasKlass(List.class)
                .hasNoChildren();

        final Node innerListElementNode = innerListCollectionNode.getElementNode();

        assertNode(innerListElementNode)
                .hasParent(midListElementNode)
                .hasKlass(String.class)
                .hasNoChildren();
//
//        // Node representing 'List<T> innerList' field of Inner.class
//        final FieldNode innerListFieldNode = (FieldNode) innerClassNode.getChildren().get(0);
//
//        assertFieldNode(innerListFieldNode)
//                .hasParent(innerClassNode)
//                .hasFieldName("innerList")
//                .hasActualFieldType(List.class)
//                .hasTypeMappedTo(getTypeVar(Inner.class, "T"), String.class)
//                .hasTypeMapWithSize(1)
//                .hasChildrenOfSize(1);
//
//        final ClassNode listElementClassNode = (ClassNode) innerListFieldNode.getChildren().get(0);
//
//        assertNode(listElementClassNode)
//                .hasParent(innerListFieldNode)
//                .hasKlass(String.class)
//                .hasGenericType(null)
//                .hasNoChildren();
    }
}