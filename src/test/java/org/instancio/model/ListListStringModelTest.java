package org.instancio.model;

import org.instancio.pojo.collections.lists.ListListString;
import org.instancio.testsupport.fixtures.Types;
import org.instancio.testsupport.templates.ModelTestTemplate;
import org.instancio.testsupport.utils.CollectionUtils;

import java.util.List;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class ListListStringModelTest extends ModelTestTemplate<ListListString> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(ListListString.class)
                .hasChildrenOfSize(1);

        // List<List<String>>
        final CollectionNode outerListNode = assertNode(CollectionUtils.getOnlyElement(rootNode.getChildren()))
                .hasNoChildren()
                .hasFieldName("nested")
                .hasEffectiveType(Types.LIST_LIST_STRING.getType())
                .getAs(CollectionNode.class);

        // List<String>
        final CollectionNode outerListElementNode = (CollectionNode) outerListNode.getElementNode();

        assertNode(outerListElementNode)
                .hasKlass(List.class)
                .hasNullField()
                .hasEffectiveType(Types.LIST_STRING.getType())
                .hasNoChildren()
                .getAs(CollectionNode.class);

        // String
        assertNode(outerListElementNode.getElementNode())
                .hasKlass(String.class)
                .hasNullField()
                .hasNoChildren();
    }
}