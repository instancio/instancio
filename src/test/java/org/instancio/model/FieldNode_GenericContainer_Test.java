package org.instancio.model;

import org.instancio.pojo.generics.container.GenericContainer;
import org.instancio.testsupport.tags.ModelTag;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

@ModelTag
class FieldNode_GenericContainer_Test {

    private NodeContext nodeContext;

    @BeforeEach
    void setUp() {
        Map<TypeVariable<?>, Class<?>> typeMap = new HashMap<>();
        typeMap.put(getTypeVar(GenericContainer.class, "T"), String.class);

        nodeContext = new NodeContext(typeMap);
    }
//
//    @Test
//    void value() {
//        final String rootField = "value";
//        final FieldNode node = new FieldNode(nodeContext, ReflectionUtils.getField(GenericContainer.class, rootField));
//
//        assertFieldNode(node)
//                .hasFieldName(rootField)
//                .hasActualFieldType(String.class)
//                .hasEmptyTypeMap()
//                .hasNoChildren();
//    }
//
//    @Test
//    void array() {
//        final String rootField = "array";
//        final FieldNode arrayNode = new FieldNode(nodeContext, ReflectionUtils.getField(GenericContainer.class, rootField));
//
//        assertFieldNode(arrayNode)
//                .hasFieldName(rootField)
//                .hasActualFieldType(Object[].class)
//                .hasEmptyTypeMap()
//                .hasChildrenOfSize(1);
//
//        final ClassNode stringClassNode = (ClassNode) arrayNode.getChildren().get(0);
//
//        assertClassNode(stringClassNode)
//                .hasParent(arrayNode)
//                .hasKlass(String.class)
//                .hasNoChildren();
//    }
//
//    @Test
//    void list() {
//        final String rootField = "list";
//        final FieldNode node = new FieldNode(nodeContext, ReflectionUtils.getField(GenericContainer.class, rootField));
//
//        assertFieldNode(node)
//                .hasFieldName(rootField)
//                .hasActualFieldType(List.class)
//                .hasTypeMappedTo(getTypeVar(List.class, "E"), getTypeVar(GenericContainer.class, "T"))
//                .hasTypeMappedTo(getTypeVar(GenericContainer.class, "T"), String.class)
//                .hasChildrenOfSize(1);
//    }
}