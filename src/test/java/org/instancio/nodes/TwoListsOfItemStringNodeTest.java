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
import org.instancio.internal.nodes.Node;
import org.instancio.pojo.collections.lists.TwoListsOfItemString;
import org.instancio.pojo.generics.basic.Item;
import org.instancio.testsupport.fixtures.Types;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.CollectionUtils;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

/**
 * Verifies 'visited' node logic doesn't preclude the second list from being processed.
 */
class TwoListsOfItemStringNodeTest extends NodeTestTemplate<TwoListsOfItemString> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasTargetClass(TwoListsOfItemString.class)
                .hasChildrenOfSize(2);

        assertListNode(rootNode, "list1");
        assertListNode(rootNode, "list2");
    }

    private void assertListNode(Node rootNode, String listField) {
        final CollectionNode list = assertNode(NodeUtils.getChildNode(rootNode, listField))
                .hasNoChildren()
                .hasGenericType(Types.LIST_ITEM_STRING.get())
                .getAs(CollectionNode.class);

        assertNode(list.getElementNode())
                .hasNullField()
                .hasTargetClass(Item.class)
                .hasChildrenOfSize(1);

        assertNode(CollectionUtils.getOnlyElement(list.getElementNode().getChildren()))
                .hasFieldName("value")
                .hasTargetClass(String.class)
                .hasNoChildren();
    }
}