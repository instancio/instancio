package org.instancio.model;

import org.instancio.pojo.generics.container.GenericItem;
import org.instancio.pojo.generics.container.GenericItemContainer;
import org.instancio.util.ReflectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.TypeVariable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.instancio.testsupport.asserts.FieldNodeAssert.assertFieldNode;
import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

class FieldNode_GenericItemContainer_Test {

    private final Map<TypeVariable<?>, Class<?>> classGenericItemContainerTypeMap = new HashMap<>();

    @BeforeEach
    void setUp() {
        classGenericItemContainerTypeMap.put(getTypeVar(GenericItemContainer.class, "X"), String.class);
        classGenericItemContainerTypeMap.put(getTypeVar(GenericItemContainer.class, "Y"), LocalDateTime.class);
    }

    @Test
    void itemValueL() {
        final String rootField = "itemValueL";
        final FieldNode node = new FieldNode(ReflectionUtils.getField(GenericItemContainer.class, rootField),
                classGenericItemContainerTypeMap);

        assertFieldNode(node)
                .hasFieldName(rootField)
                .hasActualFieldType(GenericItem.class)
                .hasTypeMappedTo(getTypeVar(GenericItem.class, "K"), getTypeVar(GenericItemContainer.class, "X"))
                .hasTypeMappedTo(getTypeVar(GenericItemContainer.class, "X"), String.class)
                .hasTypeMapWithSize(2)
                .hasChildrenOfSize(1);

    }
}