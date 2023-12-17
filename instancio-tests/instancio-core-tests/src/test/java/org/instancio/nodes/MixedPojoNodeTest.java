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

import org.instancio.TypeToken;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeKind;
import org.instancio.test.support.pojo.dynamic.MixedPojo;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import java.util.Map;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class MixedPojoNodeTest extends NodeTestTemplate<MixedPojo> {

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasTargetClass(MixedPojo.class)
                .hasDepth(0)
                .hasChildrenOfSize(6);

        assertNode(NodeUtils.getChildNode(rootNode, "data"))
                .hasDepth(1)
                .hasType(new TypeToken<Map<String, Object>>() {}.get())
                .hasRawType(Map.class)
                .hasTargetClass(Map.class)
                .hasField("data")
                .hasNullSetter()
                .isOfKind(NodeKind.MAP)
                .hasChildrenOfSize(2); // key and value nodes

        assertNode(NodeUtils.getChildNode(rootNode, "regularField"))
                .hasDepth(1)
                .hasType(String.class)
                .hasRawType(String.class)
                .hasTargetClass(String.class)
                .hasField("regularField")
                .hasSetter("setRegularField", String.class)
                .isOfKind(NodeKind.JDK)
                .hasEmptyTypeMap()
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(rootNode, "regularFieldWithNoSetter"))
                .hasDepth(1)
                .hasType(String.class)
                .hasRawType(String.class)
                .hasTargetClass(String.class)
                .hasField("regularFieldWithNoSetter")
                .hasNullSetter()
                .isOfKind(NodeKind.JDK)
                .hasEmptyTypeMap()
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(rootNode, "regularFieldWithNonMatchingSetter"))
                .hasDepth(1)
                .hasType(String.class)
                .hasRawType(String.class)
                .hasTargetClass(String.class)
                .hasField("regularFieldWithNonMatchingSetter")
                .hasNullSetter()
                .isOfKind(NodeKind.JDK)
                .hasEmptyTypeMap()
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(rootNode, "setDynamicField", String.class))
                .hasDepth(1)
                .hasType(String.class)
                .hasRawType(String.class)
                .hasTargetClass(String.class)
                .hasNullField()
                .hasSetter("setDynamicField", String.class)
                .isOfKind(NodeKind.JDK)
                .hasEmptyTypeMap()
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(rootNode, "setFoo", String.class))
                .hasDepth(1)
                .hasType(String.class)
                .hasRawType(String.class)
                .hasTargetClass(String.class)
                .hasNullField()
                .hasSetter("setFoo", String.class)
                .isOfKind(NodeKind.JDK)
                .hasEmptyTypeMap()
                .hasNoChildren();
    }
}