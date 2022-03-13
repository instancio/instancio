package org.instancio.model;

import org.instancio.pojo.generics.PairLongPairIntegerString;
import org.instancio.pojo.generics.basic.Pair;
import org.instancio.testsupport.fixtures.Types;
import org.instancio.testsupport.templates.ModelTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;
import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

class PairLongPairIntegerStringModelTest extends ModelTestTemplate<PairLongPairIntegerString> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(PairLongPairIntegerString.class)
                .hasChildrenOfSize(1);

        // Pair<Long, Pair<Integer, String>>
        final String fieldName = "pairLongPairIntegerString";

        final Node outerPair = assertNode(NodeUtils.getChildNode(rootNode, fieldName))
                .hasParent(rootNode)
                .hasFieldName(fieldName)
                .hasEffectiveClass(Pair.class)
                .hasTypeMappedTo(getTypeVar(Pair.class, "L"), Long.class)
                .hasTypeMappedTo(getTypeVar(Pair.class, "R"), Types.PAIR_INTEGER_STRING.getType())
                .hasTypeMapWithSize(2)
                .hasChildrenOfSize(2)
                .getAs(ClassNode.class);

        assertNode(NodeUtils.getChildNode(outerPair, "left"))
                .hasFieldName("left")
                .hasParent(outerPair)
                .hasKlass(Object.class)
                .hasEffectiveClass(Long.class)
                .hasTypeMappedTo(getTypeVar(Pair.class, "L"), Long.class)
                .hasTypeMappedTo(getTypeVar(Pair.class, "R"), Types.PAIR_INTEGER_STRING.getType())
                .hasTypeMapWithSize(2)
                .hasNoChildren();

        final Node innerPair = assertNode(NodeUtils.getChildNode(outerPair, "right"))
                .hasFieldName("right")
                .hasParent(outerPair)
                .hasKlass(Object.class)
                .hasEffectiveClass(Pair.class)
                .hasTypeMappedTo(getTypeVar(Pair.class, "L"), Integer.class)
                .hasTypeMappedTo(getTypeVar(Pair.class, "R"), String.class)
                .hasTypeMapWithSize(2)
                .hasChildrenOfSize(2)
                .getAs(ClassNode.class);

        assertInnerPair(innerPair);
    }

    private void assertInnerPair(Node innerPair) {
        assertNode(NodeUtils.getChildNode(innerPair, "left"))
                .hasFieldName("left")
                .hasParent(innerPair)
                .hasKlass(Object.class)
                .hasEffectiveClass(Integer.class)
                .hasTypeMappedTo(getTypeVar(Pair.class, "L"), Integer.class)
                .hasTypeMappedTo(getTypeVar(Pair.class, "R"), String.class)
                .hasTypeMapWithSize(2)
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(innerPair, "right"))
                .hasFieldName("right")
                .hasParent(innerPair)
                .hasKlass(Object.class)
                .hasEffectiveClass(String.class)
                .hasTypeMappedTo(getTypeVar(Pair.class, "L"), Integer.class)
                .hasTypeMappedTo(getTypeVar(Pair.class, "R"), String.class)
                .hasTypeMapWithSize(2)
                .hasNoChildren();
    }
}