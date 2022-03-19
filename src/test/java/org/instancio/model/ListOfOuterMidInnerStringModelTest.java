package org.instancio.model;

import org.instancio.pojo.generics.outermidinner.Inner;
import org.instancio.pojo.generics.outermidinner.ListOfOuterMidInnerString;
import org.instancio.pojo.generics.outermidinner.Mid;
import org.instancio.pojo.generics.outermidinner.Outer;
import org.instancio.testsupport.templates.ModelTestTemplate;

import java.util.List;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;
import static org.instancio.testsupport.utils.CollectionUtils.getOnlyElement;

class ListOfOuterMidInnerStringModelTest extends ModelTestTemplate<ListOfOuterMidInnerString> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasParent(null)
                .hasNullField()
                .hasKlass(ListOfOuterMidInnerString.class)
                .hasEmptyTypeMap()
                .hasChildrenOfSize(1);

        //  List<Outer<Mid<Inner<String>>>>
        final Node rootListElement = assertRootListAndItsElement(rootNode);

        // List<T> outerList
        final Node outerElement = assertOuterList(rootListElement);

        // List<T> midList
        final Node midListElement = assertMidList(outerElement);

        // List<T> innerList
        assertInnerList(midListElement);
    }

    private Node assertRootListAndItsElement(Node rootNode) {
        final CollectionNode rootList = assertNode(getOnlyElement(rootNode.getChildren()))
                .hasParent(rootNode)
                .hasFieldName("rootList")
                .hasTypeMappedTo(List.class, "E", "org.instancio.pojo.generics.outermidinner." +
                        "Outer<org.instancio.pojo.generics.outermidinner." +
                        "Mid<org.instancio.pojo.generics.outermidinner." +
                        "Inner<java.lang.String>>>")
                .hasTypeMapWithSize(1)
                .hasEffectiveClass(List.class)
                .getAs(CollectionNode.class);

        // Outer
        assertNode(rootList.getElementNode())
                .hasParent(rootList)
                .hasNullField()
                .hasKlass(Outer.class)
                .hasTypeMappedTo(Outer.class, "T", "org.instancio.pojo.generics.outermidinner." +
                        "Mid<org.instancio.pojo.generics.outermidinner." +
                        "Inner<java.lang.String>>")
                .hasGenericTypeName("org.instancio.pojo.generics.outermidinner." +
                        "Outer<org.instancio.pojo.generics.outermidinner." +
                        "Mid<org.instancio.pojo.generics.outermidinner." +
                        "Inner<java.lang.String>>>")
                .hasChildrenOfSize(1);

        return rootList.getElementNode();
    }

    private Node assertOuterList(Node rootListElement) {
        final CollectionNode outerList = assertNode(getOnlyElement(rootListElement.getChildren()))
                .hasParent(rootListElement)
                .hasFieldName("outerList")
                .hasEffectiveClass(List.class)
                .getAs(CollectionNode.class);

        assertNode(outerList.getElementNode())
                .hasParent(outerList)
                .hasNullField()
                .hasKlass(Mid.class)
                .hasGenericTypeName("org.instancio.pojo.generics.outermidinner." +
                        "Mid<org.instancio.pojo.generics.outermidinner." +
                        "Inner<java.lang.String>>")
                .hasTypeMappedTo(Mid.class, "T", "org.instancio.pojo.generics.outermidinner." +
                        "Inner<java.lang.String>")
                .hasChildrenOfSize(1);

        return outerList.getElementNode();
    }

    private Node assertMidList(Node outerElement) {
        final CollectionNode midList = assertNode(getOnlyElement(outerElement.getChildren()))
                .hasParent(outerElement)
                .hasFieldName("midList")
                .hasKlass(List.class)
                .hasNoChildren()
                .getAs(CollectionNode.class);

        assertNode(midList.getElementNode())
                .hasParent(midList)
                .hasNullField()
                .hasKlass(Inner.class)
                .hasTypeMappedTo(Inner.class, "T", String.class)
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1);

        return midList.getElementNode();
    }

    private void assertInnerList(Node midListElement) {
        final CollectionNode innerList = assertNode(getOnlyElement(midListElement.getChildren()))
                .hasParent(midListElement)
                .hasFieldName("innerList")
                .hasKlass(List.class)
                .hasNoChildren()
                .getAs(CollectionNode.class);

        assertNode(innerList.getElementNode())
                .hasParent(innerList)
                .hasNullField()
                .hasKlass(String.class)
                .hasEmptyTypeMap()
                .hasNoChildren();
    }
}