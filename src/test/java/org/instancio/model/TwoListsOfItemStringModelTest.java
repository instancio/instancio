package org.instancio.model;

import org.instancio.internal.model.CollectionNode;
import org.instancio.internal.model.Node;
import org.instancio.pojo.collections.lists.TwoListsOfItemString;
import org.instancio.pojo.generics.basic.Item;
import org.instancio.testsupport.fixtures.Types;
import org.instancio.testsupport.templates.ModelTestTemplate;
import org.instancio.testsupport.utils.CollectionUtils;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

/**
 * Verifies 'visited' node logic doesn't preclude the second list from being processed.
 */
class TwoListsOfItemStringModelTest extends ModelTestTemplate<TwoListsOfItemString> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(TwoListsOfItemString.class)
                .hasChildrenOfSize(2);

        assertListNode(rootNode, "list1");
        assertListNode(rootNode, "list2");
    }

    private void assertListNode(Node rootNode, String listField) {
        final CollectionNode list = assertNode(NodeUtils.getChildNode(rootNode, listField))
                .hasNoChildren()
                .hasGenericType(Types.LIST_ITEM_STRING.get())
                .getAs(CollectionNode.class);

        assertNode(list.getElementNode())
                .hasNullField()
                .hasKlass(Item.class)
                .hasChildrenOfSize(1);

        assertNode(CollectionUtils.getOnlyElement(list.getElementNode().getChildren()))
                .hasFieldName("value")
                .hasKlass(String.class)
                .hasNoChildren();
    }
}