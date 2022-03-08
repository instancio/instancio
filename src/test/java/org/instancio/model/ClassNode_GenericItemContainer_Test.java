package org.instancio.model;

import org.instancio.pojo.generics.container.GenericItemContainer;
import org.instancio.testsupport.tags.ModelTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.TypeVariable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

@ModelTag
class ClassNode_GenericItemContainer_Test {

    private NodeContext nodeContext;

    @BeforeEach
    void setUp() {
        Map<TypeVariable<?>, Class<?>> typeMap = new HashMap<>();
        typeMap.put(getTypeVar(GenericItemContainer.class, "X"), String.class);
        typeMap.put(getTypeVar(GenericItemContainer.class, "Y"), LocalDateTime.class);

        nodeContext = new NodeContext(typeMap);
    }

    @Test
    void classNode() {
        ClassNode rootNode = ClassNode.createRootNode(nodeContext, GenericItemContainer.class);

        System.out.println(rootNode);
//        Map<Type, Class<?>> typeMap = new HashMap<>();
//        typeMap.put("A", Long.class);
//        typeMap.put("B", String.class);
//        typeMap.put("C", Integer.class);
//
//        final ClassNode node = new ClassNode(MiscFields.class, typeMap);
//
//        System.out.println(node);
    }
}
