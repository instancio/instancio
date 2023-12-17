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
import org.instancio.test.support.pojo.generics.basic.Pair;
import org.instancio.test.support.pojo.generics.container.PairContainer;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class PairContainerIntegerStringNodeTest extends NodeTestTemplate<PairContainer<Integer, String>> {

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasTargetClass(PairContainer.class)
                .hasChildrenOfSize(1);

        // Pair<X, Y> pairValue;
        final String pairValueFieldName = "pairValue";
        final InternalNode pairValue = assertNode(NodeUtils.getChildNode(rootNode, pairValueFieldName))
                .hasField(pairValueFieldName)
                .hasTargetClass(Pair.class)
                .hasTypeName("org.instancio.test.support.pojo.generics.basic.Pair<X, Y>")
                .hasTypeMappedTo(Pair.class, "L", "X")
                .hasTypeMappedTo(Pair.class, "R", "Y")
                .hasChildrenOfSize(2)
                .hasTypeMapWithSize(2)
                .get();

        assertNode(NodeUtils.getChildNode(pairValue, "left"))
                .hasTargetClass(Integer.class)
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(pairValue, "right"))
                .hasTargetClass(String.class)
                .hasNoChildren();
    }
}