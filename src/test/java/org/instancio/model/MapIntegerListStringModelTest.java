package org.instancio.model;

import org.instancio.internal.model.CollectionNode;
import org.instancio.internal.model.MapNode;
import org.instancio.internal.model.Node;
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
                .hasTypeMappedTo(Map.class, "V", Types.LIST_STRING.get())
                .hasTypeMapWithSize(2)
                .hasNoChildren()
                .getAs(MapNode.class);

        assertNode(map.getKeyNode())
                .hasParent(map)
                .hasKlass(Integer.class)
                .hasNoChildren();

        final CollectionNode list = assertNode(map.getValueNode())
                .hasParent(map)
                .hasNullField()
                .hasKlass(List.class)
                .hasTypeMappedTo(List.class, "E", String.class)
                .hasNoChildren()
                .getAs(CollectionNode.class);

        assertNode(list.getElementNode())
                .hasNoChildren()
                .hasParent(list)
                .hasKlass(String.class)
                .hasNoChildren();
    }
}