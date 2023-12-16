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
import org.instancio.test.support.pojo.generics.inheritance.GenericTypesWithInheritance.EntityWithId;
import org.instancio.test.support.pojo.generics.inheritance.GenericTypesWithInheritance.GenericPhoneHolderWithInheritance;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.PhoneType;
import org.instancio.test.support.pojo.person.PhoneWithType;
import org.instancio.test.support.tags.GenericsTag;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

@GenericsTag
class GenericPhoneHolderWithInheritanceNodeTest
        extends NodeTestTemplate<GenericPhoneHolderWithInheritance<PhoneWithType>> {

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasDepth(0)
                .hasTargetClass(GenericPhoneHolderWithInheritance.class)
                .hasTypeMappedTo(GenericPhoneHolderWithInheritance.class, "PHONE", PhoneWithType.class)
                .hasTypeMappedTo(EntityWithId.class, "ID", Long.class)
                .hasTypeMapWithSize(2)
                .hasChildrenOfSize(2);

        assertId(rootNode);
        assertPhone(rootNode);
    }

    private static void assertId(final InternalNode rootNode) {
        assertNode(NodeUtils.getChildNode(rootNode, "id"))
                .hasDepth(1)
                .hasParent(rootNode)
                .hasFieldName("id")
                .hasTargetClass(Long.class)
                .hasEmptyTypeMap()
                .hasNoChildren()
                .get();
    }

    private static void assertPhone(final InternalNode rootNode) {
        final InternalNode phoneWithType = assertNode(NodeUtils.getChildNode(rootNode, "phone"))
                .hasDepth(1)
                .hasParent(rootNode)
                .hasFieldName("phone")
                .hasSetterName("setPhone", Phone.class)
                .hasTargetClass(PhoneWithType.class)
                .hasChildrenOfSize(3)
                .get();

        assertNode(NodeUtils.getChildNode(phoneWithType, "countryCode"))
                .hasDepth(2)
                .hasParent(phoneWithType)
                .hasFieldName("countryCode")
                .hasSetterName("setCountryCode", String.class)
                .hasTargetClass(String.class)
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(phoneWithType, "number"))
                .hasDepth(2)
                .hasParent(phoneWithType)
                .hasFieldName("number")
                .hasSetterName("setNumber", String.class)
                .hasTargetClass(String.class)
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(phoneWithType, "phoneType"))
                .hasDepth(2)
                .hasParent(phoneWithType)
                .hasFieldName("phoneType")
                .hasSetterName("setPhoneType", PhoneType.class)
                .hasTargetClass(PhoneType.class)
                .hasNoChildren();
    }
}