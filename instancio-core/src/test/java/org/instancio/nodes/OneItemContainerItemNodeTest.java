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
import org.instancio.test.support.pojo.generics.container.OneItemContainer;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.test.support.util.CollectionUtils.getOnlyElement;
import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class OneItemContainerItemNodeTest extends NodeTestTemplate<OneItemContainer<Item<String>>> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasTargetClass(OneItemContainer.class)
                .hasTypeMappedTo(OneItemContainer.class, "T", "org.instancio.test.support.pojo.generics.basic.Item<java.lang.String>")
                .hasChildrenOfSize(1);

        final String itemField = "item";
        final Node item = assertNode(NodeUtils.getChildNode(rootNode, itemField))
                .hasParent(rootNode)
                .hasFieldName(itemField)
                .hasTargetClass(Item.class)
                .hasTypeMappedTo(Item.class, "K", "T")
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1)
                .getAs(Node.class);

        final Node nestedItem = assertNode(getOnlyElement(item.getChildren()))
                .hasParent(item)
                .hasFieldName("value")
                .hasTargetClass(Item.class)
                .hasTypeMappedTo(Item.class, "K", String.class)
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1)
                .getAs(Node.class);

        assertNode(getOnlyElement(nestedItem.getChildren()))
                .hasParent(nestedItem)
                .hasFieldName("value")
                .hasTargetClass(String.class)
                .hasEmptyTypeMap()
                .hasNoChildren();
    }
}