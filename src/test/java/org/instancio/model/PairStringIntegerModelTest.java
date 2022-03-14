package org.instancio.model;

import org.instancio.pojo.generics.PairAString;
import org.instancio.pojo.generics.basic.Pair;
import org.instancio.testsupport.templates.ModelTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import java.util.UUID;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;
import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

class PairStringIntegerModelTest extends ModelTestTemplate<Pair<String, Integer>> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(Pair.class)
                .hasChildrenOfSize(2);

        // L left
        assertNode(NodeUtils.getChildNode(rootNode, "left"))
                .hasFieldName("left")
                .hasParent(rootNode)
                //.hasKlass(Object.class)
                .hasEffectiveClass(String.class)
//                .hasTypeMappedTo(getTypeVar(Pair.class, "L"), getTypeVar(PairAString.class, "A"))
//                .hasTypeMappedTo(getTypeVar(Pair.class, "R"), String.class)
//                .hasTypeMappedTo(getTypeVar(PairAString.class, "A"), UUID.class)
//                .hasTypeMapWithSize(3)
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(rootNode, "right"))
                .hasFieldName("right")
                .hasParent(rootNode)
//                .hasKlass(Object.class)
                .hasEffectiveClass(Integer.class)
//                .hasTypeMappedTo(getTypeVar(Pair.class, "L"), getTypeVar(PairAString.class, "A"))
//                .hasTypeMappedTo(getTypeVar(Pair.class, "R"), String.class)
//                .hasTypeMappedTo(getTypeVar(PairAString.class, "A"), UUID.class)
//                .hasTypeMapWithSize(3)
                .hasNoChildren();
    }
}