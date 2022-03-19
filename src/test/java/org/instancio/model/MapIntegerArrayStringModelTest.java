package org.instancio.model;

import org.instancio.pojo.collections.maps.MapIntegerArrayString;
import org.instancio.testsupport.templates.ModelTestTemplate;

import java.util.Map;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;
import static org.instancio.testsupport.utils.CollectionUtils.getOnlyElement;

class MapIntegerArrayStringModelTest extends ModelTestTemplate<MapIntegerArrayString> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(MapIntegerArrayString.class)
                .hasChildrenOfSize(1);

        final MapNode map = assertNode(getOnlyElement(rootNode.getChildren()))
                .hasParent(rootNode)
                .hasFieldName("map")
                .hasKlass(Map.class)
                .hasTypeMappedTo(Map.class, "K", Integer.class)
                .hasTypeMappedTo(Map.class, "V", String[].class)
                .hasTypeMapWithSize(2)
                .hasNoChildren()
                .getAs(MapNode.class);

        assertNode(map.getKeyNode())
                .hasParent(map)
                .hasKlass(Integer.class)
                .hasNoChildren();

        final ArrayNode array = assertNode(map.getValueNode())
                .hasParent(map)
                .hasNullField()
                .hasKlass(String[].class)
                .hasNoChildren()
                .getAs(ArrayNode.class);

        assertNode(array.getElementNode())
                .hasNoChildren()
                .hasParent(array)
                .hasKlass(String.class)
                .hasNoChildren();
    }
}