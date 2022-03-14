package org.instancio.model;

import org.instancio.pojo.circular.BidirectionalOneToOne;
import org.instancio.testsupport.tags.CircularRefsTag;
import org.instancio.testsupport.templates.ModelTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

@CircularRefsTag
class BidirectionalOneToOneModelTest extends ModelTestTemplate<BidirectionalOneToOne.Parent> {

    // TODO
    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(BidirectionalOneToOne.Parent.class)
                .hasChildrenOfSize(2);

        final Node child = assertNode(NodeUtils.getChildNode(rootNode, "child"))
                .hasParent(rootNode)
                .hasFieldName("child")
                .hasKlass(BidirectionalOneToOne.Child.class)
                .hasChildrenOfSize(2)
                .getAs(Node.class);

        final Node parent = assertNode(NodeUtils.getChildNode(child, "parent"))
                .hasParent(child)
                .hasFieldName("parent")
                .hasKlass(BidirectionalOneToOne.Parent.class)
                .hasChildrenOfSize(2)
                .getAs(Node.class);

        final Node childAgain = assertNode(NodeUtils.getChildNode(parent, "child"))
                .hasParent(parent)
                .hasFieldName("child")
                .hasKlass(BidirectionalOneToOne.Child.class)
                .hasChildrenOfSize(2)
                .getAs(Node.class);

        final Node parentAgain = assertNode(NodeUtils.getChildNode(childAgain, "parent"))
                .hasParent(childAgain)
                .hasFieldName("parent")
                .hasKlass(BidirectionalOneToOne.Parent.class)
                .hasChildrenOfSize(2)
                .getAs(Node.class);

    }
}