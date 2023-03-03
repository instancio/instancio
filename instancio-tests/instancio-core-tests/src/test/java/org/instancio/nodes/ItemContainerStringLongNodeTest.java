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
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.generics.container.ItemContainer;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.test.support.util.CollectionUtils.getOnlyElement;
import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class ItemContainerStringLongNodeTest extends NodeTestTemplate<ItemContainer<String, Long>> {

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasTargetClass(ItemContainer.class)
                .hasChildrenOfSize(2);

        final String itemValueXField = "itemValueX";
        final InternalNode itemValueX = assertNode(NodeUtils.getChildNode(rootNode, itemValueXField))
                .hasParent(rootNode)
                .hasFieldName(itemValueXField)
                .hasTargetClass(Item.class)
                .hasTypeMappedTo(Item.class, "K", "X")
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1)
                .get();

        assertNode(getOnlyElement(itemValueX.getChildren()))
                .hasParent(itemValueX)
                .hasFieldName("value")
                .hasTargetClass(String.class)
                .hasNoChildren();

        final String itemValueYField = "itemValueY";
        final InternalNode itemValueY = assertNode(NodeUtils.getChildNode(rootNode, itemValueYField))
                .hasParent(rootNode)
                .hasFieldName(itemValueYField)
                .hasTargetClass(Item.class)
                .hasTypeMappedTo(Item.class, "K", "Y")
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1)
                .get();

        assertNode(getOnlyElement(itemValueY.getChildren()))
                .hasParent(itemValueY)
                .hasFieldName("value")
                .hasTargetClass(Long.class)
                .hasNoChildren();
    }
}