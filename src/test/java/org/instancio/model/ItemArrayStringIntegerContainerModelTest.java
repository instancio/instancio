package org.instancio.model;

import org.instancio.pojo.generics.basic.Item;
import org.instancio.pojo.generics.container.ItemArrayContainer;
import org.instancio.testsupport.templates.ModelTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class ItemArrayStringIntegerContainerModelTest extends ModelTestTemplate<ItemArrayContainer<String, Integer>> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(ItemArrayContainer.class)
                .hasChildrenOfSize(2);

        // Item<X>[] itemArrayX
        assertItemArrayX(rootNode);

        // Item<Y>[] itemArrayX
        assertItemArrayY(rootNode);
    }

    private void assertItemArrayX(Node rootNode) {
        final String itemArrayField = "itemArrayX";
        final ArrayNode array = assertNode(NodeUtils.getChildNode(rootNode, itemArrayField))
                .hasFieldName(itemArrayField)
                .hasKlass(Item[].class)
                .hasGenericTypeName("org.instancio.pojo.generics.basic.Item<X>[]")
                .hasNoChildren()
                .hasEmptyTypeMap()
                .getAs(ArrayNode.class);

        assertElementNode(array.getElementNode(), rootNode, "X");
    }

    private void assertItemArrayY(Node rootNode) {
        final String itemArrayField = "itemArrayY";
        final ArrayNode array = assertNode(NodeUtils.getChildNode(rootNode, itemArrayField))
                .hasFieldName(itemArrayField)
                .hasKlass(Item[].class)
                .hasGenericTypeName("org.instancio.pojo.generics.basic.Item<Y>[]")
                .hasNoChildren()
                .hasEmptyTypeMap()
                .getAs(ArrayNode.class);

        assertElementNode(array.getElementNode(), rootNode, "Y");
    }

    private void assertElementNode(Node elementNode, Node expectedParent, String expectedType) {
        assertNode(elementNode)
                .hasKlass(Item.class)
                .hasNullField()
                .hasParent(expectedParent)
                .hasTypeMappedTo(Item.class, "K", expectedType)
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1);
    }
}