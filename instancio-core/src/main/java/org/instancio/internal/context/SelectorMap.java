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
package org.instancio.internal.context;

import org.instancio.PredicateSelector;
import org.instancio.Scope;
import org.instancio.TargetSelector;
import org.instancio.internal.PrimitiveWrapperBiLookup;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.selectors.PredicateSelectorImpl;
import org.instancio.internal.selectors.PrimitiveAndWrapperSelectorImpl;
import org.instancio.internal.selectors.ScopeImpl;
import org.instancio.internal.selectors.ScopelessSelector;
import org.instancio.internal.selectors.SelectorImpl;
import org.instancio.internal.selectors.SelectorTargetKind;
import org.instancio.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

/**
 * A map that supports looking up values for a given node, taking into account the node's ancestors.
 * <p>
 * The question this map answers is: given a node, which selector(s) can be applied to it?
 * Since selectors can have scopes that are specified top-down, the lookup will traverse up
 * the node tree and selector scopes to determine if a selector can be applied.
 * <p>
 * Although this class is named a 'Map', it also contains a List of predicate selectors.
 * Since predicate matches can only be resolved by applying the predicate to a target,
 * a map cannot be used like with regular selectors.
 *
 * @param <V> value type
 */
final class SelectorMap<V> {
    private static final boolean FIND_ONE_ONLY = true;

    private final Map<ScopelessSelector, List<SelectorImpl>> scopelessSelectors = new LinkedHashMap<>();
    private final Map<? super TargetSelector, V> selectors = new LinkedHashMap<>();
    private final Set<? super TargetSelector> unusedSelectors = new LinkedHashSet<>();
    private final List<PredicateSelectorEntry<V>> predicateSelectors = new ArrayList<>();

    private static final class PredicateSelectorEntry<V> {
        private final PredicateSelectorImpl predicateSelector;
        private final V value;
        private boolean matched;

        private PredicateSelectorEntry(final PredicateSelectorImpl predicateSelector, final V value) {
            this.predicateSelector = predicateSelector;
            this.value = value;
        }
    }

    void put(final TargetSelector targetSelector, final V value) {
        if (targetSelector instanceof SelectorImpl) {
            final SelectorImpl selector = (SelectorImpl) targetSelector;
            final ScopelessSelector scopeless;

            if (selector.getSelectorTargetKind() == SelectorTargetKind.FIELD) {
                final Field field = ReflectionUtils.getField(selector.getTargetClass(), selector.getFieldName());
                scopeless = new ScopelessSelector(field.getDeclaringClass(), field);
            } else {
                scopeless = new ScopelessSelector(selector.getTargetClass());
            }

            selectors.put(selector, value);
            unusedSelectors.add(selector);
            scopelessSelectors.computeIfAbsent(scopeless, selectorList -> new ArrayList<>()).add(selector);
        } else if (targetSelector instanceof PredicateSelector) {
            final PredicateSelectorImpl selector = (PredicateSelectorImpl) targetSelector;
            predicateSelectors.add(new PredicateSelectorEntry<>(selector, value));
        }
    }

    public Set<? super TargetSelector> getUnusedKeys() {
        final Set<? super TargetSelector> unused = new HashSet<>(unusedSelectors);
        for (PredicateSelectorEntry<?> entry : predicateSelectors) {
            if (!entry.matched) {
                unused.add(entry.predicateSelector);
            }
        }
        return unused;
    }

    /**
     * Returns last value for given node (in the order values were added).
     * <p>
     * Regular selectors have higher precedence than predicate selectors.
     * Therefore, if a regular selector is found, it will be returned
     * even if there is a predicate selector that also matches the target node.
     *
     * @param node for which to look up the value
     * @return value for given node, if present
     */
    Optional<V> getValue(final Node node) {
        final Optional<V> value = getSelectorsWithParent(node, getCandidates(node), FIND_ONE_ONLY).stream()
                .findFirst()
                .map(this::markUsed)
                .map(selectors::get);

        if (value.isPresent()) {
            return value;
        }

        return getPredicateSelectorMatch(node);
    }

    private Optional<V> getPredicateSelectorMatch(final Node node) {
        PredicateSelectorEntry<V> classPredicate = null;

        // If there's a field predicate anywhere in the list, then return the first one found.
        // Otherwise, return the first class predicate.
        for (PredicateSelectorEntry<V> entry : predicateSelectors) {
            if (entry.predicateSelector.getSelectorTargetKind() == SelectorTargetKind.FIELD) {
                if (isPredicateMatch(node, entry)) {
                    entry.matched = true;
                    return Optional.of(entry.value);
                }
            } else if (classPredicate == null // we want to return the first one that matches
                    && entry.predicateSelector.getSelectorTargetKind() == SelectorTargetKind.CLASS
                    && isPredicateMatch(node, entry)) {
                classPredicate = entry;
            }
        }

        if (classPredicate != null) {
            classPredicate.matched = true;
            return Optional.of(classPredicate.value);
        }

        return Optional.empty();
    }

    /**
     * Returns all values for given node, including those matched by predicate selectors.
     *
     * @param node for which to look up the values
     * @return all values for given node, or an empty list if none found
     */
    List<V> getValues(final Node node) {
        final List<V> values = getSelectorsWithParent(node, getCandidates(node), !FIND_ONE_ONLY)
                .stream()
                .map(this::markUsed)
                .map(selectors::get)
                .collect(Collectors.toCollection(ArrayList::new));

        final List<V> valuesMatchingPredicates = new ArrayList<>();

        for (PredicateSelectorEntry<V> entry : predicateSelectors) {
            if (isPredicateMatch(node, entry)) {
                entry.matched = true;
                valuesMatchingPredicates.add(entry.value);
            }
        }

        values.addAll(valuesMatchingPredicates);
        return values;
    }

    private static boolean isPredicateMatch(final Node node, final PredicateSelectorEntry<?> entry) {
        return entry.predicateSelector.getSelectorTargetKind() == SelectorTargetKind.FIELD
                ? entry.predicateSelector.getFieldPredicate().test(node.getField())
                : entry.predicateSelector.getClassPredicate().test(node.getTargetClass());
    }

    private SelectorImpl markUsed(final SelectorImpl selector) {
        // Special treatment of convenience PrimitiveAndWrapper selectors such as Select.allInts(),
        // which contains all(Integer.class) and all(int.class). If we only
        // match one, consider the equivalent to be matched as well.
        if (selector.getParent() instanceof PrimitiveAndWrapperSelectorImpl) {
            final SelectorImpl equivalent = new SelectorImpl(
                    selector.getSelectorTargetKind(),
                    PrimitiveWrapperBiLookup.getEquivalent(selector.getTargetClass()),
                    selector.getFieldName(),
                    selector.getScopes(),
                    selector.getParent());

            unusedSelectors.remove(equivalent);
        }
        unusedSelectors.remove(selector);
        return selector;
    }

    /**
     * Returns candidate selectors for the given node, with class selectors first and field selectors last.
     *
     * @param node to look up selectors for
     * @return list of selectors
     */
    private List<SelectorImpl> getCandidates(final Node node) {
        final List<SelectorImpl> candidates = new ArrayList<>(
                scopelessSelectors.getOrDefault(new ScopelessSelector(node.getRawType()), Collections.emptyList()));

        if (node.getField() != null) {
            final List<SelectorImpl> fieldSelectors = scopelessSelectors.getOrDefault(
                    new ScopelessSelector(node.getField().getDeclaringClass(), node.getField()), Collections.emptyList());

            candidates.addAll(fieldSelectors);
        }
        return candidates;
    }

    private static List<SelectorImpl> getSelectorsWithParent(
            final Node targetNode,
            final List<SelectorImpl> candidates,
            final boolean findOneOnly) {

        if (candidates.isEmpty()) {
            return Collections.emptyList();
        }

        final List<SelectorImpl> results = new ArrayList<>();

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

    private static boolean selectorScopesMatchNodeHierarchy(final SelectorImpl candidate, final Node targetNode) {
        if (candidate.getScopes().isEmpty()) {
            return true;
        }
        final Deque<Scope> deq = new ArrayDeque<>(candidate.getScopes());
        ScopeImpl scope = (ScopeImpl) deq.removeLast();
        Node node = targetNode;

        while (node != null) {
            if (scope.getField() != null) {
                if (scope.getField().equals(node.getField())) {
                    scope = (ScopeImpl) deq.pollLast();
                }
            } else if (node.getRawType().equals(scope.getTargetClass())) {
                scope = (ScopeImpl) deq.pollLast();
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
}
