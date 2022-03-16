package org.instancio.model;

import org.instancio.pojo.circular.CircularList;
import org.instancio.testsupport.templates.ModelTestTemplate;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class CircularListModelTest extends ModelTestTemplate<CircularList> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(CircularList.class)
                .hasChildrenOfSize(1);
    }
}