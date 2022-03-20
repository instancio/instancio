package org.instancio.model;

import org.instancio.pojo.generics.ListWithoutType;
import org.instancio.testsupport.templates.ModelTestTemplate;
import org.instancio.testsupport.utils.CollectionUtils;

import java.util.List;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class ListWithoutTypeModelTest extends ModelTestTemplate<ListWithoutType> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(ListWithoutType.class)
                .hasChildrenOfSize(1);

        final CollectionNode list = assertNode(CollectionUtils.getOnlyElement(rootNode.getChildren()))
                .hasFieldName("list")
                .hasNoChildren()
                .hasKlass(List.class)
                .getAs(CollectionNode.class);

        assertNode(list.getElementNode())
                .hasKlass(Object.class);
    }
}