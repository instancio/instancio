package org.instancio.model;

import org.instancio.pojo.collections.maps.MapIntegerListString;
import org.instancio.testsupport.fixtures.Types;
import org.instancio.testsupport.templates.ModelTestTemplate;

import java.util.List;
import java.util.Map;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;
import static org.instancio.testsupport.utils.CollectionUtils.getOnlyElement;

class MapIntegerListStringModelTest extends ModelTestTemplate<MapIntegerListString> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(MapIntegerListString.class)
                .hasChildrenOfSize(1);

        final MapNode map = assertNode(getOnlyElement(rootNode.getChildren()))
                .hasParent(rootNode)
                .hasFieldName("map")
                .hasKlass(Map.class)
                .hasTypeMappedTo(Map.class, "K", Integer.class)
                .hasTypeMappedTo(Map.class, "V", Types.LIST_STRING.getType())
                .hasTypeMapWithSize(2)
                .hasNoChildren()
                .getAs(MapNode.class);

        assertNode(map.getKeyNode())
                .hasParent(rootNode)
                .hasKlass(Integer.class)
                .hasNoChildren();

        final CollectionNode list = assertNode(map.getValueNode())
                .hasParent(rootNode)
                .hasNullField()
                .hasKlass(List.class)
                .hasTypeMappedTo(List.class, "E", String.class)
                .hasNoChildren()
                .getAs(CollectionNode.class);

        assertNode(list.getElementNode())
                .hasNoChildren()
                .hasParent(rootNode)
                .hasKlass(String.class)
                .hasNoChildren();
    }
}