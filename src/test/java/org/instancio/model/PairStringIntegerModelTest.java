package org.instancio.model;

import org.instancio.pojo.generics.basic.Pair;
import org.instancio.testsupport.templates.ModelTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class PairStringIntegerModelTest extends ModelTestTemplate<Pair<String, Integer>> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(Pair.class)
                .hasTypeMappedTo(Pair.class, "L", String.class)
                .hasTypeMappedTo(Pair.class, "R", Integer.class)
                .hasTypeMapWithSize(2)
                .hasChildrenOfSize(2);

        // L left
        assertNode(NodeUtils.getChildNode(rootNode, "left"))
                .hasFieldName("left")
                .hasParent(rootNode)
                .hasKlass(String.class)
                .hasEmptyTypeMap()
                .hasNoChildren();

        // R right
        assertNode(NodeUtils.getChildNode(rootNode, "right"))
                .hasFieldName("right")
                .hasParent(rootNode)
                .hasKlass(Integer.class)
                .hasEmptyTypeMap()
                .hasNoChildren();
    }
}