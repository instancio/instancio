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
package org.instancio.internal;

import org.instancio.internal.nodes.InternalNode;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class DelayedNodeQueue {

    private final Deque<DelayedNode> delayedNodes = new ArrayDeque<>();

    // Ideally constructor nodes would go into delayedNodeQueue
    // but that requires knowing the parent GeneratorResult.
    // Unlike a DelayedNode, a node that could not be constructed has no
    // instantiated parent to be assigned into, so it cannot be resumed;
    // it is regenerated in full by whichever caller retries it.
    // Insertion order is preserved to keep error messages reproducible.
    private final Set<InternalNode> delayedConstructorNodes = new LinkedHashSet<>();

    void addLast(final DelayedNode delayedNode) {
        delayedNodes.addLast(delayedNode);
    }

    DelayedNode removeFirst() {
        return delayedNodes.removeFirst();
    }

    void addConstructorNode(final InternalNode node) {
        delayedConstructorNodes.add(node);
    }

    void removeConstructorNode(final InternalNode node) {
        delayedConstructorNodes.remove(node);
    }

    int size() {
        return delayedNodes.size();
    }

    boolean isEmpty() {
        return delayedNodes.isEmpty();
    }

    boolean hasConstructorNodes() {
        return !delayedConstructorNodes.isEmpty();
    }

    // Only used for error reporting
    public Stream<InternalNode> unresolvedNodes() {
        return Stream.concat(
                        delayedNodes.stream().map(DelayedNode::getNode),
                        delayedConstructorNodes.stream())
                .distinct();
    }
}
