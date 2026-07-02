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
package org.instancio.internal.generation;

import org.instancio.TargetSelector;
import org.instancio.documentation.VisibleForTesting;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.selectors.ElementOfDescriptor;
import org.instancio.internal.selectors.PredicateSelectorImpl;
import org.instancio.internal.util.Sonar;
import org.jspecify.annotations.Nullable;
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
public class AssignmentObjectStore implements GenerationListener {
    private static final Logger LOG = LoggerFactory.getLogger(AssignmentObjectStore.class);
    private static final GeneratorResult UNRESOLVED_RESULT = GeneratorResult.unresolvedResult();

    // A map containing "scope" objects as keys, and a Map of destination
    // selectors to generated objects as values.
    private final Map<Object, Map<TargetSelector, GeneratorResult>> objectStore = new IdentityHashMap<>();
    private final Deque<Object> scopes = new ArrayDeque<>();

    // Persistent store for elementOf-priority destinations. Survives element scope
    // exits so a value generated outside an element scope can be consumed within it
    // (e.g. an origin in one container feeding a destination in a sibling/nested one)
    private final Map<TargetSelector, GeneratorResult> crossElementStore = new IdentityHashMap<>();

    private final ModelContext context;

    private boolean hasNewValues;

    private AssignmentObjectStore(final ModelContext context) {
        this.context = context;
        enterScope(); // root object's scope
    }

    public static AssignmentObjectStore create(final ModelContext context) {
        return context.getSelectorMaps().hasAssignments()
                ? new AssignmentObjectStore(context)
                : new NoopAssignmentObjectStore();
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

    /**
     * Clears cross-element values whose destination targets {@code containerNode},
     * called once a container instance has finished its elements. They have
     * already been consumed within this instance, so dropping them stops the next
     * instance of the <i>same</i> container from reusing a stale value instead of
     * waiting for its own origin. Values for a <i>different</i> (sibling or nested)
     * container don't match and are retained for when that container is generated.
     */
    public void clearCrossElementValuesFor(final InternalNode containerNode) {
        if (crossElementStore.isEmpty()) {
            return;
        }
        final Iterator<TargetSelector> it = crossElementStore.keySet().iterator();
        while (it.hasNext()) {
            final ElementOfDescriptor eod =
                    ((PredicateSelectorImpl) it.next()).getElementOfDescriptor();

            if (eod != null && eod.matchesContainer(containerNode)) {
                it.remove();
            }
        }
    }

    @Override
    public void objectCreated(final InternalNode node, final GeneratorResult result) {
        for (TargetSelector destination : context.getAssignmentDestinationSelectors(node)) {
            if (destination instanceof PredicateSelectorImpl ps && ps.isElementOfPriority()) {
                crossElementStore.put(destination, result);
                hasNewValues = true;
                LOG.trace("Added (cross-element) {} for {}", result, destination);
            } else {
                putValue(destination, result);
                LOG.trace("Added {} for {}", result, destination);
            }
        }
    }

    @Nullable
    public GeneratorResult getValue(final TargetSelector destination) {
        final GeneratorResult crossResult = crossElementStore.get(destination);
        if (crossResult != null) {
            return crossResult;
        }

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
    @SuppressWarnings({"NullAway", "DataFlowIssue", Sonar.ANNOTATE_PARAMETER_NULLABLE})
    @VisibleForTesting
    static final class NoopAssignmentObjectStore extends AssignmentObjectStore {
        NoopAssignmentObjectStore() { super(null); }
        @Override public boolean hasNewValues() { return false; }
        @Override public void enterScope() { /* no-op */ }
        @Override public void exitScope() { /* no-op */ }
        @Override public void clearCrossElementValuesFor(InternalNode containerNode) { /* no-op */ }
        @Override public void objectCreated(InternalNode node, GeneratorResult result) { /* no-op */ }
        @Override public GeneratorResult getValue(TargetSelector destination) { return UNRESOLVED_RESULT; }
    }
    //@formatter:on
}
