package org.instancio.model;

import org.instancio.pojo.generics.NestedMaps;
import org.instancio.testsupport.tags.ModelTag;
import org.instancio.util.ReflectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

import static org.instancio.testsupport.asserts.ClassNodeAssert.assertClassNode;
import static org.instancio.testsupport.asserts.FieldNodeAssert.assertFieldNode;
import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

@ModelTag
class FieldNode_NestedMaps_Test {

    private static final Class<NestedMaps> ROOT_CLASS = NestedMaps.class;
    private NodeContext nodeContext;

    @BeforeEach
    void setUp() {
        Map<TypeVariable<?>, Class<?>> typeMap = new HashMap<>();
        typeMap.put(getTypeVar(ROOT_CLASS, "OKEY"), Long.class);
        typeMap.put(getTypeVar(ROOT_CLASS, "IKEY"), String.class);

        nodeContext = new NodeContext(typeMap);
    }

    @Test
    void map1() {
        // Map<Long, Map<String, Boolean>> map1

        final String map1Field = "map1";
        final FieldNode map1FieldNode = new FieldNode(nodeContext, ReflectionUtils.getField(ROOT_CLASS, map1Field));

        assertFieldNode(map1FieldNode)
                .hasParent(null)
                .hasFieldName(map1Field)
                .hasActualFieldType(Map.class)
                .hasTypeMappedTo(getTypeVar(Map.class, "K"), Long.class)
                .hasTypeMappedTo(getTypeVar(Map.class, "V"), Map.class)
                .hasTypeMapWithSize(2)
                .hasChildrenOfSize(1);

        assertNestedMap(map1FieldNode);
    }

    @Test
    void map2() {
        // Map<OKEY, Map<IKEY, Boolean>> map2

        final String map2Field = "map2";
        final FieldNode map2FieldNode = new FieldNode(nodeContext, ReflectionUtils.getField(ROOT_CLASS, map2Field));

        assertFieldNode(map2FieldNode)
                .hasParent(null)
                .hasFieldName(map2Field)
                .hasActualFieldType(Map.class)
                .hasTypeMappedTo(getTypeVar(Map.class, "K"), getTypeVar(NestedMaps.class, "OKEY"))
                .hasTypeMappedTo(getTypeVar(NestedMaps.class, "OKEY"), Long.class)
                .hasTypeMappedTo(getTypeVar(Map.class, "V"), Map.class)
                .hasTypeMapWithSize(3)
                .hasChildrenOfSize(1);

        assertNestedMap(map2FieldNode);
    }

    private void assertNestedMap(FieldNode parentFieldNode) {
        final MapNode outerMapNode = (MapNode) parentFieldNode.getChildren().get(0);

        assertClassNode(outerMapNode.getKeyNode())
                //.hasParent(nestedMapNode) // TODO who should be the parent
                .hasParent(parentFieldNode)
                .hasKlass(Long.class)
                .hasNoChildren();

        final ClassNode mapClassNode = outerMapNode.getValueNode();

        assertClassNode(mapClassNode)
                //.hasParent(nestedMapNode) // TODO who should be the parent
                .hasParent(parentFieldNode)
                .hasKlass(Map.class)
                .hasChildrenOfSize(1);

        final MapNode innerMapNode = (MapNode) mapClassNode.getChildren().get(0);

        assertClassNode(innerMapNode.getKeyNode())
                .hasParent(mapClassNode) // TODO who should be the parent
                .hasKlass(String.class)
                .hasNoChildren();

        assertClassNode(innerMapNode.getValueNode())
                .hasParent(mapClassNode) // TODO who should be the parent
                .hasKlass(Boolean.class)
                .hasNoChildren();
    }

}