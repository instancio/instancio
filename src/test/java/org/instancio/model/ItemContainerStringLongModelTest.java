package org.instancio.model;

import org.instancio.internal.model.Node;
import org.instancio.pojo.generics.basic.Item;
import org.instancio.pojo.generics.container.ItemContainer;
import org.instancio.testsupport.templates.ModelTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;
import static org.instancio.testsupport.utils.CollectionUtils.getOnlyElement;

class ItemContainerStringLongModelTest extends ModelTestTemplate<ItemContainer<String, Long>> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(ItemContainer.class)
                .hasChildrenOfSize(2);

        final String itemValueXField = "itemValueX";
        final Node itemValueX = assertNode(NodeUtils.getChildNode(rootNode, itemValueXField))
                .hasParent(rootNode)
                .hasFieldName(itemValueXField)
                .hasKlass(Item.class)
                .hasTypeMappedTo(Item.class, "K", "X")
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1)
                .getAs(Node.class);

        assertNode(getOnlyElement(itemValueX.getChildren()))
                .hasParent(itemValueX)
                .hasFieldName("value")
                .hasKlass(String.class)
                .hasNoChildren();

        final String itemValueYField = "itemValueY";
        final Node itemValueY = assertNode(NodeUtils.getChildNode(rootNode, itemValueYField))
                .hasParent(rootNode)
                .hasFieldName(itemValueYField)
                .hasKlass(Item.class)
                .hasTypeMappedTo(Item.class, "K", "Y")
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1)
                .getAs(Node.class);

        assertNode(getOnlyElement(itemValueY.getChildren()))
                .hasParent(itemValueY)
                .hasFieldName("value")
                .hasKlass(Long.class)
                .hasNoChildren();
    }
}