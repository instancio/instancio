/*
 * Copyright 2022-2024 the original author or authors.
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
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Baz;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.generics.foobarbaz.FooBarBazContainer;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class FooBarBazContainerNodeTest extends NodeTestTemplate<FooBarBazContainer> {

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasTargetClass(FooBarBazContainer.class)
                .hasDepth(0)
                .hasChildrenOfSize(1);

        final InternalNode itemNode = rootNode.getOnlyChild();
        assertNode(itemNode)
                .hasDepth(1)
                .hasTargetClass(Foo.class)
                .hasTypeMappedTo(Foo.class, "X", "org.instancio.test.support.pojo.generics.foobarbaz." +
                        "Bar<org.instancio.test.support.pojo.generics.foobarbaz." +
                        "Baz<java.lang.String>>")
                .hasTypeName("org.instancio.test.support.pojo.generics.foobarbaz." +
                        "Foo<org.instancio.test.support.pojo.generics.foobarbaz." +
                        "Bar<org.instancio.test.support.pojo.generics.foobarbaz." +
                        "Baz<java.lang.String>>>")
                .hasChildrenOfSize(2);

        final InternalNode fooValueNode = NodeUtils.getChildNode(itemNode, "fooValue");
        assertNode(fooValueNode)
                .hasDepth(2)
                .hasTargetClass(Bar.class)
                .hasTypeMappedTo(Bar.class, "Y", "org.instancio.test.support.pojo.generics.foobarbaz." +
                        "Baz<java.lang.String>")
                .hasTypeName("org.instancio.test.support.pojo.generics.foobarbaz." +
                        "Bar<org.instancio.test.support.pojo.generics.foobarbaz." +
                        "Baz<java.lang.String>>")
                .hasChildrenOfSize(2);

        assertNode(NodeUtils.getChildNode(itemNode, "otherFooValue"))
                .hasDepth(2)
                .hasTargetClass(Object.class)
                .hasNoChildren();

        final InternalNode barValueNode = NodeUtils.getChildNode(fooValueNode, "barValue");
        assertNode(barValueNode)
                .hasDepth(3)
                .hasTargetClass(Baz.class)
                .hasTypeMappedTo(Baz.class, "Z", String.class)
                .hasTypeName("org.instancio.test.support.pojo.generics.foobarbaz.Baz<java.lang.String>")
                .hasChildrenOfSize(1);

        assertNode(NodeUtils.getChildNode(fooValueNode, "otherBarValue"))
                .hasDepth(3)
                .hasTargetClass(Object.class)
                .hasNoChildren();

        assertNode(barValueNode.getOnlyChild())
                .hasDepth(4)
                .hasTargetClass(String.class)
                .hasTypeName("java.lang.String")
                .hasNoChildren();
    }
}