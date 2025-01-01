/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.internal.nodes;

import org.instancio.internal.RootType;
import org.instancio.internal.nodes.resolvers.NodeKindResolverFacade;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

class PredefinedNodeCreator {

    private final RootType rootType;
    private final NodeKindResolverFacade nodeKindResolverFacade;

    PredefinedNodeCreator(
            final RootType rootType,
            final NodeKindResolverFacade nodeKindResolverFacade) {

        this.rootType = rootType;
        this.nodeKindResolverFacade = nodeKindResolverFacade;
    }

    @Nullable
    InternalNode createFromTemplate(@NotNull final Type type,
                                    @Nullable final Member member,
                                    @Nullable final InternalNode parent) {

        if (type == OptionalInt.class) {
            return createOptional(OptionalInt.class, int.class, member, parent);
        } else if (type == OptionalLong.class) {
            return createOptional(OptionalLong.class, long.class, member, parent);
        } else if (type == OptionalDouble.class) {
            return createOptional(OptionalDouble.class, double.class, member, parent);
        }

        return null;
    }

    /**
     * Creates {@link OptionalInt}, {@link OptionalLong}, or
     * {@link OptionalDouble}. These are container nodes because instances
     * of these types are created using a static factory method,
     * e.g. {@link OptionalInt#of(int)}. However, since they are not
     * parameterized types, the node factory would not be able to resolve
     * the child node. For this reason, the nodes are defined manually.
     */
    private InternalNode createOptional(
            @NotNull final Class<?> type,
            @NotNull final Class<?> childType,
            @Nullable final Member member,
            @Nullable final InternalNode parent) {

        final InternalNode result = InternalNode.builder(type, type, rootType)
                .nodeKind(NodeKind.CONTAINER)
                .member(member)
                .parent(parent)
                .build();

        result.setChildren(Collections.singletonList(
                InternalNode.builder(childType, childType, rootType)
                        .nodeKind(nodeKindResolverFacade.getNodeKind(childType))
                        .parent(result)
                        .build()));

        return result;
    }
}
