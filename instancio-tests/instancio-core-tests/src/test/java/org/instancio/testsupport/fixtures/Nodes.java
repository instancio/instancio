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
package org.instancio.testsupport.fixtures;

import org.instancio.internal.context.BooleanSelectorMap;
import org.instancio.internal.context.SubtypeSelectorMap;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeContext;
import org.instancio.internal.nodes.NodeFactory;

import java.lang.reflect.Type;
import java.util.Collections;

public final class Nodes {

    public static InternalNode node(final Type type) {
        return nodeFactory().createRootNode(type);
    }

    public static NodeFactory nodeFactory() {
        return new NodeFactory(nodeContext());
    }

    public static NodeContext nodeContext() {
        return NodeContext.builder()
                .maxDepth(Integer.MAX_VALUE)
                .ignoredSelectorMap(new BooleanSelectorMap(Collections.emptySet()))
                .subtypeSelectorMap(new SubtypeSelectorMap(
                        Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap()))
                .assignmentOriginSelectors(new BooleanSelectorMap(Collections.emptySet()))
                .build();
    }

    public static NodeContext.Builder nodeContextBuilder() {
        return NodeContext.builder()
                .maxDepth(Integer.MAX_VALUE)
                .ignoredSelectorMap(new BooleanSelectorMap(Collections.emptySet()))
                .subtypeSelectorMap(new SubtypeSelectorMap(
                        Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap()))
                .assignmentOriginSelectors(new BooleanSelectorMap(Collections.emptySet()));
    }

    private Nodes() {
        // non-instantiable
    }
}
