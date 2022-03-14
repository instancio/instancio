package org.instancio.model;

import org.instancio.pojo.collections.IntegerStringMap;
import org.instancio.testsupport.templates.ModelTestTemplate;

import java.util.Map;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;
import static org.instancio.testsupport.utils.CollectionUtils.getOnlyElement;

class IntegerStringMapModelTest extends ModelTestTemplate<IntegerStringMap> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(IntegerStringMap.class)
                .hasChildrenOfSize(1);

        final MapNode map = assertNode(getOnlyElement(rootNode.getChildren()))
                .hasParent(rootNode)
                .hasFieldName("mapField")
                .hasKlass(Map.class)
                .hasTypeMappedTo(Map.class, "K", Integer.class)
                .hasTypeMappedTo(Map.class, "V", String.class)
                .hasTypeMapWithSize(2)
                .hasNoChildren()
                .getAs(MapNode.class);

        assertNode(map.getKeyNode())
                .hasParent(rootNode)
                .hasKlass(Integer.class)
                .hasNoChildren();

        assertNode(map.getValueNode())
                .hasParent(rootNode)
                .hasKlass(String.class)
                .hasNoChildren();
    }
}