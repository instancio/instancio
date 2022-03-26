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
package org.instancio.model;

import org.instancio.internal.model.ArrayNode;
import org.instancio.internal.model.CollectionNode;
import org.instancio.internal.model.Node;
import org.instancio.pojo.generics.container.GenericContainer;
import org.instancio.testsupport.templates.ModelTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import java.util.List;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class GenericContainerStringModelTest extends ModelTestTemplate<GenericContainer<String>> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(GenericContainer.class)
                .hasChildrenOfSize(3);

        // T value
        assertNode(NodeUtils.getChildNode(rootNode, "value"))
                .hasParent(rootNode)
                .hasFieldName("value")
                .hasKlass(String.class)
                .hasEmptyTypeMap() // TODO verify why
                //.hasTypeMappedTo(GenericContainer.class, "T", String.class)
                //.hasTypeMapWithSize(1)
                .hasNoChildren();

        // T[] array
        final ArrayNode array = assertNode(NodeUtils.getChildNode(rootNode, "array"))
                .hasParent(rootNode)
                .hasFieldName("array")
                .hasKlass(Object[].class)
                .hasEmptyTypeMap()  // TODO
                .hasNoChildren()
                .getAs(ArrayNode.class);

        assertNode(array.getElementNode())
                .hasParent(array)
                .hasNullField()
                .hasKlass(String.class)
                .hasEmptyTypeMap()
                .hasNoChildren();

        // List<T> list
        final CollectionNode list = assertNode(NodeUtils.getChildNode(rootNode, "list"))
                .hasParent(rootNode)
                .hasFieldName("list")
                .hasKlass(List.class)
                .hasTypeMappedTo(List.class, "E", "T")
                .hasTypeMapWithSize(1)
                .hasNoChildren()
                .getAs(CollectionNode.class);

        assertNode(list.getElementNode())
                .hasParent(list)
                .hasNullField()
                .hasKlass(String.class)
                .hasEmptyTypeMap()
                .hasNoChildren();
    }
}