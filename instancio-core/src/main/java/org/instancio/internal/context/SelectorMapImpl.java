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
package org.instancio.internal.context;

import org.instancio.TargetSelector;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.selectors.ElementFrameStack;
import org.instancio.internal.selectors.ElementOfDescriptor;
import org.instancio.internal.selectors.InternalSelector;
import org.instancio.internal.selectors.PredicateSelectorImpl;
import org.instancio.internal.selectors.SelectorScopeMatcher;
import org.instancio.internal.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import static org.instancio.internal.util.Constants.NL;

final class SelectorMapImpl<V> implements SelectorMap<V> {

    private final Set<TargetSelector> unusedSelectors = new LinkedHashSet<>(4);
    private boolean isEmpty = true;

    private final Set<SelectorEntry<V>> selectorEntries = new SortedSetWithReverseInsertionOrder<>(
            Comparator.comparingInt(o -> o.selector().getPriority()));

    private final ElementOfState elementOfState;
    private final ElementOfState.SelectorMapRole selectorMapRole;
    private final boolean staticElementOfMatching;

    private SelectorMapImpl(
            final ElementOfState elementOfState,
            final ElementOfState.SelectorMapRole selectorMapRole,
            final boolean staticElementOfMatching) {

        this.elementOfState = elementOfState;
        this.selectorMapRole = selectorMapRole;
        this.staticElementOfMatching = staticElementOfMatching;
    }

    /**
     * Returns a map for selectors that do not support {@code elementOf()},
     * e.g. {@code subtype()}; its elementOf state is never shared or read.
     */
    static <V> SelectorMap<V> withoutElementOfSupport() {
        return new SelectorMapImpl<>(new ElementOfState(), ElementOfState.SelectorMapRole.STANDARD, false);
    }

    /**
     * Returns a map sharing the given {@code elementOfState}, matching elementOf()
     * entries against the active element frame (the default, runtime matching).
     */
    static <V> SelectorMap<V> create(final ElementOfState elementOfState) {
        return new SelectorMapImpl<>(elementOfState, ElementOfState.SelectorMapRole.STANDARD, false);
    }

    /**
     * As {@link #create(ElementOfState)}, but with an explicit {@code selectorMapRole}
     * (e.g. {@link ElementOfState.SelectorMapRole#IGNORE} for {@code ignore()} entries).
     */
    static <V> SelectorMap<V> withRole(
            final ElementOfState elementOfState,
            final ElementOfState.SelectorMapRole selectorMapRole) {
        return new SelectorMapImpl<>(elementOfState, selectorMapRole, false);
    }

    /**
     * Returns a map that matches elementOf() entries statically against the node tree
     * (see {@link #staticElementOfMatching}) rather than against an active element frame,
     * for build-time consumers (e.g. feeds) that run before any frames exist.
     */
    static <V> SelectorMap<V> frameIndependent(final ElementOfState elementOfState) {
        return new SelectorMapImpl<>(elementOfState, ElementOfState.SelectorMapRole.STANDARD, true);
    }

    @Override
    public Iterator<SelectorEntry<V>> iterator() {
        return selectorEntries.iterator();
    }

    @Override
    public void forEach(final BiConsumer<TargetSelector, ? super V> action) {
        for (SelectorEntry<V> entry : selectorEntries) {
            action.accept(entry.selector(), entry.value());
        }
    }

    @Override
    public boolean isEmpty() {
        return isEmpty;
    }

    @Override
    public void put(final TargetSelector targetSelector, final V value) {
        isEmpty = false;

        final PredicateSelectorImpl selector = (PredicateSelectorImpl) targetSelector;
        selectorEntries.add(new SelectorEntry<>(selector, value));
        elementOfState.onPut(selector, selectorMapRole);

        if (!selector.isLenient()) {
            unusedSelectors.add(targetSelector);
        }
    }

    @Override
    public Set<TargetSelector> getUnusedKeys() {
        return Collections.unmodifiableSet(unusedSelectors);
    }

    @Override
    public void markSelectorUsed(final TargetSelector selector) {
        unusedSelectors.remove(selector);
    }

    @Override
    public Optional<V> getValue(final InternalNode node) {
        return getEntry(node, Integer.MAX_VALUE).map(SelectorEntry::value);
    }

    @Override
    public List<Match<V>> getMatches(final InternalNode node) {
        if (isEmpty) {
            return List.of();
        }
        final List<Match<V>> matches = new ArrayList<>();
        for (SelectorEntry<V> entry : selectorEntries) {
            if (isPredicateMatch(node, entry.selector())) {
                matches.add(new Match<>((PredicateSelectorImpl) entry.selector(), entry.value()));
            }
        }
        return matches;
    }

    private Optional<SelectorEntry<V>> getEntry(final InternalNode node, final int maxPriorityInclusive) {
        if (isEmpty) {
            return Optional.empty();
        }

        SelectorEntry<V> result = null;
        for (SelectorEntry<V> entry : selectorEntries) {
            if (entry.selector().getPriority() > maxPriorityInclusive) {
                break;
            }
            if (isPredicateMatch(node, entry.selector()) && result == null) {
                result = entry;
            }
        }
        return Optional.ofNullable(result);
    }

    @Override
    public List<V> getValues(final InternalNode node) {
        if (isEmpty) {
            return List.of();
        }
        final List<V> values = new ArrayList<>();

        for (SelectorEntry<V> entry : selectorEntries) {
            if (isPredicateMatch(node, entry.selector())) {
                values.add(entry.value());
            }
        }

        return values;
    }

    @Override
    public List<V> getValues(final TargetSelector selector) {
        if (isEmpty) {
            return List.of();
        }
        final List<V> results = new ArrayList<>();

        for (SelectorEntry<V> entry : selectorEntries) {
            if (entry.selector().equals(selector)) {
                results.add(entry.value());
            }
        }
        return results;
    }

    @Override
    public Set<TargetSelector> getSelectors(final InternalNode node) {
        if (isEmpty) {
            return Set.of();
        }

        final Set<TargetSelector> results = new HashSet<>();
        for (SelectorEntry<V> entry : selectorEntries) {
            if (isPredicateMatch(node, entry.selector())) {
                results.add(entry.selector());
            }
        }
        return results;
    }


    @Override
    public Optional<V> getActiveElementOfValue(final InternalNode node) {
        final Optional<SelectorEntry<V>> entry = getEntry(node, Constants.SelectorPriority.ELEMENT_OF);
        if (entry.isPresent()) {
            // Mark all lower-priority selectors that also match this node as "used"
            for (SelectorEntry<V> e : selectorEntries) {
                if (e.selector().getPriority() > Constants.SelectorPriority.ELEMENT_OF) {
                    isPredicateMatch(node, e.selector());
                }
            }
        }
        return entry.map(SelectorEntry::value);
    }

    private boolean isPredicateMatch(final InternalNode targetNode, final InternalSelector selector) {
        final PredicateSelectorImpl predicateSelector = (PredicateSelectorImpl) selector;
        final ElementOfDescriptor elementOfDescriptor = predicateSelector.getElementOfDescriptor();

        // The active frame is supplied to both elementOf descriptors and any elementOf-derived
        // scopes (e.g. a field selector scoped by scope(elementOf(list).at(0)) from setModel).
        // Regular selectors match on the node predicate (incl. depth) plus scopes.
        final ElementFrameStack.Frame frame = elementOfState.peekActiveFrame();
        final boolean isMatch;
        if (elementOfDescriptor == null) {
            isMatch = predicateSelector.getNodePredicate().test(targetNode)
                    && SelectorScopeMatcher.matches(predicateSelector.getScopes(), targetNode, frame);
        } else if (staticElementOfMatching) {
            isMatch = elementOfDescriptor.matchesStatically(targetNode)
                    && SelectorScopeMatcher.matches(predicateSelector.getScopes(), targetNode, frame);
        } else {
            isMatch = elementOfDescriptor.matches(targetNode, frame)
                    && SelectorScopeMatcher.matches(predicateSelector.getScopes(), targetNode, frame);
        }

        if (isMatch) {
            unusedSelectors.remove(predicateSelector);
        }

        return isMatch;
    }

    @Override
    public String toString() {
        if (selectorEntries.isEmpty()) {
            return "SelectorMap{}";
        }

        final StringBuilder sb = new StringBuilder(1024)
                .append("SelectorMap{")
                .append(NL);

        for (SelectorEntry<V> it : selectorEntries) {
            sb.append("  ").append(it.selector()).append('=').append(it.value()).append(NL);
        }
        return sb.append('}').toString();
    }
}
