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
package org.instancio.internal;

import org.instancio.internal.nodes.InternalNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

public final class DelayedNodeQueue implements Iterable<DelayedNode> {

    private final Deque<DelayedNode> delayedNodes = new ArrayDeque<>();

    // Ideally record nodes would go into delayedNodeQueue
    // but that requires knowing the parent GeneratorResult
    private final Set<InternalNode> delayedRecordNodes = new HashSet<>();

    void addLast(final DelayedNode delayedNode) {
        delayedNodes.addLast(delayedNode);
    }

    DelayedNode removeFirst() {
        return delayedNodes.removeFirst();
    }

    void addRecord(final InternalNode node) {
        delayedRecordNodes.add(node);
    }

    void removeRecord(final InternalNode node) {
        delayedRecordNodes.remove(node);
    }

    int size() {
        return delayedNodes.size();
    }

    boolean isEmpty() {
        return delayedNodes.isEmpty();
    }

    boolean hasRecordNodes() {
        return !delayedRecordNodes.isEmpty();
    }

    public Stream<DelayedNode> stream() {
        return delayedNodes.stream();
    }

    @NotNull
    @Override
    public Iterator<DelayedNode> iterator() {
        return delayedNodes.iterator();
    }
}
