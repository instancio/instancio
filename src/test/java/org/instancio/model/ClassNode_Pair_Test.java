package org.instancio.model;

import org.instancio.pojo.generics.container.Pair;
import org.instancio.testsupport.tags.ModelTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

import static org.instancio.testsupport.asserts.ClassNodeAssert.assertClassNode;
import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

@ModelTag
class ClassNode_Pair_Test {

    private NodeContext nodeContext;

    @BeforeEach
    void setUp() {
        Map<TypeVariable<?>, Class<?>> typeMap = new HashMap<>();
        typeMap.put(getTypeVar(Pair.class, "L"), String.class);
        typeMap.put(getTypeVar(Pair.class, "R"), Integer.class);

        nodeContext = new NodeContext(typeMap);
    }

    @Test
    void classNode() {

        ClassNode rootNode = new ClassNode(nodeContext, null, Pair.class, null, null);

        System.out.println(rootNode);

        assertClassNode(rootNode)
                .hasKlass(Pair.class)
                .hasParent(null)
                .hasChildrenOfSize(2);

        final ClassNode leftNode = (ClassNode) NodeUtil.getChildNode(rootNode, "left");
        assertClassNode(leftNode)
                .hasKlass(Object.class)
                .hasEffectiveClass(String.class)
                .hasGenericTypeName("L")
                .hasNoChildren();

        final ClassNode rightNode = (ClassNode) NodeUtil.getChildNode(rootNode, "right");
        assertClassNode(rightNode)
                .hasKlass(Object.class)
                .hasEffectiveClass(Integer.class)
                .hasGenericTypeName("R")
                .hasNoChildren();

    }
}
