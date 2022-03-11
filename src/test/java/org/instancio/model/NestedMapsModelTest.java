package org.instancio.model;

import org.instancio.pojo.generics.NestedMaps;
import org.instancio.testsupport.fixtures.Types;
import org.instancio.testsupport.tags.ModelTag;
import org.instancio.testsupport.templates.ModelTestTemplate;

import java.util.Map;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;
import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

@ModelTag
class NestedMapsModelTest extends ModelTestTemplate<NestedMaps<Long, String>> {

    @Override
    protected void verify(Node rootNode) {
        map1(rootNode);
        map2(rootNode);
    }

    private void map1(Node rootNode) {
        // Map<Long, Map<String, Boolean>> map1
        final String fieldName = "map1";

        final MapNode outerMap = assertNode(NodeUtil.getChildNode(rootNode, fieldName))
                .hasParent(rootNode)
                .hasFieldName(fieldName)
                .hasEffectiveClass(Map.class)
                .hasTypeMappedTo(getTypeVar(Map.class, "K"), Long.class)
                .hasTypeMappedTo(getTypeVar(Map.class, "V"), Types.MAP_STRING_BOOLEAN.getType())
                .hasTypeMapWithSize(2)
                .hasNoChildren()
                .getAs(MapNode.class);

        assertNestedMap(outerMap);
    }

    private void map2(Node rootNode) {
        // Map<OKEY, Map<IKEY, Boolean>> map2
        final String fieldName = "map2";
        final String expectedNestedMapGenericType = "java.util.Map<IKEY, java.lang.Boolean>";

        final MapNode outerMap = assertNode(NodeUtil.getChildNode(rootNode, fieldName))
                .hasParent(rootNode)
                .hasFieldName(fieldName)
                .hasEffectiveClass(Map.class)
                .hasTypeMappedTo(getTypeVar(Map.class, "K"), getTypeVar(NestedMaps.class, "OKEY"))
                .hasTypeMappedTo(getTypeVar(NestedMaps.class, "OKEY"), Long.class)
                .hasTypeMappedTo(getTypeVar(Map.class, "V"), expectedNestedMapGenericType)
                .hasTypeMapWithSize(3)
                .hasNoChildren()
                .getAs(MapNode.class);

        assertNestedMap(outerMap);
    }

    private void assertNestedMap(MapNode outerMap) {
        assertNode(outerMap.getKeyNode())
                .hasParent(outerMap)
                .hasKlass(Long.class)
                .hasEffectiveClass(Long.class)
                .hasNoChildren();

        final MapNode innerMapNode = assertNode(outerMap.getValueNode())
                // nested map belongs to the "value" node of the MapNode,
                // not the outer MapNode itself.
                .hasParent(outerMap.getValueNode())
                .hasKlass(Map.class)
                .hasEffectiveClass(Map.class)
                .hasNoChildren()
                .getAs(MapNode.class);

        assertNode(innerMapNode.getKeyNode())
                .hasParent(innerMapNode)
                .hasKlass(String.class)
                .hasEffectiveClass(String.class)
                .hasNoChildren();

        assertNode(innerMapNode.getValueNode())
                .hasParent(innerMapNode)
                .hasKlass(Boolean.class)
                .hasEffectiveClass(Boolean.class)
                .hasNoChildren();
    }

}