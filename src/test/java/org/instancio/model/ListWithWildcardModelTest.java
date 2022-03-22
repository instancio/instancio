package org.instancio.model;

import org.instancio.internal.model.CollectionNode;
import org.instancio.internal.model.Node;
import org.instancio.pojo.generics.ListWithWildcard;
import org.instancio.testsupport.templates.ModelTestTemplate;
import org.instancio.testsupport.utils.CollectionUtils;

import java.util.List;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class ListWithWildcardModelTest extends ModelTestTemplate<ListWithWildcard> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(ListWithWildcard.class)
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