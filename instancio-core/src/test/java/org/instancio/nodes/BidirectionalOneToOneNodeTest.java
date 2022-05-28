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
import org.instancio.test.support.pojo.cyclic.BidirectionalOneToOne;
import org.instancio.test.support.tags.CyclicTag;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

@CyclicTag
class BidirectionalOneToOneNodeTest extends NodeTestTemplate<BidirectionalOneToOne.Parent> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasTargetClass(BidirectionalOneToOne.Parent.class)
                .hasChildrenOfSize(2);

        final Node child = assertNode(NodeUtils.getChildNode(rootNode, "child"))
                .hasParent(rootNode)
                .hasFieldName("child")
                .hasTargetClass(BidirectionalOneToOne.Child.class)
                .hasChildrenOfSize(2)
                .get();

        final Node parent = assertNode(NodeUtils.getChildNode(child, "parent"))
                .hasParent(child)
                .hasFieldName("parent")
                .hasTargetClass(BidirectionalOneToOne.Parent.class)
                .hasChildrenOfSize(1)
                .get();

        assertNode(parent.getOnlyChild())
                .hasParent(parent)
                .hasFieldName("parentName")
                .hasTargetClass(String.class)
                .hasNoChildren();
    }
}