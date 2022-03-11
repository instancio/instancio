package org.instancio.model;

import org.instancio.pojo.collections.NestedLists;
import org.instancio.testsupport.fixtures.Types;
import org.instancio.testsupport.tags.ModelTag;
import org.instancio.testsupport.templates.ModelTestTemplate;
import org.instancio.testsupport.utils.CollectionUtils;

import java.util.List;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

@ModelTag
class NestedListsModelTest extends ModelTestTemplate<NestedLists> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(NestedLists.class)
                .hasChildrenOfSize(1);

        // List<List<String>>
        final CollectionNode outerListNode = assertNode(CollectionUtils.getOnlyElement(rootNode.getChildren()))
                .hasNoChildren()
                .hasEffectiveType(Types.LIST_LIST_STRING.getType())
                .getAs(CollectionNode.class);

        // List<String>
        final CollectionNode outerListElementNode = (CollectionNode) outerListNode.getElementNode();

        assertNode(outerListElementNode)
                .hasKlass(List.class)
                .hasEffectiveType(Types.LIST_STRING.getType())
                .hasNoChildren()
                .getAs(CollectionNode.class);

        // String
        assertNode(outerListElementNode.getElementNode())
                .hasKlass(String.class)
                .hasNoChildren();
    }
}