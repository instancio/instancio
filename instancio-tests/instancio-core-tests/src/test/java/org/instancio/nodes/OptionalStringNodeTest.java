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
import org.instancio.test.support.pojo.misc.OptionalString;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import java.util.Optional;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class OptionalStringNodeTest extends NodeTestTemplate<OptionalString> {

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasTargetClass(OptionalString.class)
                .hasChildrenOfSize(1);

        final InternalNode optionalNode = assertNode(NodeUtils.getChildNode(rootNode, "optional"))
                .hasParent(rootNode)
                .hasFieldName("optional")
                .isOfKind(NodeKind.CONTAINER)
                .hasTargetClass(Optional.class)
                .hasTypeMappedTo(Optional.class, "T", String.class)
                .hasChildrenOfSize(1)
                .get();

        assertNode(optionalNode.getOnlyChild())
                .hasParent(optionalNode)
                .hasTargetClass(String.class)
                .isOfKind(NodeKind.JDK)
                .hasEmptyTypeMap()
                .hasNoChildren();
    }
}