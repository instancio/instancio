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
import org.instancio.pojo.inheritance.BaseClasSubClassInheritance;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class BaseClasSubClassInheritanceNodeTest extends NodeTestTemplate<BaseClasSubClassInheritance> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasTargetClass(BaseClasSubClassInheritance.class)
                .hasChildrenOfSize(1);

        final Node subClass = assertNode(NodeUtils.getChildNode(rootNode, "subClass"))
                .hasChildrenOfSize(3)
                .getAs(Node.class);

        // Subclass field
        assertNode(NodeUtils.getChildNode(subClass, "subClassField"))
                .hasNoChildren();

        // Superclass fields
        assertNode(NodeUtils.getChildNode(subClass, "privateBaseClassField"))
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(subClass, "protectedBaseClassField"))
                .hasNoChildren();

    }
}