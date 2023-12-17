package org.instancio.nodes;

import org.instancio.internal.nodes.InternalNode;
import org.instancio.test.support.pojo.generics.inheritance.GenericTypesWithInheritance;
import org.instancio.test.support.pojo.generics.inheritance.GenericTypesWithInheritance.GenericLevel4;
import org.instancio.test.support.tags.GenericsTag;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

@GenericsTag
public class GenericLevelNodeTest extends NodeTestTemplate<GenericLevel4> {

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasDepth(0)
                .hasTargetClass(GenericTypesWithInheritance.GenericLevel4.class)
                .hasTypeMappedTo(GenericTypesWithInheritance.GenericLevel2.class, "ID", Long.class)
                .hasTypeMappedTo(GenericTypesWithInheritance.EntityWithId.class, "ID", "ID")
                .hasTypeMapWithSize(2)
                .hasChildrenOfSize(1);

        assertId(rootNode);
    }

    private static void assertId(final InternalNode rootNode) {
        assertNode(NodeUtils.getChildNode(rootNode, "id"))
                .hasDepth(1)
                .hasParent(rootNode)
                .hasFieldName("id")
                .hasTargetClass(Long.class)
                .hasEmptyTypeMap()
                .hasNoChildren()
                .get();
    }
}
