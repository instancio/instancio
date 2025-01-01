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
import org.instancio.test.support.pojo.generics.inheritance.WithGenericParent;
import org.instancio.test.support.pojo.generics.inheritance.WithGenericParent.GenericChild;
import org.instancio.test.support.pojo.generics.inheritance.WithGenericParent.GenericGrandChild;
import org.instancio.test.support.pojo.generics.inheritance.WithGenericParent.GenericParent;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import java.time.Month;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class WithGenericParentNodeTest extends NodeTestTemplate<WithGenericParent> {

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasTargetClass(WithGenericParent.class)
                .hasEmptyTypeMap()
                .hasChildrenOfSize(3);

        assertGenericParent(NodeUtils.getChildNode(rootNode, "genericParent"));
        assertGenericChild(NodeUtils.getChildNode(rootNode, "genericChild"));
        assertGenericGrandChild(NodeUtils.getChildNode(rootNode, "genericGrandChild"));
    }

    private static void assertGenericParent(final InternalNode node) {
        assertNode(node)
                .hasField("genericParent")
                .hasTargetClass(GenericParent.class)
                .hasTypeMappedTo(GenericParent.class, "PARENT", Long.class)
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1);

        assertNode(node.getOnlyChild())
                .hasTargetClass(Long.class)
                .hasNoChildren();
    }

    private static void assertGenericChild(final InternalNode node) {
        assertNode(node)
                .hasField("genericChild")
                .hasTargetClass(GenericChild.class)
                .hasTypeMappedTo(GenericChild.class, "CHILD", String.class)
                .hasTypeMappedTo(GenericParent.class, "PARENT", GenericChild.class, "CHILD")
                .hasTypeMapWithSize(2)
                .hasChildrenOfSize(2);

        assertNode(NodeUtils.getChildNode(node, "parentValue"))
                .hasTargetClass(String.class)
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(node, "childValue"))
                .hasTargetClass(String.class)
                .hasNoChildren();
    }

    private void assertGenericGrandChild(final InternalNode node) {
        assertNode(node)
                .hasField("genericGrandChild")
                .hasTargetClass(GenericGrandChild.class)
                .hasTypeMappedTo(GenericGrandChild.class, "GRANDCHILD", Month.class)
                .hasTypeMappedTo(GenericChild.class, "CHILD", GenericGrandChild.class, "GRANDCHILD")
                .hasTypeMappedTo(GenericParent.class, "PARENT", GenericChild.class, "CHILD")
                .hasTypeMapWithSize(3)
                .hasChildrenOfSize(2);

        assertNode(NodeUtils.getChildNode(node, "parentValue"))
                .hasTargetClass(Month.class)
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(node, "childValue"))
                .hasTargetClass(Month.class)
                .hasNoChildren();

    }
}