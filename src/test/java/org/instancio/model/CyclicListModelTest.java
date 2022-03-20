package org.instancio.model;

import org.instancio.pojo.cyclic.CyclicList;
import org.instancio.testsupport.templates.ModelTestTemplate;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class CyclicListModelTest extends ModelTestTemplate<CyclicList> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(CyclicList.class)
                .hasChildrenOfSize(1);
    }
}