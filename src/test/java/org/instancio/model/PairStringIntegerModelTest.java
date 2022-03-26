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

import org.instancio.internal.model.Node;
import org.instancio.pojo.generics.basic.Pair;
import org.instancio.testsupport.templates.ModelTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class PairStringIntegerModelTest extends ModelTestTemplate<Pair<String, Integer>> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(Pair.class)
                .hasTypeMappedTo(Pair.class, "L", String.class)
                .hasTypeMappedTo(Pair.class, "R", Integer.class)
                .hasTypeMapWithSize(2)
                .hasChildrenOfSize(2);

        // L left
        assertNode(NodeUtils.getChildNode(rootNode, "left"))
                .hasFieldName("left")
                .hasParent(rootNode)
                .hasKlass(String.class)
                .hasEmptyTypeMap()
                .hasNoChildren();

        // R right
        assertNode(NodeUtils.getChildNode(rootNode, "right"))
                .hasFieldName("right")
                .hasParent(rootNode)
                .hasKlass(Integer.class)
                .hasEmptyTypeMap()
                .hasNoChildren();
    }
}