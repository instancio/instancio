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
import org.instancio.test.support.pojo.generics.PairAString;
import org.instancio.test.support.pojo.generics.basic.Pair;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import java.util.UUID;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class PairAStringNodeTest extends NodeTestTemplate<PairAString<UUID>> {

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasTargetClass(PairAString.class)
                .hasChildrenOfSize(1);

        // Pair<A, String>
        final String fieldName = "pairAString";

        final InternalNode pair = assertNode(NodeUtils.getChildNode(rootNode, fieldName))
                .hasParent(rootNode)
                .hasFieldName(fieldName)
                .hasTargetClass(Pair.class)
                .hasTypeMappedTo(Pair.class, "L", "A")
                .hasTypeMappedTo(Pair.class, "R", String.class)
                .hasTypeMapWithSize(2)
                .hasChildrenOfSize(2)
                .get();

        assertNode(NodeUtils.getChildNode(pair, "left"))
                .hasFieldName("left")
                .hasParent(pair)
                .hasTargetClass(UUID.class)
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(pair, "right"))
                .hasFieldName("right")
                .hasParent(pair)
                .hasTargetClass(String.class)
                .hasNoChildren();
    }
}