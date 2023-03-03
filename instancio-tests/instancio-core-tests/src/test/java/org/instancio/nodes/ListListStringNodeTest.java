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
import org.instancio.test.support.pojo.collections.lists.ListListString;
import org.instancio.test.support.util.CollectionUtils;
import org.instancio.testsupport.fixtures.Types;
import org.instancio.testsupport.templates.NodeTestTemplate;

import java.util.List;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class ListListStringNodeTest extends NodeTestTemplate<ListListString> {

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasTargetClass(ListListString.class)
                .hasChildrenOfSize(1);

        // List<List<String>>
        final InternalNode outerListNode = assertNode(CollectionUtils.getOnlyElement(rootNode.getChildren()))
                .hasChildrenOfSize(1)
                .hasFieldName("nested")
                .hasType(Types.LIST_LIST_STRING.get())
                .get();

        // List<String>
        final InternalNode outerListElementNode = outerListNode.getOnlyChild();

        assertNode(outerListElementNode)
                .hasTargetClass(List.class)
                .hasNullField()
                .hasType(Types.LIST_STRING.get())
                .hasChildrenOfSize(1);

        // String
        assertNode(outerListElementNode.getOnlyChild())
                .hasTargetClass(String.class)
                .hasNullField()
                .hasNoChildren();
    }
}