/*
 * Copyright 2022-2026 the original author or authors.
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

import org.instancio.TypeToken;
import org.instancio.exception.InstancioTerminatingException;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeFactory;
import org.instancio.testsupport.fixtures.Fixtures;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ElementOfDescriptorTest {

    private static final NodeFactory NODE_FACTORY = new NodeFactory(Fixtures.modelContext());

    @Test
    void matchesThrowsWhenIndexedContainerRequiredButContainerIsNotIndexed() {
        final InternalNode setContainer = NODE_FACTORY.createRootNode(new TypeToken<Set<String>>() {}.get());

        final ElementOfDescriptor descriptor = new ElementOfDescriptor(
                /* containerPredicate      */ node -> true,
                /* containerScopes         */ Collections.emptyList(),
                /* elementIndexFilter      */ null,
                /* innerNodePredicate      */ null,
                /* containerWasRoot        */ false,
                /* requireIndexedContainer */ true,
                /* isUserOriginated        */ true,
                /* descriptionSuffix       */ "");

        final ElementFrameStack.Frame frame = new ElementFrameStack.Frame(setContainer, 0, 1);

        assertThatThrownBy(() -> descriptor.matches(setContainer, frame))
                .isExactlyInstanceOf(InstancioTerminatingException.class)
                .hasMessageContaining("Indexed container expected, but got:");
    }
}
