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
import org.instancio.test.support.pojo.cyclic.ClassesWithCrossReferences;
import org.instancio.test.support.tags.CyclicTag;
import org.instancio.testsupport.templates.NodeTestTemplate;
import org.junit.jupiter.api.Disabled;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

@CyclicTag
@Disabled("This test was added to get a ballpark of how long it takes to create the nodes")
class ClassesWithCrossReferencesNodeTest extends NodeTestTemplate<ClassesWithCrossReferences> {

    @Override
    protected int withMaxDepth() {
        return 9;
    }

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode).isNotNull();
    }
}