package org.instancio.model;

import org.instancio.pojo.generics.container.ItemContainer;
import org.instancio.testsupport.tags.ModelTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.TypeVariable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

@ModelTag
class FieldNode_ItemContainer_Test {

    private static final Class<ItemContainer> ROOT_CLASS = ItemContainer.class;

    private NodeContext nodeContext;

    @BeforeEach
    void setUp() {
        Map<TypeVariable<?>, Class<?>> typeMap = new HashMap<>();
        typeMap.put(getTypeVar(ROOT_CLASS, "X"), String.class);
        typeMap.put(getTypeVar(ROOT_CLASS, "Y"), LocalDateTime.class);

        nodeContext = new NodeContext(typeMap);
    }

    @Test
    void itemValueX() {
//        final String rootField = "itemValueX";
//        final FieldNode node = new FieldNode(nodeContext, ReflectionUtils.getField(ROOT_CLASS, rootField));
//
//        assertFieldNode(node)
//                .hasFieldName(rootField)
//                .hasActualFieldType(GenericItem.class)
//                .hasTypeMappedTo(getTypeVar(GenericItem.class, "K"), getTypeVar(ROOT_CLASS, "X"))
//                .hasTypeMappedTo(getTypeVar(ROOT_CLASS, "X"), String.class)
//                .hasTypeMapWithSize(2)
//                .hasChildrenOfSize(1);
    }

}