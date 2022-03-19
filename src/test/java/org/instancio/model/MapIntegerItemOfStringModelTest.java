package org.instancio.model;

import org.instancio.pojo.collections.maps.MapIntegerItemOfString;
import org.instancio.pojo.generics.basic.Item;
import org.instancio.testsupport.fixtures.Types;
import org.instancio.testsupport.templates.ModelTestTemplate;

import java.util.Map;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;
import static org.instancio.testsupport.utils.CollectionUtils.getOnlyElement;

class MapIntegerItemOfStringModelTest extends ModelTestTemplate<MapIntegerItemOfString> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(MapIntegerItemOfString.class)
                .hasChildrenOfSize(1);

        final MapNode outerMap = assertNode(getOnlyElement(rootNode.getChildren()))
                .hasParent(rootNode)
                .hasFieldName("map")
                .hasKlass(Map.class)
                .hasTypeMappedTo(Map.class, "K", Integer.class)
                .hasTypeMappedTo(Map.class, "V", Types.ITEM_STRING.get())
                .hasTypeMapWithSize(2)
                .hasNoChildren()
                .getAs(MapNode.class);

        assertNode(outerMap.getKeyNode())
                .hasParent(outerMap)
                .hasKlass(Integer.class);

        assertNode(outerMap.getValueNode())
                .hasParent(outerMap)
                .hasKlass(Item.class)
                .hasChildrenOfSize(1);

        assertNode(getOnlyElement(outerMap.getValueNode().getChildren()))
                .hasParent(outerMap.getValueNode())
                .hasKlass(String.class)
                .hasNoChildren();
    }
}