package org.instancio.model;

import org.instancio.pojo.generics.container.ItemArrayContainer;
import org.instancio.testsupport.tags.ModelTag;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.TypeVariable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

@ModelTag
class FieldNode_ItemArrayContainer_Test {

    private static final Class<ItemArrayContainer> ROOT_CLASS = ItemArrayContainer.class;

    private NodeContext nodeContext;

    @BeforeEach
    void setUp() {
        Map<TypeVariable<?>, Class<?>> typeMap = new HashMap<>();
        typeMap.put(getTypeVar(ROOT_CLASS, "X"), String.class);
        typeMap.put(getTypeVar(ROOT_CLASS, "Y"), LocalDateTime.class);

        nodeContext = new NodeContext(typeMap);
    }
//
//    @Test
//    void itemArrayX() {
//        final String rootField = "itemArrayX";
//        final FieldNode node = new FieldNode(nodeContext, ReflectionUtils.getField(ROOT_CLASS, rootField));
//
//        assertFieldNode(node)
//                .hasFieldName(rootField)
//                .hasActualFieldType(GenericItem[].class)
//                // TODO
////                .hasTypeMappedTo(getTypeVar(GenericItem.class, "K"), getTypeVar(GenericItemContainer.class, "X"))
////                .hasTypeMappedTo(getTypeVar(GenericItemContainer.class, "X"), String.class)
////                .hasTypeMapWithSize(2)
//                .hasChildrenOfSize(1);
//
//        final ClassNode genericItemClassNode = (ClassNode) node.getChildren().get(0);
//
//        assertClassNode(genericItemClassNode)
//                .hasParent(node)
//                .hasKlass(GenericItem.class)
//                .hasGenericTypeName("org.instancio.pojo.generics.container.GenericItem<X>")
//                .hasParent(node)
//                .hasChildrenOfSize(1);
//
//        final FieldNode valueFieldNode = (FieldNode) genericItemClassNode.getChildren().get(0);
//
//        assertFieldNode(valueFieldNode)
//                .hasParent(genericItemClassNode)
//                .hasFieldName("value")
//                .hasActualFieldType(String.class)
//                .hasTypeMappedTo(getTypeVar(GenericItem.class, "K"), getTypeVar(ROOT_CLASS, "X"))
//                .hasTypeMappedTo(getTypeVar(ROOT_CLASS, "X"), String.class)
//                .hasTypeMapWithSize(2)
//                .hasNoChildren();
//    }

}