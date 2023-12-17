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
import org.instancio.test.support.pojo.generics.container.GenericContainer;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import java.util.List;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class GenericContainerStringNodeTest extends NodeTestTemplate<GenericContainer<String>> {

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasDepth(0)
                .hasTargetClass(GenericContainer.class)
                .hasChildrenOfSize(3);

        // T value
        assertNode(NodeUtils.getChildNode(rootNode, "value"))
                .hasDepth(1)
                .hasParent(rootNode)
                .hasField("value")
                .hasTargetClass(String.class)
                .hasEmptyTypeMap()
                .hasNoChildren();

        // T[] array
        final InternalNode array = assertNode(NodeUtils.getChildNode(rootNode, "array"))
                .hasDepth(1)
                .hasParent(rootNode)
                .hasField("array")
                .hasTargetClass(String[].class)
                .hasEmptyTypeMap()
                .hasChildrenOfSize(1)
                .get();

        assertNode(array.getOnlyChild())
                .hasDepth(2)
                .hasParent(array)
                .hasNullField()
                .hasTargetClass(String.class)
                .hasEmptyTypeMap()
                .hasNoChildren();

        // List<T> list
        final InternalNode list = assertNode(NodeUtils.getChildNode(rootNode, "list"))
                .hasDepth(1)
                .hasParent(rootNode)
                .hasField("list")
                .hasTargetClass(List.class)
                .hasTypeMappedTo(List.class, "E", "T")
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1)
                .get();

        assertNode(list.getOnlyChild())
                .hasDepth(2)
                .hasParent(list)
                .hasNullField()
                .hasTargetClass(String.class)
                .hasEmptyTypeMap()
                .hasNoChildren();
    }
}