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

import org.instancio.internal.nodes.MapNode;
import org.instancio.internal.nodes.Node;
import org.instancio.test.support.pojo.collections.maps.MapIntegerItemOfString;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.testsupport.fixtures.Types;
import org.instancio.testsupport.templates.NodeTestTemplate;

import java.util.Map;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;
import static org.instancio.testsupport.utils.CollectionUtils.getOnlyElement;

class MapIntegerItemOfStringNodeTest extends NodeTestTemplate<MapIntegerItemOfString> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasTargetClass(MapIntegerItemOfString.class)
                .hasChildrenOfSize(1);

        final MapNode outerMap = assertNode(getOnlyElement(rootNode.getChildren()))
                .hasParent(rootNode)
                .hasFieldName("map")
                .hasTargetClass(Map.class)
                .hasTypeMappedTo(Map.class, "K", Integer.class)
                .hasTypeMappedTo(Map.class, "V", Types.ITEM_STRING.get())
                .hasTypeMapWithSize(2)
                .hasNoChildren()
                .getAs(MapNode.class);

        assertNode(outerMap.getKeyNode())
                .hasParent(outerMap)
                .hasTargetClass(Integer.class);

        assertNode(outerMap.getValueNode())
                .hasParent(outerMap)
                .hasTargetClass(Item.class)
                .hasChildrenOfSize(1);

        assertNode(getOnlyElement(outerMap.getValueNode().getChildren()))
                .hasParent(outerMap.getValueNode())
                .hasTargetClass(String.class)
                .hasNoChildren();
    }
}