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

import org.instancio.internal.nodes.ArrayNode;
import org.instancio.internal.nodes.Node;
import org.instancio.test.support.pojo.arrays.ArrayLong;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class ArrayLongNodeTest extends NodeTestTemplate<ArrayLong> {

    @Override
    protected void verify(Node rootNode) {
        final ArrayNode primitiveArray = assertNode(NodeUtils.getChildNode(rootNode, "primitive"))
                .hasTargetClass(long[].class)
                .hasChildrenOfSize(1)
                .getAs(ArrayNode.class);

        assertNode(primitiveArray.getElementNode())
                .hasTargetClass(long.class)
                .hasNullField();

        final ArrayNode wrapperArray = assertNode(NodeUtils.getChildNode(rootNode, "wrapper"))
                .hasTargetClass(Long[].class)
                .hasChildrenOfSize(1)
                .getAs(ArrayNode.class);

        assertNode(wrapperArray.getElementNode())
                .hasTargetClass(Long.class)
                .hasNullField();
    }
}