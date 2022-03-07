package org.instancio.model;

import org.instancio.pojo.generics.container.GenericItem;
import org.instancio.pojo.generics.container.GenericItemContainer;
import org.instancio.testsupport.tags.ModelTag;
import org.instancio.util.ReflectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.TypeVariable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.instancio.testsupport.asserts.FieldNodeAssert.assertFieldNode;
import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

@ModelTag
class FieldNode_GenericItemContainer_Test {

    private NodeContext nodeContext;

    @BeforeEach
    void setUp() {
        Map<TypeVariable<?>, Class<?>> typeMap = new HashMap<>();
        typeMap.put(getTypeVar(GenericItemContainer.class, "X"), String.class);
        typeMap.put(getTypeVar(GenericItemContainer.class, "Y"), LocalDateTime.class);

        nodeContext = new NodeContext(typeMap);
    }

    @Test
    void itemValueL() {
        final String rootField = "itemValueL";
        final FieldNode node = new FieldNode(nodeContext, ReflectionUtils.getField(GenericItemContainer.class, rootField));

        assertFieldNode(node)
                .hasFieldName(rootField)
                .hasActualFieldType(GenericItem.class)
                .hasTypeMappedTo(getTypeVar(GenericItem.class, "K"), getTypeVar(GenericItemContainer.class, "X"))
                .hasTypeMappedTo(getTypeVar(GenericItemContainer.class, "X"), String.class)
                .hasTypeMapWithSize(2)
                .hasChildrenOfSize(1);

    }
}