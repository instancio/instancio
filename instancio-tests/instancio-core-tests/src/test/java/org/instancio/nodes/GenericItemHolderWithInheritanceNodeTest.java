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

import org.instancio.internal.nodes.Node;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.generics.inheritance.GenericTypesWithInheritance.EntityWithId;
import org.instancio.test.support.pojo.generics.inheritance.GenericTypesWithInheritance.GenericItemHolderWithInheritance;
import org.instancio.test.support.pojo.person.PhoneType;
import org.instancio.test.support.pojo.person.PhoneWithType;
import org.instancio.test.support.tags.GenericsTag;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

@GenericsTag
class GenericItemHolderWithInheritanceNodeTest
        extends NodeTestTemplate<GenericItemHolderWithInheritance<PhoneWithType, Item<PhoneWithType>>> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasDepth(0)
                .hasTargetClass(GenericItemHolderWithInheritance.class)
                .hasTypeMappedTo(GenericItemHolderWithInheritance.class, "ITEM",
                        "org.instancio.test.support.pojo.generics.basic.Item<org.instancio.test.support.pojo.person.PhoneWithType>")
                .hasTypeMappedTo(GenericItemHolderWithInheritance.class, "VALUE", PhoneWithType.class)
                .hasTypeMappedTo(EntityWithId.class, "ID", Long.class)
                .hasTypeMapWithSize(3)
                .hasChildrenOfSize(2);

        assertId(rootNode);
        assertItem(rootNode);
    }

    private static void assertId(final Node rootNode) {
        assertNode(NodeUtils.getChildNode(rootNode, "id"))
                .hasDepth(1)
                .hasParent(rootNode)
                .hasFieldName("id")
                .hasTargetClass(Long.class)
                .hasEmptyTypeMap()
                .hasNoChildren()
                .get();
    }

    private static void assertItem(final Node rootNode) {
        final Node item = assertNode(NodeUtils.getChildNode(rootNode, "item"))
                .hasDepth(1)
                .hasParent(rootNode)
                .hasFieldName("item")
                .hasTargetClass(Item.class)
                .hasChildrenOfSize(1)
                .get();

        final Node phoneWithType = assertNode(NodeUtils.getChildNode(item, "value"))
                .hasDepth(2)
                .hasParent(item)
                .hasTargetClass(PhoneWithType.class)
                .hasFieldName("value")
                .hasTargetClass(PhoneWithType.class)
                .hasChildrenOfSize(3)
                .get();

        assertNode(NodeUtils.getChildNode(phoneWithType, "countryCode"))
                .hasDepth(3)
                .hasParent(phoneWithType)
                .hasFieldName("countryCode")
                .hasTargetClass(String.class)
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(phoneWithType, "number"))
                .hasDepth(3)
                .hasParent(phoneWithType)
                .hasFieldName("number")
                .hasTargetClass(String.class)
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(phoneWithType, "phoneType"))
                .hasDepth(3)
                .hasParent(phoneWithType)
                .hasFieldName("phoneType")
                .hasTargetClass(PhoneType.class)
                .hasNoChildren();
    }
}