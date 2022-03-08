package org.instancio.model;

import org.instancio.pojo.generics.NestedMaps;
import org.instancio.testsupport.tags.ModelTag;
import org.instancio.util.ReflectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Fail.fail;
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

        final MapNode nestedMapNode = (MapNode) map1FieldNode.getChildren().get(0);

        assertClassNode(nestedMapNode.getKeyNode())
                //.hasParent(nestedMapNode) // TODO verify
                .hasKlass(Long.class)
                .hasNoChildren();

        assertClassNode(nestedMapNode.getValueNode())
                //.hasParent(nestedMapNode) // TODO verify
                .hasKlass(Map.class)
                .hasChildrenOfSize(1); // XXX should have another child..
    }

    @Test
    void map2() {
        //    private Map<OKEY, Map<IKEY, Boolean>> map2;
        fail("TODO");
    }

}