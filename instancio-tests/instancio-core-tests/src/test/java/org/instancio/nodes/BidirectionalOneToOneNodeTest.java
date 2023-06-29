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
import org.instancio.test.support.pojo.cyclic.BidirectionalOneToOne;
import org.instancio.test.support.tags.CyclicTag;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

@CyclicTag
class BidirectionalOneToOneNodeTest extends NodeTestTemplate<BidirectionalOneToOne.Parent> {

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasDepth(0)
                .hasTargetClass(BidirectionalOneToOne.Parent.class)
                .hasChildrenOfSize(2);

        final InternalNode child = assertNode(NodeUtils.getChildNode(rootNode, "child"))
                .hasDepth(1)
                .hasParent(rootNode)
                .hasFieldName("child")
                .hasTargetClass(BidirectionalOneToOne.Child.class)
                .hasChildrenOfSize(2)
                .get();

        assertNode(NodeUtils.getChildNode(child, "childName"))
                .hasDepth(2)
                .hasParent(child)
                .hasFieldName("childName")
                .hasTargetClass(String.class)
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(child, "parent"))
                .hasDepth(2)
                .hasParent(child)
                .hasFieldName("parent")
                .hasTargetClass(BidirectionalOneToOne.Parent.class)
                .isCyclic()
                .hasNoChildren();
    }
}