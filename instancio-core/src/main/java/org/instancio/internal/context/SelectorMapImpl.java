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
package org.instancio.internal.context;

import org.instancio.PredicateSelector;
import org.instancio.Scope;
import org.instancio.TargetSelector;
import org.instancio.internal.PrimitiveWrapperBiLookup;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.selectors.PredicateSelectorImpl;
import org.instancio.internal.selectors.PrimitiveAndWrapperSelectorImpl;
import org.instancio.internal.selectors.ScopeImpl;
import org.instancio.internal.selectors.ScopelessSelector;
import org.instancio.internal.selectors.SelectorImpl;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.Sonar;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import static java.util.stream.Collectors.joining;

@SuppressWarnings("PMD.GodClass")
final class SelectorMapImpl<V> implements SelectorMap<V> {
    private static final boolean FIND_ONE_ONLY = true;
    private static final SelectorMap<?> EMPTY_MAP = new EmptyMap<>();

    // Root's target class is always null
    private static final ScopelessSelector SCOPELESS_ROOT = new ScopelessSelector(null);

    private final Map<ScopelessSelector, List<SelectorImpl>> scopelessSelectors = new LinkedHashMap<>(0);
    private final Map<TargetSelector, V> selectors = new LinkedHashMap<>(0);
    private final Set<TargetSelector> unusedSelectors = new LinkedHashSet<>(0);

    /**
     * Predicate selector precedence is based on priority (lower values are higher priority)
     * and insertion order (last added wins).
     */
    private final Set<PredicateSelectorEntry<V>> predicateSelectors = new SortedSetWithReverseInsertionOrder<>(
            Comparator.comparingInt((o -> o.predicateSelector.getPriority())));

    @SuppressWarnings("unchecked")
    public static <T> SelectorMap<T> emptyMap() {
        return (SelectorMap<T>) EMPTY_MAP;
    }

    @Override
    public void forEach(final BiConsumer<TargetSelector, V> action) {
        for (Map.Entry<TargetSelector, V> entry : selectors.entrySet()) {
            final TargetSelector selector = entry.getKey();
            final V value = entry.getValue();

            action.accept(selector, value);
        }

        for (PredicateSelectorEntry<V> entry : predicateSelectors) {
            action.accept(entry.predicateSelector, entry.value);
        }
    }

    @Override
    public void put(final TargetSelector targetSelector, final V value) {
        if (targetSelector instanceof SelectorImpl) {
            final SelectorImpl selector = (SelectorImpl) targetSelector;

            final ScopelessSelector scopeless;

            if (selector.isRoot()) {
                scopeless = SCOPELESS_ROOT;
            } else if (selector.isFieldSelector()) {
                final Field field = ReflectionUtils.getField(selector.getTargetClass(), selector.getFieldName());
                scopeless = new ScopelessSelector(field.getDeclaringClass(), field);
            } else {
                scopeless = new ScopelessSelector(selector.getTargetClass());
            }

            selectors.put(selector, value);
            unusedSelectors.add(selector);
            scopelessSelectors.computeIfAbsent(scopeless, selectorList -> new ArrayList<>(3)).add(selector);
        } else if (targetSelector instanceof PredicateSelector) {
            final PredicateSelectorImpl selector = (PredicateSelectorImpl) targetSelector;
            predicateSelectors.add(new PredicateSelectorEntry<>(selector, value));
        } else {
            // Other types not expected because selectors should be
            // pre-processed by ModelContext being adding to the selector map
            throw Fail.withFataInternalError("Invalid selector type: " + targetSelector.getClass().getName());
        }
    }

    @Override
    public Set<TargetSelector> getUnusedKeys() {
        final Set<TargetSelector> unused = new HashSet<>(unusedSelectors);
        for (PredicateSelectorEntry<?> entry : predicateSelectors) {
            if (!entry.matched) {
                unused.add(entry.predicateSelector);
            }
        }
        return unused;
    }

    @Override
    public Optional<V> getValue(final InternalNode node) {
        final List<SelectorImpl> withParent = getSelectorsWithParent(node, getCandidates(node), FIND_ONE_ONLY);

        if (!withParent.isEmpty()) {
            final SelectorImpl selector = withParent.get(0);
            markUsed(selector);
            return Optional.of(this.selectors.get(selector));
        }

        return getPredicateSelectorMatch(node);
    }

    private Optional<V> getPredicateSelectorMatch(final InternalNode node) {
        for (PredicateSelectorEntry<V> entry : predicateSelectors) {
            if (isPredicateMatch(node, entry)) {
                entry.matched = true;
                return Optional.of(entry.value);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<V> getValues(final InternalNode node) {
        final List<SelectorImpl> selectorsWithParent = getSelectorsWithParent(node, getCandidates(node), !FIND_ONE_ONLY);
        final List<V> values = new ArrayList<>();

        for (SelectorImpl s : selectorsWithParent) {
            markUsed(s);
            values.add(this.selectors.get(s));
        }

        for (PredicateSelectorEntry<V> entry : predicateSelectors) {
            if (isPredicateMatch(node, entry)) {
                entry.matched = true;
                values.add(entry.value);
            }
        }

        return values;
    }

    @Override
    public Set<TargetSelector> getSelectors(final InternalNode node) {
        final List<SelectorImpl> selectorsWithParent = getSelectorsWithParent(node, getCandidates(node), !FIND_ONE_ONLY);

        final Set<TargetSelector> results = new HashSet<>(selectorsWithParent);

        for (PredicateSelectorEntry<V> entry : predicateSelectors) {
            if (isPredicateMatch(node, entry)) {
                results.add(entry.predicateSelector);
            }
        }

        return results;
    }

    private static boolean isPredicateMatch(final InternalNode node, final PredicateSelectorEntry<?> entry) {
        return entry.predicateSelector.getNodePredicate().test(node);
    }

    private void markUsed(final SelectorImpl selector) {
        // Special treatment of convenience PrimitiveAndWrapper selectors such as Select.allInts(),
        // which contains all(Integer.class) and all(int.class). If we only
        // match one, consider the equivalent to be matched as well.
        if (selector.getParent() instanceof PrimitiveAndWrapperSelectorImpl) {
            final SelectorImpl equivalent = selector.toBuilder()
                    .targetClass(PrimitiveWrapperBiLookup.getEquivalent(selector.getTargetClass()))
                    .build();

            unusedSelectors.remove(equivalent);
        }
        unusedSelectors.remove(selector);
    }

    /**
     * Returns candidate selectors for the given node, with class selectors first and field selectors last.
     *
     * @param node to look up selectors for
     * @return list of selectors
     */
    private List<SelectorImpl> getCandidates(final InternalNode node) {
        if (node.getParent() == null && scopelessSelectors.containsKey(SCOPELESS_ROOT)) {
            return Collections.singletonList(scopelessSelectors.get(SCOPELESS_ROOT).get(0));
        }

        final List<SelectorImpl> candidates = new ArrayList<>(
                scopelessSelectors.getOrDefault(new ScopelessSelector(node.getRawType()), Collections.emptyList()));

        if (node.getField() != null) {
            final ScopelessSelector key = new ScopelessSelector(
                    node.getField().getDeclaringClass(), node.getField());

            final List<SelectorImpl> fieldSelectors = scopelessSelectors.getOrDefault(
                    key, Collections.emptyList());

            candidates.addAll(fieldSelectors);
        }
        return candidates;
    }

    private static List<SelectorImpl> getSelectorsWithParent(
            final InternalNode targetNode,
            final List<SelectorImpl> candidates,
            final boolean findOneOnly) {

        if (candidates.isEmpty()) {
            return Collections.emptyList();
        }

        final List<SelectorImpl> results = new ArrayList<>(3);

        // Start from the end so that in case of overlaps last selector wins
        // (only matters if a single result is requested)
        for (int i = candidates.size() - 1; i >= 0; i--) {
            if (findOneOnly && !results.isEmpty()) {
                break;
            }

            final SelectorImpl candidate = candidates.get(i);
            if (selectorScopesMatchNodeHierarchy(candidate, targetNode)) {
                results.add(candidate);
            }
        }
        return results;
    }

    @SuppressWarnings(Sonar.COGNITIVE_COMPLEXITY_OF_METHOD)
    private static boolean selectorScopesMatchNodeHierarchy(
            final SelectorImpl candidate,
            final InternalNode targetNode) {

        if (candidate.getDepth() != null && candidate.getDepth() != targetNode.getDepth()) {
            return false;
        }
        if (candidate.getScopes().isEmpty()) {
            return true;
        }
        final Deque<Scope> deq = new ArrayDeque<>(candidate.getScopes());
        ScopeImpl scope = (ScopeImpl) deq.removeLast();
        InternalNode node = targetNode;

        while (node != null) {
            // Matching scope depth offers two implementation options:
            //
            // (A) node.getDepth() == scope.getDepth()
            // (B) node.getDepth() >= scope.getDepth()
            //
            // (A) would result in exact matching between scope and node depth values.
            // Although it seems like a good option and feels more intuitive, this approach
            // has two disadvantages:
            //
            //  1. The precise matching is actually more restrictive
            //  2. It's inconsistent with the semantics of within(Scope) API,
            //     i.e. "match anywhere _within_ given scope" (at given depth or further)
            if (scope.getDepth() == null || node.getDepth() >= scope.getDepth()) {

                if (scope.isFieldScope()) {
                    if (scope.resolveField().equals(node.getField())) {
                        scope = (ScopeImpl) deq.pollLast();
                    }
                } else if (node.getRawType().equals(scope.getTargetClass())) {
                    scope = (ScopeImpl) deq.pollLast();
                }
            }
            if (scope == null) { // All scopes have been matched
                return true;
            }

            node = node.getParent();
        }
        return false;
    }

    @Override
    public String toString() {
        if (selectors.isEmpty()) {
            return "SelectorMap{}";
        }
        return String.format("SelectorMap:{%n%s%n}", selectors.entrySet()
                .stream()
                .map(Object::toString)
                .collect(joining(System.lineSeparator())));
    }

    private static final class PredicateSelectorEntry<V> {
        private final PredicateSelectorImpl predicateSelector;
        private final V value;
        private boolean matched;

        private PredicateSelectorEntry(final PredicateSelectorImpl predicateSelector, final V value) {
            this.predicateSelector = predicateSelector;
            this.value = value;
        }
    }

    //@formatter:off
    private static class EmptyMap<V> implements SelectorMap<V> {
        @Override public void put(TargetSelector targetSelector, V value) {
            throw new UnsupportedOperationException("Unmodifiable SelectorMap");
        }
        @Override public void forEach(BiConsumer<TargetSelector, V> action) { /* no-op */ }
        @Override public Set<TargetSelector> getUnusedKeys() { return Collections.emptySet(); }
        @Override public Optional<V> getValue(InternalNode node) { return Optional.empty(); }
        @Override public List<V> getValues(InternalNode node) { return Collections.emptyList(); }
        @Override public Set<TargetSelector> getSelectors(InternalNode node) { return Collections.emptySet(); }
    }
    //@formatter:on
}
