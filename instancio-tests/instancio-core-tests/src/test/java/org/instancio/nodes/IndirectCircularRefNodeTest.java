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
import org.instancio.test.support.pojo.cyclic.IndirectCircularRef;
import org.instancio.test.support.tags.CyclicTag;
import org.instancio.testsupport.templates.NodeTestTemplate;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

@CyclicTag
class IndirectCircularRefNodeTest extends NodeTestTemplate<IndirectCircularRef> {

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasDepth(0)
                .hasTargetClass(IndirectCircularRef.class)
                .hasChildrenOfSize(1);

        final InternalNode startA = assertNode(rootNode.getOnlyChild())
                .hasDepth(1)
                .hasTargetClass(IndirectCircularRef.A.class)
                .hasFieldName("startA")
                .hasChildrenOfSize(1)
                .get();

        final InternalNode b = assertNode(startA.getOnlyChild())
                .hasDepth(2)
                .hasTargetClass(IndirectCircularRef.B.class)
                .hasFieldName("b")
                .hasChildrenOfSize(1)
                .get();

        final InternalNode c = assertNode(b.getOnlyChild())
                .hasDepth(3)
                .hasTargetClass(IndirectCircularRef.C.class)
                .hasFieldName("c")
                .hasChildrenOfSize(1)
                .get();

        assertNode(c.getOnlyChild())
                .hasDepth(4)
                .hasTargetClass(IndirectCircularRef.A.class)
                .hasFieldName("endA")
                .hasNoChildren();
    }
}