package org.instancio.model;

import org.instancio.pojo.generics.container.GenericContainer;
import org.instancio.testsupport.templates.ModelTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import java.util.List;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class GenericContainerStringModelTest extends ModelTestTemplate<GenericContainer<String>> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(GenericContainer.class)
                .hasChildrenOfSize(3);

        // T value
        assertNode(NodeUtils.getChildNode(rootNode, "value"))
                .hasParent(rootNode)
                .hasFieldName("value")
                .hasKlass(Object.class)
                .hasEffectiveClass(String.class)
                .hasTypeMappedTo(GenericContainer.class, "T", String.class)
                .hasTypeMapWithSize(1)
                .hasNoChildren();

        // T[] array
        final ArrayNode array = assertNode(NodeUtils.getChildNode(rootNode, "array"))
                .hasParent(rootNode)
                .hasFieldName("array")
                .hasKlass(Object[].class)
                .hasEffectiveClass(Object[].class) // TODO
                .hasEmptyTypeMap()  // TODO
                .hasNoChildren()
                .getAs(ArrayNode.class);

        assertNode(array.getElementNode())
                .hasParent(rootNode)
                .hasNullField()
                .hasKlass(String.class)
                .hasEffectiveClass(String.class)
                .hasEmptyTypeMap()
                .hasNoChildren();

        // List<T> list
        final CollectionNode list = assertNode(NodeUtils.getChildNode(rootNode, "list"))
                .hasParent(rootNode)
                .hasFieldName("list")
                .hasKlass(List.class)
                .hasEffectiveClass(List.class)
                .hasTypeMappedTo(List.class, "E", String.class)
                .hasTypeMapWithSize(1)
                .hasNoChildren()
                .getAs(CollectionNode.class);

        assertNode(list.getElementNode())
                .hasParent(rootNode)
                .hasNullField()
                .hasKlass(String.class)
                .hasEffectiveClass(String.class)
                .hasEmptyTypeMap()
                .hasNoChildren();
    }
}