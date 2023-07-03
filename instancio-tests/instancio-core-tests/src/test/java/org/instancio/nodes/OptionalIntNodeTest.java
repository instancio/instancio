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
import org.instancio.internal.nodes.NodeKind;
import org.instancio.testsupport.templates.NodeTestTemplate;

import java.util.OptionalInt;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class OptionalIntNodeTest extends NodeTestTemplate<OptionalInt> {

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasDepth(0)
                .isOfKind(NodeKind.CONTAINER)
                .hasTargetClass(OptionalInt.class)
                .hasType(OptionalInt.class)
                .hasRawType(OptionalInt.class)
                .hasParent(null)
                .hasNullField()
                .hasEmptyTypeMap()
                .hasChildrenOfSize(1);

        assertNode(rootNode.getOnlyChild())
                .hasParent(rootNode)
                .isOfKind(NodeKind.DEFAULT)
                .hasTargetClass(int.class)
                .hasType(int.class)
                .hasRawType(int.class)
                .hasNullField()
                .hasNoChildren();
    }
}