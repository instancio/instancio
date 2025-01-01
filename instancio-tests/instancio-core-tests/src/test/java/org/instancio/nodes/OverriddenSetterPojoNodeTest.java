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
import org.instancio.test.support.pojo.assignment.OverriddenSetterPojo;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class OverriddenSetterPojoNodeTest extends NodeTestTemplate<OverriddenSetterPojo.Child> {

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasTargetClass(OverriddenSetterPojo.Child.class)
                .hasDepth(0)
                .hasChildrenOfSize(3);

        assertNode(NodeUtils.getChildNode(rootNode, "childSetterValue"))
                .hasDepth(1)
                .hasTargetClass(String.class)
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(rootNode, "parentSetterValue"))
                .hasDepth(1)
                .hasTargetClass(String.class)
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(rootNode, "setValue", String.class))
                .hasDepth(1)
                .hasTargetClass(String.class)
                .hasNoChildren();

        // Note: Parent.setValue(String) method is overridden by Child.setValue(String).
        // For this reason, the parent method is ignored.
        // If the parent method was also included, Instancio would invoke both:
        //
        //  - Child.setValue(String)
        //  - Parent.setValue(String)
        //
        // Since the latter is overridden, it's effectively calling Child.setValue()
        // twice, which would lead to unexpected results for the user.
    }
}