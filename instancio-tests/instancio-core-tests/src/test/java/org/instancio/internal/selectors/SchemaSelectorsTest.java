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
package org.instancio.internal.selectors;

import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeFactory;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.testsupport.fixtures.Nodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.testsupport.utils.NodeUtils.getChildNode;

class SchemaSelectorsTest {

    private static final NodeFactory NODE_FACTORY = new NodeFactory(Nodes.nodeContext());

    @Test
    void verifyToString() {
        final InternalNode personNode = NODE_FACTORY.createRootNode(Person.class);

        assertThat(SchemaSelectors.forProperty(getChildNode(personNode, "age")))
                .hasToString("SchemaSelectors.forProperty(\"age\").lenient() [internal]");
    }
}
