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
import org.instancio.test.support.pojo.generics.outermidinner.Inner;
import org.instancio.test.support.pojo.generics.outermidinner.ListOfOuterMidInnerString;
import org.instancio.test.support.pojo.generics.outermidinner.Mid;
import org.instancio.test.support.pojo.generics.outermidinner.Outer;
import org.instancio.testsupport.templates.NodeTestTemplate;

import java.util.List;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class ListOfOuterMidInnerStringNodeTest extends NodeTestTemplate<ListOfOuterMidInnerString> {

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasParent(null)
                .hasNullField()
                .hasTargetClass(ListOfOuterMidInnerString.class)
                .hasEmptyTypeMap()
                .hasChildrenOfSize(1);

        //  List<Outer<Mid<Inner<String>>>>
        final InternalNode rootListElement = assertRootListAndItsElement(rootNode);

        // List<T> outerList
        final InternalNode outerElement = assertOuterList(rootListElement);

        // List<T> midList
        final InternalNode midListElement = assertMidList(outerElement);

        // List<T> innerList
        assertInnerList(midListElement);
    }

    private InternalNode assertRootListAndItsElement(InternalNode rootNode) {
        final InternalNode rootList = assertNode(rootNode.getOnlyChild())
                .hasParent(rootNode)
                .hasField("rootList")
                .hasTargetClass(List.class)
                .hasTypeMappedTo(List.class, "E", "org.instancio.test.support.pojo.generics.outermidinner." +
                        "Outer<org.instancio.test.support.pojo.generics.outermidinner." +
                        "Mid<org.instancio.test.support.pojo.generics.outermidinner." +
                        "Inner<java.lang.String>>>")
                .hasTypeMapWithSize(1)
                .get();

        // Outer
        assertNode(rootList.getOnlyChild())
                .hasParent(rootList)
                .hasNullField()
                .hasTargetClass(Outer.class)
                .hasTypeMappedTo(Outer.class, "T", "org.instancio.test.support.pojo.generics.outermidinner." +
                        "Mid<org.instancio.test.support.pojo.generics.outermidinner." +
                        "Inner<java.lang.String>>")
                .hasTypeName("org.instancio.test.support.pojo.generics.outermidinner." +
                        "Outer<org.instancio.test.support.pojo.generics.outermidinner." +
                        "Mid<org.instancio.test.support.pojo.generics.outermidinner." +
                        "Inner<java.lang.String>>>")
                .hasChildrenOfSize(1);

        return rootList.getOnlyChild();
    }

    private InternalNode assertOuterList(InternalNode rootListElement) {
        final InternalNode outerList = assertNode(rootListElement.getOnlyChild())
                .hasParent(rootListElement)
                .hasField("outerList")
                .hasTargetClass(List.class)
                .get();

        assertNode(outerList.getOnlyChild())
                .hasParent(outerList)
                .hasNullField()
                .hasTargetClass(Mid.class)
                .hasTypeName("org.instancio.test.support.pojo.generics.outermidinner." +
                        "Mid<org.instancio.test.support.pojo.generics.outermidinner." +
                        "Inner<java.lang.String>>")
                .hasTypeMappedTo(Mid.class, "T", "org.instancio.test.support.pojo.generics.outermidinner." +
                        "Inner<java.lang.String>")
                .hasChildrenOfSize(1);

        return outerList.getOnlyChild();
    }

    private InternalNode assertMidList(InternalNode outerElement) {
        final InternalNode midList = assertNode(outerElement.getOnlyChild())
                .hasParent(outerElement)
                .hasField("midList")
                .hasTargetClass(List.class)
                .hasChildrenOfSize(1)
                .get();

        assertNode(midList.getOnlyChild())
                .hasParent(midList)
                .hasNullField()
                .hasTargetClass(Inner.class)
                .hasTypeMappedTo(Inner.class, "T", String.class)
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1);

        return midList.getOnlyChild();
    }

    private void assertInnerList(InternalNode midListElement) {
        final InternalNode innerList = assertNode(midListElement.getOnlyChild())
                .hasParent(midListElement)
                .hasField("innerList")
                .hasTargetClass(List.class)
                .hasChildrenOfSize(1)
                .get();

        assertNode(innerList.getOnlyChild())
                .hasParent(innerList)
                .hasNullField()
                .hasTargetClass(String.class)
                .hasEmptyTypeMap()
                .hasNoChildren();
    }
}