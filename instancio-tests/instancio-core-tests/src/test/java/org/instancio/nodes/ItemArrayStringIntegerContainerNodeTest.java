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

import org.instancio.internal.nodes.Node;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.generics.container.ItemArrayContainer;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class ItemArrayStringIntegerContainerNodeTest extends NodeTestTemplate<ItemArrayContainer<String, Integer>> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasTargetClass(ItemArrayContainer.class)
                .hasChildrenOfSize(2);

        // Item<X>[] itemArrayX
        assertItemArrayX(rootNode);

        // Item<Y>[] itemArrayX
        assertItemArrayY(rootNode);
    }

    private void assertItemArrayX(Node rootNode) {
        final String itemArrayField = "itemArrayX";
        final Node array = assertNode(NodeUtils.getChildNode(rootNode, itemArrayField))
                .hasFieldName(itemArrayField)
                .hasTargetClass(Item[].class)
                .hasTypeName("org.instancio.test.support.pojo.generics.basic.Item<X>[]")
                .hasChildrenOfSize(1)
                .hasEmptyTypeMap()
                .get();

        assertElementNode(array, "X");
    }

    private void assertItemArrayY(Node rootNode) {
        final String itemArrayField = "itemArrayY";
        final Node array = assertNode(NodeUtils.getChildNode(rootNode, itemArrayField))
                .hasFieldName(itemArrayField)
                .hasTargetClass(Item[].class)
                .hasTypeName("org.instancio.test.support.pojo.generics.basic.Item<Y>[]")
                .hasChildrenOfSize(1)
                .hasEmptyTypeMap()
                .get();

        assertElementNode(array, "Y");
    }

    private void assertElementNode(Node arrayNode, String expectedType) {
        final Node elementNode = arrayNode.getOnlyChild();
        assertNode(elementNode)
                .hasTargetClass(Item.class)
                .hasNullField()
                .hasParent(arrayNode)
                .hasTypeMappedTo(Item.class, "K", expectedType)
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1);
    }
}