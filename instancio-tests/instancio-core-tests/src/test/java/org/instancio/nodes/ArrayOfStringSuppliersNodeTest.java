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
import org.instancio.test.support.pojo.generics.arrayofsuppliers.ArrayOfStringSuppliers;
import org.instancio.test.support.pojo.generics.arrayofsuppliers.GenericArrayHolder;
import org.instancio.test.support.tags.GenericsTag;
import org.instancio.testsupport.templates.NodeTestTemplate;

import java.util.function.Supplier;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

@GenericsTag
class ArrayOfStringSuppliersNodeTest extends NodeTestTemplate<ArrayOfStringSuppliers> {

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasDepth(0)
                .hasTargetClass(ArrayOfStringSuppliers.class)
                .hasTypeMappedTo(GenericArrayHolder.class, "T", "java.util.function.Supplier<java.lang.String>")
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1);

        final InternalNode array = assertNode(rootNode.getOnlyChild())
                .hasDepth(1)
                .hasField("array")
                .hasTargetClass(Supplier[].class)
                .hasEmptyTypeMap()
                .hasChildrenOfSize(1)
                .get();

        assertNode(array.getOnlyChild())
                .hasDepth(2)
                .hasTargetClass(Supplier.class)
                .hasTypeMappedTo(Supplier.class, "T", String.class)
                .hasTypeMapWithSize(1)
                .hasNoChildren();
    }
}