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

import org.instancio.TypeToken;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeKind;
import org.instancio.test.support.pojo.dynamic.DynAddress;
import org.instancio.test.support.pojo.dynamic.DynPerson;
import org.instancio.test.support.pojo.dynamic.DynPhone;
import org.instancio.testsupport.templates.NodeTestTemplate;

import java.util.List;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;
import static org.instancio.testsupport.utils.NodeUtils.getChildNode;

/**
 * Verifies a node hierarchy created from setter parameter types,
 * as the class under test has no fields corresponding to the setters.
 */
class DynPersonNodeTest extends NodeTestTemplate<DynPerson> {

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasTargetClass(DynPerson.class)
                .hasDepth(0)
                .hasChildrenOfSize(10);

        final InternalNode address = assertNode(getChildNode(rootNode, "setAddress", DynAddress.class))
                .hasDepth(1)
                .hasTargetClass(DynAddress.class)
                .hasNullField()
                .isOfKind(NodeKind.POJO)
                .hasChildrenOfSize(5)
                .get();

        final InternalNode phoneNumbersList = assertNode(getChildNode(address, "setPhoneNumbers", List.class))
                .hasDepth(2)
                .hasType(new TypeToken<List<DynPhone>>() {}.get())
                .hasTargetClass(List.class)
                .hasNullField()
                .isOfKind(NodeKind.COLLECTION)
                .hasChildrenOfSize(1)
                .get();

        final InternalNode phone = assertNode(phoneNumbersList.getOnlyChild())
                .hasDepth(3)
                .hasTargetClass(DynPhone.class)
                .hasNullField()
                .hasNullSetter()
                .isOfKind(NodeKind.POJO)
                .hasChildrenOfSize(3)
                .get();

        assertNode(getChildNode(phone, "setCountryCode", String.class))
                .hasDepth(4)
                .hasTargetClass(String.class)
                .hasNullField()
                .isOfKind(NodeKind.JDK)
                .hasNoChildren();

        assertNode(getChildNode(phone, "setNumber", String.class))
                .hasDepth(4)
                .hasTargetClass(String.class)
                .hasNullField()
                .isOfKind(NodeKind.JDK)
                .hasNoChildren();
    }
}