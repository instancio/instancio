package org.instancio.model;

import org.instancio.pojo.generics.container.GenericArrayContainer;
import org.instancio.testsupport.tags.ModelTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.TypeVariable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Fail.fail;
import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

@ModelTag
class ClassNode_GenericArrayContainer_Test {

    private static final Class<GenericArrayContainer> ROOT_CLASS = GenericArrayContainer.class;

    private NodeContext nodeContext;

    @BeforeEach
    void setUp() {
        Map<TypeVariable<?>, Class<?>> typeMap = new HashMap<>();
        typeMap.put(getTypeVar(ROOT_CLASS, "X"), String.class);
        typeMap.put(getTypeVar(ROOT_CLASS, "Y"), LocalDateTime.class);

        nodeContext = new NodeContext(typeMap);
    }

    @Test
    void classNode() {
        fail("TODO");
/*
        ClassNode rootNode = ClassNode.createRootNode(nodeContext, ROOT_CLASS);

        assertClassNode(rootNode)
                .hasKlass(ROOT_CLASS)
                .hasParent(null)
                .hasChildrenOfSize(2)
                .hasGenericType(null);

        //
        // array X and children
        //
        final FieldNode itemArrayXFieldNode = NodeUtil.getChildFieldNode(rootNode, "itemArrayX");

        assertFieldNode(itemArrayXFieldNode)
                .hasParent(rootNode)
                .hasActualFieldType(GenericItem[].class)
                .hasChildrenOfSize(1);

        final ClassNode genericItemClassNodeOfArrayXClassNode = (ClassNode) itemArrayXFieldNode.getChildren().get(0);

        assertClassNode(genericItemClassNodeOfArrayXClassNode)
                .hasParent(itemArrayXFieldNode)
                .hasKlass(GenericItem.class)
                .hasChildrenOfSize(1);

        final FieldNode genericItemClassNodeOfArrayXFieldNode = (FieldNode) genericItemClassNodeOfArrayXClassNode.getChildren().get(0);

        assertFieldNode(genericItemClassNodeOfArrayXFieldNode)
                .hasParent(genericItemClassNodeOfArrayXClassNode)
                .hasActualFieldType(String.class)
                .hasTypeMappedTo(getTypeVar(GenericItem.class, "K"), getTypeVar(ROOT_CLASS, "X"))
                .hasTypeMappedTo(getTypeVar(ROOT_CLASS, "X"), String.class)
                .hasTypeMapWithSize(2)
                .hasNoChildren();

        //
        // array y and children
        //
        final FieldNode itemArrayYFieldNode = NodeUtil.getChildFieldNode(rootNode, "itemArrayY");

        assertFieldNode(itemArrayYFieldNode)
                .hasParent(rootNode)
                .hasActualFieldType(GenericItem[].class)
                .hasChildrenOfSize(1);

        final ClassNode genericItemClassNodeOfArrayYClassNode = (ClassNode) itemArrayYFieldNode.getChildren().get(0);

        assertClassNode(genericItemClassNodeOfArrayYClassNode)
                .hasParent(itemArrayYFieldNode)
                .hasKlass(GenericItem.class)
                .hasChildrenOfSize(1);

        final FieldNode genericItemClassNodeOfArrayYFieldNode = (FieldNode) genericItemClassNodeOfArrayYClassNode.getChildren().get(0);

        assertFieldNode(genericItemClassNodeOfArrayYFieldNode)
                .hasParent(genericItemClassNodeOfArrayYClassNode)
                .hasActualFieldType(LocalDateTime.class)
                .hasTypeMappedTo(getTypeVar(GenericItem.class, "K"), getTypeVar(ROOT_CLASS, "Y"))
                .hasTypeMappedTo(getTypeVar(ROOT_CLASS, "Y"), LocalDateTime.class)
                .hasTypeMapWithSize(2)
                .hasNoChildren();
*/
    }
}
