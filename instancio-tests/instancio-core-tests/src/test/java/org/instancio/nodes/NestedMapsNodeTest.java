/*
 * Copyright 2022-2023 the original author or authors.
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
import org.instancio.test.support.pojo.generics.NestedMaps;
import org.instancio.testsupport.fixtures.Types;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import java.util.Map;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class NestedMapsNodeTest extends NodeTestTemplate<NestedMaps<Long, String>> {

    @Override
    protected void verify(InternalNode rootNode) {
        map1(rootNode);
        map2(rootNode);
    }

    private void map1(InternalNode rootNode) {
        // Map<Long, Map<String, Boolean>> map1
        final String fieldName = "map1";

        final InternalNode outerMap = assertNode(NodeUtils.getChildNode(rootNode, fieldName))
                .hasParent(rootNode)
                .hasFieldName(fieldName)
                .hasSetterName("setMap1", Map.class)
                .hasTargetClass(Map.class)
                .hasTypeMappedTo(Map.class, "K", Long.class)
                .hasTypeMappedTo(Map.class, "V", Types.MAP_STRING_BOOLEAN.get())
                .isOfKind(NodeKind.MAP)
                .hasTypeMapWithSize(2)
                .hasChildrenOfSize(2)
                .get();

        assertNestedMap(outerMap);
    }

    private void map2(InternalNode rootNode) {
        // Map<OKEY, Map<IKEY, Boolean>> map2
        final String fieldName = "map2";

        final InternalNode outerMap = assertNode(NodeUtils.getChildNode(rootNode, fieldName))
                .hasParent(rootNode)
                .hasFieldName(fieldName)
                .hasSetterName("setMap2", Map.class)
                .hasTargetClass(Map.class)
                .hasTypeMappedTo(Map.class, "K", "OKEY")
                .hasTypeMappedTo(Map.class, "V", "java.util.Map<IKEY, java.lang.Boolean>")
                .isOfKind(NodeKind.MAP)
                .hasTypeMapWithSize(2)
                .hasChildrenOfSize(2)
                .get();

        assertNestedMap(outerMap);
    }

    private void assertNestedMap(InternalNode outerMap) {
        assertNode(outerMap.getChildren().get(0))
                .hasParent(outerMap)
                .hasNullField()
                .hasTargetClass(Long.class)
                .hasNoChildren();

        final InternalNode innerNode = assertNode(outerMap.getChildren().get(1))
                .hasParent(outerMap)
                .hasNullField()
                .hasTargetClass(Map.class)
                .hasChildrenOfSize(2)
                .get();

        assertNode(innerNode.getChildren().get(0))
                .hasParent(innerNode)
                .hasNullField()
                .hasTargetClass(String.class)
                .hasNoChildren();

        assertNode(innerNode.getChildren().get(1))
                .hasParent(innerNode)
                .hasNullField()
                .hasTargetClass(Boolean.class)
                .hasNoChildren();
    }

}