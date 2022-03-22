package org.instancio.model;

import org.instancio.internal.model.Node;
import org.instancio.pojo.inheritance.BaseClasSubClassInheritance;
import org.instancio.testsupport.templates.ModelTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class BaseClasSubClassInheritanceModelTest extends ModelTestTemplate<BaseClasSubClassInheritance> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(BaseClasSubClassInheritance.class)
                .hasChildrenOfSize(1);

        final Node subClass = assertNode(NodeUtils.getChildNode(rootNode, "subClass"))
                .hasChildrenOfSize(3)
                .getAs(Node.class);

        // Subclass field
        assertNode(NodeUtils.getChildNode(subClass, "subClassField"))
                .hasNoChildren();

        // Superclass fields
        assertNode(NodeUtils.getChildNode(subClass, "privateBaseClassField"))
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(subClass, "protectedBaseClassField"))
                .hasNoChildren();

    }
}