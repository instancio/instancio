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
package org.instancio.internal.generation;

import org.instancio.TargetSelector;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.nodes.InternalNode;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A store for keeping track of generated values for destination
 * selectors of assignments.
 */
public class AssigmentObjectStore implements GenerationListener {
    private static final Logger LOG = LoggerFactory.getLogger(AssigmentObjectStore.class);
    private static final GeneratorResult EMPTY_RESULT = GeneratorResult.emptyResult();

    // A map containing "scope" objects as keys, and a Map of destination
    // selectors to generated objects as values.
    private final IdentityHashMap<Object, Map<TargetSelector, GeneratorResult>> objectStore = new IdentityHashMap<>();
    private final Deque<Object> scopes = new ArrayDeque<>();
    private final ModelContext context;

    private boolean hasNewValues;

    private AssigmentObjectStore(final ModelContext context) {
        this.context = context;
        enterScope(); // root object's scope
    }

    public static AssigmentObjectStore create(final ModelContext context) {
        return context.getSelectorMaps().hasAssignments()
                ? new AssigmentObjectStore(context)
                : new NoopAssigmentObjectStore();
    }

    public boolean hasNewValues() {
        final boolean tmp = hasNewValues;
        hasNewValues = false;
        return tmp;
    }

    public void enterScope() {
        final Object scope = new Object();
        scopes.addLast(scope);
    }

    public void exitScope() {
        final Object scope = scopes.removeLast();
        objectStore.remove(scope);
    }

    @Override
    public void objectCreated(final InternalNode node, final GeneratorResult result) {
        for (TargetSelector destination : context.getAssignmentDestinationSelectors(node)) {
            putValue(destination, result);
            LOG.trace("Added {} for {}", result, destination);
        }
    }

    public GeneratorResult getValue(final TargetSelector destination) {
        final Iterator<Object> iter = scopes.descendingIterator();

        while (iter.hasNext()) {
            final Object scope = iter.next();
            final Map<TargetSelector, GeneratorResult> destinationValues = objectStore.get(scope);

            if (destinationValues == null) {
                continue;
            }

            final GeneratorResult result = destinationValues.get(destination);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    private void putValue(final TargetSelector selector, final GeneratorResult generatedValue) {
        final Object scope = scopes.peekLast();
        final Map<TargetSelector, GeneratorResult> destinationValues =
                objectStore.computeIfAbsent(scope, k -> new IdentityHashMap<>());

        destinationValues.put(selector, generatedValue);
        hasNewValues = true;
    }

    //@formatter:off
    @VisibleForTesting
    static final class NoopAssigmentObjectStore extends AssigmentObjectStore {
        NoopAssigmentObjectStore() { super(null); }
        @Override public boolean hasNewValues() { return false; }
        @Override public void enterScope() { /* no-op */ }
        @Override public void exitScope() { /* no-op */ }
        @Override public void objectCreated(InternalNode node, GeneratorResult result) { /* no-op */ }
        @Override public GeneratorResult getValue(TargetSelector destination) { return EMPTY_RESULT; }
    }
    //@formatter:on
}
