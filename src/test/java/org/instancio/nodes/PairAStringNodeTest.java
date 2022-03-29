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

import org.instancio.internal.nodes.ClassNode;
import org.instancio.internal.nodes.Node;
import org.instancio.pojo.generics.PairAString;
import org.instancio.pojo.generics.basic.Pair;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import java.util.UUID;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class PairAStringNodeTest extends NodeTestTemplate<PairAString<UUID>> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(PairAString.class)
                .hasChildrenOfSize(1);

        // Pair<A, String>
        final String fieldName = "pairAString";

        final Node pair = assertNode(NodeUtils.getChildNode(rootNode, fieldName))
                .hasParent(rootNode)
                .hasFieldName(fieldName)
                .hasKlass(Pair.class)
                .hasTypeMappedTo(Pair.class, "L", "A")
                .hasTypeMappedTo(Pair.class, "R", String.class)
                .hasTypeMapWithSize(2)
                .hasChildrenOfSize(2)
                .getAs(ClassNode.class);

        assertNode(NodeUtils.getChildNode(pair, "left"))
                .hasFieldName("left")
                .hasParent(pair)
                .hasKlass(UUID.class)
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(pair, "right"))
                .hasFieldName("right")
                .hasParent(pair)
                .hasKlass(String.class)
                .hasNoChildren();
    }
}