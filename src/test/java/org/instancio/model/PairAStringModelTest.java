package org.instancio.model;

import org.instancio.pojo.generics.PairAString;
import org.instancio.pojo.generics.basic.Pair;
import org.instancio.testsupport.templates.ModelTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import java.util.UUID;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class PairAStringModelTest extends ModelTestTemplate<PairAString<UUID>> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(PairAString.class)
                .hasChildrenOfSize(1);

        // Pair<A, String>
        final String fieldName = "pairAString";

        final Node pair = assertNode(NodeUtils.getChildNode(rootNode, fieldName))
                .hasParent(rootNode)
                .hasFieldName(fieldName)
                .hasEffectiveClass(Pair.class)
                .hasTypeMappedTo(Pair.class, "L", UUID.class)
                .hasTypeMappedTo(Pair.class, "R", String.class)
                .hasTypeMapWithSize(2)
                .hasChildrenOfSize(2)
                .getAs(ClassNode.class);

        assertNode(NodeUtils.getChildNode(pair, "left"))
                .hasFieldName("left")
                .hasParent(pair)
                .hasKlass(UUID.class)
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(pair, "right"))
                .hasFieldName("right")
                .hasParent(pair)
                .hasKlass(String.class)
                .hasNoChildren();
    }
}