package org.instancio.model;

import org.instancio.pojo.circular.IndirectCircularRef;
import org.instancio.testsupport.tags.CircularRefsTag;
import org.instancio.testsupport.templates.ModelTestTemplate;
import org.instancio.testsupport.utils.CollectionUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

@CircularRefsTag
class IndirectCircularRefModelTest extends ModelTestTemplate<IndirectCircularRef> {

    // TODO
    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(IndirectCircularRef.class)
                .hasChildrenOfSize(1);

        final Node startA = assertNode(CollectionUtils.getOnlyElement(rootNode.getChildren()))
                .hasKlass(IndirectCircularRef.A.class)
                .hasFieldName("startA")
                .hasChildrenOfSize(1)
                .getAs(Node.class);

        final Node b = assertNode(CollectionUtils.getOnlyElement(startA.getChildren()))
                .hasKlass(IndirectCircularRef.B.class)
                .hasFieldName("b")
                .hasChildrenOfSize(1)
                .getAs(Node.class);

        final Node c = assertNode(CollectionUtils.getOnlyElement(b.getChildren()))
                .hasKlass(IndirectCircularRef.C.class)
                .hasFieldName("c")
                .hasChildrenOfSize(1)
                .getAs(Node.class);

        final Node endA = assertNode(CollectionUtils.getOnlyElement(c.getChildren()))
                .hasKlass(IndirectCircularRef.A.class)
                .hasFieldName("endA")
                .hasChildrenOfSize(1)
                .getAs(Node.class);

        final Node bAgain = assertNode(CollectionUtils.getOnlyElement(endA.getChildren()))
                .hasKlass(IndirectCircularRef.B.class)
                .hasFieldName("b")
                .hasChildrenOfSize(1)
                .getAs(Node.class);

        final Node cAgain = assertNode(CollectionUtils.getOnlyElement(bAgain.getChildren()))
                .hasKlass(IndirectCircularRef.C.class)
                .hasFieldName("c")
                .hasChildrenOfSize(1)
                .getAs(Node.class);

        final Node aAgain = assertNode(CollectionUtils.getOnlyElement(cAgain.getChildren()))
                .hasKlass(IndirectCircularRef.A.class)
                .hasFieldName("endA")
                .hasChildrenOfSize(1)
                .getAs(Node.class);

    }
}