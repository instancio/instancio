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
package org.instancio.internal;

import org.instancio.generator.GeneratorResult;
import org.instancio.internal.nodes.Node;
import org.instancio.util.Verify;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * A class for detecting cyclic relationships among objects.
 * <p>
 * The idea is to use an {@link IdentityHashMap} to map a created object
 * to an {@link AncestorTreeNode} containing the "parent" object and its node.
 * <p>
 * This allows to "walk up" the ancestors to check whether:
 * <ol>
 *   <li>the node that needs to created has occurred before, and if so</li>
 *   <li>whether the two nodes have the same parent</li>
 * </ol>
 * If both of these conditions are true, then there is a cycle.
 * <p>
 * This can be roughly illustrated as shown below, where {@code A} is the
 * current node to be created and {@code C} is its parent.
 * <pre>
 *     A -> B -> C -> A -> B -> C -> A
 *               ^^^^^^         ^^^^^^
 *                              cycle
 * </pre>
 */
class AncestorTree {

    /**
     * Maps object instance to its parent instance.
     */
    private final Map<Object, AncestorTreeNode> idMap = new IdentityHashMap<>();

    void setObjectAncestor(@Nullable final Object obj, final AncestorTreeNode ancestor) {
        Verify.isFalse(obj instanceof GeneratorResult, "Passed GeneratorResult to ancestor tree!"); // sanity check
        if (obj != null) {
            idMap.put(obj, ancestor);
        }
    }

    @SuppressWarnings("PMD.AccessorMethodGeneration")
    Object getObjectAncestor(@Nullable final Object obj, final Node nodeToCreate) {
        AncestorTreeNode ancestor = idMap.get(obj);

        while (ancestor != null) {
            if (ancestor.instance != null
                    && Objects.equals(nodeToCreate, ancestor.node)
                    && Objects.equals(nodeToCreate.getParent(), ancestor.node.getParent())) {
                return ancestor;
            }
            ancestor = idMap.get(ancestor.instance);
        }

        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(512);
        sb.append(getClass().getSimpleName()).append(" {\n");
        idMap.forEach((k, v) -> sb
                .append("  [").append(System.identityHashCode(k)).append("] ")
                .append(k.getClass().getSimpleName())
                .append(" -> ")
                .append(v == null ? "null" : v.getClass().getSimpleName())
                .append(" [").append(System.identityHashCode(v)).append("]\n"));
        return sb.append('}').toString();
    }


    static class AncestorTreeNode {
        private final Object instance;
        private final Node node;

        AncestorTreeNode(@Nullable final Object instance, final Node node) {
            this.instance = instance;
            this.node = node;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", AncestorTreeNode.class.getSimpleName() + "[", "]")
                    .add("instance=" + instance)
                    .add("node=" + node)
                    .toString();
        }
    }
}
