package org.instancio.model;

import org.instancio.pojo.collections.lists.TwoListsWithItemString;
import org.instancio.pojo.generics.basic.Item;
import org.instancio.testsupport.fixtures.Types;
import org.instancio.testsupport.templates.ModelTestTemplate;
import org.instancio.testsupport.utils.CollectionUtils;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

/**
 * Verifies 'visited' node logic doesn't preclude the second list from being processed.
 */
class TwoListsWithItemStringModelTest extends ModelTestTemplate<TwoListsWithItemString> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(TwoListsWithItemString.class)
                .hasChildrenOfSize(2);

        assertListNode(rootNode, "list1");
        assertListNode(rootNode, "list2");
    }

    private void assertListNode(Node rootNode, String listField) {
        final CollectionNode list = assertNode(NodeUtils.getChildNode(rootNode, listField))
                .hasNoChildren()
                .hasEffectiveType(Types.LIST_ITEM_STRING.get())
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