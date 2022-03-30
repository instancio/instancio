/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.nodes;

import org.instancio.internal.nodes.CollectionNode;
import org.instancio.internal.nodes.MapNode;
import org.instancio.internal.nodes.Node;
import org.instancio.pojo.collections.maps.MapIntegerListString;
import org.instancio.testsupport.fixtures.Types;
import org.instancio.testsupport.templates.NodeTestTemplate;

import java.util.List;
import java.util.Map;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;
import static org.instancio.testsupport.utils.CollectionUtils.getOnlyElement;

class MapIntegerListStringNodeTest extends NodeTestTemplate<MapIntegerListString> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasTargetClass(MapIntegerListString.class)
                .hasChildrenOfSize(1);

        final MapNode map = assertNode(getOnlyElement(rootNode.getChildren()))
                .hasParent(rootNode)
                .hasFieldName("map")
                .hasTargetClass(Map.class)
                .hasTypeMappedTo(Map.class, "K", Integer.class)
                .hasTypeMappedTo(Map.class, "V", Types.LIST_STRING.get())
                .hasTypeMapWithSize(2)
                .hasNoChildren()
                .getAs(MapNode.class);

        assertNode(map.getKeyNode())
                .hasParent(map)
                .hasTargetClass(Integer.class)
                .hasNoChildren();

        final CollectionNode list = assertNode(map.getValueNode())
                .hasParent(map)
                .hasNullField()
                .hasTargetClass(List.class)
                .hasTypeMappedTo(List.class, "E", String.class)
                .hasNoChildren()
                .getAs(CollectionNode.class);

        assertNode(list.getElementNode())
                .hasNoChildren()
                .hasParent(list)
                .hasTargetClass(String.class)
                .hasNoChildren();
    }
}