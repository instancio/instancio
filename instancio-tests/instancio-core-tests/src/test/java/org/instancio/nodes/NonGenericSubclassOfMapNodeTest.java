/*
 * Copyright 2022-2025 the original author or authors.
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
import org.instancio.test.support.pojo.generics.inheritance.NonGenericSubclassOfMap;
import org.instancio.testsupport.templates.NodeTestTemplate;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class NonGenericSubclassOfMapNodeTest extends NodeTestTemplate<NonGenericSubclassOfMap> {

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasTargetClass(NonGenericSubclassOfMap.class)
                .hasChildrenOfSize(2);

        assertNode(rootNode.getChildren().get(0))
                .hasTargetClass(String.class)
                .hasNullField()
                .hasNoChildren();

        assertNode(rootNode.getChildren().get(1))
                .hasTargetClass(Long.class)
                .hasNullField()
                .hasNoChildren();
    }
}