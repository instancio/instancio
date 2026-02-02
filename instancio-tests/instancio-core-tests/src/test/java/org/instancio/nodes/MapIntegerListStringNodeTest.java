/*
 * Copyright 2022-2026 the original author or authors.
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

import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeKind;
import org.instancio.test.support.pojo.collections.maps.MapIntegerListString;
import org.instancio.testsupport.fixtures.Types;
import org.instancio.testsupport.templates.NodeTestTemplate;

import java.util.List;
import java.util.Map;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class MapIntegerListStringNodeTest extends NodeTestTemplate<MapIntegerListString> {

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasTargetClass(MapIntegerListString.class)
                .hasChildrenOfSize(1);

        final InternalNode map = assertNode(rootNode.getOnlyChild())
                .hasParent(rootNode)
                .hasField("map")
                .isOfKind(NodeKind.MAP)
                .hasTargetClass(Map.class)
                .hasTypeMappedTo(Map.class, "K", Integer.class)
                .hasTypeMappedTo(Map.class, "V", Types.LIST_STRING.get())
                .hasTypeMapWithSize(2)
                .hasChildrenOfSize(2)
                .get();

        assertNode(map.getChildren().get(0))
                .hasParent(map)
                .hasTargetClass(Integer.class)
                .hasNoChildren();

        final InternalNode list = assertNode(map.getChildren().get(1))
                .hasParent(map)
                .hasNullField()
                .isOfKind(NodeKind.COLLECTION)
                .hasTargetClass(List.class)
                .hasTypeMappedTo(List.class, "E", String.class)
                .hasChildrenOfSize(1)
                .get();

        assertNode(list.getOnlyChild())
                .hasNoChildren()
                .hasParent(list)
                .hasTargetClass(String.class)
                .hasNoChildren();
    }
}