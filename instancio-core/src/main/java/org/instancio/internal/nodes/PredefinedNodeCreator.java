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
package org.instancio.internal.nodes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

class PredefinedNodeCreator {

    private final NodeContext nodeContext;

    PredefinedNodeCreator(final NodeContext nodeContext) {
        this.nodeContext = nodeContext;
    }

    @Nullable
    InternalNode createFromTemplate(@NotNull final Type type,
                                    @Nullable final Field field,
                                    @Nullable final InternalNode parent) {

        if (type == OptionalInt.class) {
            return createOptional(OptionalInt.class, int.class, field, parent);
        } else if (type == OptionalLong.class) {
            return createOptional(OptionalLong.class, long.class, field, parent);
        } else if (type == OptionalDouble.class) {
            return createOptional(OptionalDouble.class, double.class, field, parent);
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
            final Class<?> type,
            final Class<?> childType,
            final Field field,
            final InternalNode parent) {

        final InternalNode result = InternalNode.builder()
                .nodeContext(nodeContext)
                .nodeKind(NodeKind.CONTAINER)
                .field(field)
                .type(type)
                .rawType(type)
                .targetClass(type)
                .parent(parent)
                .build();

        result.setChildren(Collections.singletonList(InternalNode.builder()
                .nodeContext(nodeContext)
                .nodeKind(NodeKind.DEFAULT)
                .type(childType)
                .rawType(childType)
                .targetClass(childType)
                .parent(result)
                .build()));

        return result;
    }
}
