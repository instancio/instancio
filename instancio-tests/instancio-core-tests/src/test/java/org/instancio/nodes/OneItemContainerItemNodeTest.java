/*
 * Copyright 2022-2025 the original author or authors.
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
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.generics.container.OneItemContainer;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class OneItemContainerItemNodeTest extends NodeTestTemplate<OneItemContainer<Item<String>>> {

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasTargetClass(OneItemContainer.class)
                .hasTypeMappedTo(OneItemContainer.class, "T", "org.instancio.test.support.pojo.generics.basic.Item<java.lang.String>")
                .isOfKind(NodeKind.POJO)
                .hasChildrenOfSize(1);

        final String itemField = "item";
        final InternalNode item = assertNode(NodeUtils.getChildNode(rootNode, itemField))
                .hasParent(rootNode)
                .hasField(itemField)
                .hasTargetClass(Item.class)
                .hasTypeMappedTo(Item.class, "K", "T")
                .isOfKind(NodeKind.POJO)
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1)
                .get();

        final InternalNode nestedItem = assertNode(item.getOnlyChild())
                .hasParent(item)
                .hasField("value")
                .hasTargetClass(Item.class)
                .hasTypeMappedTo(Item.class, "K", String.class)
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1)
                .get();

        assertNode(nestedItem.getOnlyChild())
                .hasParent(nestedItem)
                .hasField("value")
                .hasTargetClass(String.class)
                .isOfKind(NodeKind.JDK)
                .hasEmptyTypeMap()
                .hasNoChildren();
    }
}