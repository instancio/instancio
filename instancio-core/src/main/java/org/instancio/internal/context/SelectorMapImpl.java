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

import org.instancio.Scope;
import org.instancio.TargetSelector;
import org.instancio.internal.PrimitiveWrapperBiLookup;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.selectors.InternalSelector;
import org.instancio.internal.selectors.PredicateScopeImpl;
import org.instancio.internal.selectors.PredicateSelectorImpl;
import org.instancio.internal.selectors.PrimitiveAndWrapperSelectorImpl;
import org.instancio.internal.selectors.ScopeImpl;
import org.instancio.internal.selectors.ScopelessSelector;
import org.instancio.internal.selectors.SelectorImpl;
import org.instancio.internal.selectors.Target;
import org.instancio.internal.selectors.TargetClass;
import org.instancio.internal.selectors.TargetField;
import org.instancio.internal.selectors.TargetSetter;
import org.instancio.internal.util.Fail;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static org.instancio.internal.util.Constants.NL;

@SuppressWarnings({"PMD.GodClass", "PMD.ExcessiveImports"})
final class SelectorMapImpl<V> implements SelectorMap<V> {
    private static final boolean FIND_ONE_ONLY = true;

    // Root's target class is always null
    private static final ScopelessSelector SCOPELESS_ROOT = new ScopelessSelector(null);

    private final Map<ScopelessSelector, List<SelectorImpl>> scopelessSelectors = new LinkedHashMap<>(4);
    private final Map<TargetSelector, V> selectors = new LinkedHashMap<>(4);
    private final Set<TargetSelector> unusedSelectors = new LinkedHashSet<>(4);
    private boolean isEmpty = true;

    /**
     * Predicate selector precedence is based on priority (lower values are higher priority)
     * and insertion order (last added wins).
     */
    private final Set<PredicateSelectorEntry<V>> predicateSelectors = new SortedSetWithReverseInsertionOrder<>(
            Comparator.comparingInt(o -> o.predicateSelector.getPriority()));

    @Override
    public void forEach(final BiConsumer<TargetSelector, ? super V> action) {
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
        isEmpty = false;

        if (!((InternalSelector) targetSelector).isLenient()) {
            unusedSelectors.add(targetSelector);
        }

        if (targetSelector instanceof SelectorImpl selector) {
            final Target target = selector.getTarget();
            final ScopelessSelector scopeless = target.toScopelessSelector();

            selectors.put(selector, value);
            scopelessSelectors.computeIfAbsent(scopeless, selectorList -> new ArrayList<>(3)).add(selector);
        } else if (targetSelector instanceof PredicateSelectorImpl selector) {
            predicateSelectors.add(new PredicateSelectorEntry<>(selector, value));
        } else {
            // Should not be reachable because selectors should be processed
            // into expected types before being added to the selector map.
            throw Fail.withFataInternalError("Invalid selector type: %s", targetSelector.getClass().getName());
        }
    }

    @Override
    public Set<TargetSelector> getUnusedKeys() {
        return Collections.unmodifiableSet(unusedSelectors);
    }

    @Override
    public Optional<V> getValue(final InternalNode node) {
        if (isEmpty) {
            return Optional.empty();
        }
        final List<SelectorImpl> withParent = getSelectorsWithParent(node, getCandidates(node), FIND_ONE_ONLY);

        if (!withParent.isEmpty()) {
            final SelectorImpl selector = withParent.get(0);
            markUsed(selector);
            return Optional.ofNullable(this.selectors.get(selector));
        }

        return getPredicateSelectorMatch(node);
    }

    private Optional<V> getPredicateSelectorMatch(final InternalNode node) {
        for (PredicateSelectorEntry<V> entry : predicateSelectors) {
            if (isPredicateMatch(node, entry)) {
                return Optional.of(entry.value);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<V> getValues(final InternalNode node) {
        if (isEmpty) {
            return emptyList();
        }
        final List<SelectorImpl> selectorsWithParent = getSelectorsWithParent(node, getCandidates(node), !FIND_ONE_ONLY);
        final List<V> values = new ArrayList<>();

        for (SelectorImpl s : selectorsWithParent) {
            markUsed(s);
            values.add(this.selectors.get(s));
        }

        for (PredicateSelectorEntry<V> entry : predicateSelectors) {
            if (isPredicateMatch(node, entry)) {
                values.add(entry.value);
            }
        }

        return values;
    }

    @Override
    public boolean isEmpty() {
        return isEmpty;
    }

    @Override
    public List<V> getValues(final TargetSelector selector) {
        if (isEmpty) {
            return emptyList();
        }
        final List<V> results = new ArrayList<>();
        final V v = selectors.get(selector);
        if (v != null) {
            results.add(v);
        }

        for (PredicateSelectorEntry<V> entry : predicateSelectors) {
            if (entry.predicateSelector.equals(selector)) {
                results.add(entry.value);
            }
        }
        return results;
    }

    @Override
    public Set<TargetSelector> getSelectors(final InternalNode node) {
        if (isEmpty) {
            return Collections.emptySet();
        }
        final List<SelectorImpl> selectorsWithParent = getSelectorsWithParent(node, getCandidates(node), !FIND_ONE_ONLY);

        final Set<TargetSelector> results = new HashSet<>(selectorsWithParent);

        for (PredicateSelectorEntry<V> entry : predicateSelectors) {
            if (isPredicateMatch(node, entry)) {
                results.add(entry.predicateSelector);
            }
        }

        return results;
    }

    private boolean isPredicateMatch(final InternalNode targetNode, final PredicateSelectorEntry<?> entry) {
        final boolean isMatch = entry.predicateSelector.getNodePredicate().test(targetNode)
                // Predicate selector depth is captured as a Predicate<Integer>
                // and it is checked by getNodePredicate() above.
                // Therefore, passing null below
                && selectorScopesMatchNodeHierarchy(/*candidateDepth = */ null, entry.predicateSelector.getScopes(), targetNode);

        if (isMatch) {
            unusedSelectors.remove(entry.predicateSelector);
        }

        return isMatch;
    }

    private void markUsed(final SelectorImpl selector) {
        // Special treatment of convenience PrimitiveAndWrapper selectors such as Select.allInts(),
        // which contains all(Integer.class) and all(int.class). If we only
        // match one, consider the equivalent to be matched as well.
        if (selector.getParent() instanceof PrimitiveAndWrapperSelectorImpl) {
            final Class<?> equivalentType = PrimitiveWrapperBiLookup.getEquivalent(selector.getTargetClass());

            final SelectorImpl equivalent = selector.toBuilder(new TargetClass(requireNonNull(equivalentType)))
                    .build();

            unusedSelectors.remove(equivalent);
        }
        unusedSelectors.remove(selector);
    }

    /**
     * Returns candidate selectors for the given node.
     * The order of the returned selectors matters (last one wins).
     *
     * @param node to look up selectors for
     * @return list of candidate selectors
     */
    private List<SelectorImpl> getCandidates(final InternalNode node) {
        if (node.getParent() == null && scopelessSelectors.containsKey(SCOPELESS_ROOT)) {
            return Collections.singletonList(scopelessSelectors.get(SCOPELESS_ROOT).get(0));
        }

        final List<SelectorImpl> candidates = new ArrayList<>();

        // Class selectors
        candidates.addAll(scopelessSelectors.getOrDefault(new ScopelessSelector(node.getRawType()), emptyList()));

        // Setter selectors
        final Method setter = node.getSetter();
        if (setter != null) {
            final ScopelessSelector key = new ScopelessSelector(
                    setter.getDeclaringClass(), setter);

            candidates.addAll(scopelessSelectors.getOrDefault(key, emptyList()));
        }

        // Field selectors last as they have the highest precedence
        final Field field = node.getField();
        if (field != null) {
            final ScopelessSelector key = new ScopelessSelector(
                    field.getDeclaringClass(), field);

            candidates.addAll(scopelessSelectors.getOrDefault(key, emptyList()));
        }

        return candidates;
    }

    private static List<SelectorImpl> getSelectorsWithParent(
            final InternalNode targetNode,
            final List<SelectorImpl> candidates,
            final boolean findOneOnly) {

        if (candidates.isEmpty()) {
            return emptyList();
        }

        final List<SelectorImpl> results = new ArrayList<>(3);

        // Start from the end so that in case of overlaps last selector wins
        // (only matters if a single result is requested)
        for (int i = candidates.size() - 1; i >= 0; i--) {
            if (findOneOnly && !results.isEmpty()) {
                break;
            }

            final SelectorImpl candidate = candidates.get(i);
            if (selectorScopesMatchNodeHierarchy(candidate.getDepth(), candidate.getScopes(), targetNode)) {
                results.add(candidate);
            }
        }
        return results;
    }

    private static boolean selectorScopesMatchNodeHierarchy(
            @Nullable final Integer candidateDepth,
            final List<Scope> candidateScopes,
            final InternalNode targetNode) {

        if (candidateDepth != null && candidateDepth != targetNode.getDepth()) {
            return false;
        }
        if (candidateScopes.isEmpty()) {
            return true;
        }
        final Deque<Scope> deq = new ArrayDeque<>(candidateScopes);
        Scope scope = deq.removeLast();
        InternalNode node = targetNode;

        while (node != null) {
            final boolean scopeMatched = scope instanceof ScopeImpl
                    ? isRegularScopeMatch((ScopeImpl) scope, node)
                    : isPredicateScopeMatch((PredicateScopeImpl) scope, node);

            if (scopeMatched) {
                scope = deq.pollLast();

                if (scope == null) { // All scopes have been matched
                    return true;
                }

                // allow consecutive scopes to match the same node
                continue;
            }

            node = node.getParent();
        }
        return false;
    }

    private static boolean isPredicateScopeMatch(final PredicateScopeImpl scope, final InternalNode node) {
        return scope.getNodePredicate().test(node);
    }

    private static boolean isRegularScopeMatch(final ScopeImpl scope, final InternalNode node) {
        boolean matched = false;

        // For regular selectors, matching scope depth offers two implementation options:
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
        final Integer scopeDepth = scope.getDepth();
        if (scopeDepth == null || node.getDepth() >= scopeDepth) {

            if (scope.getTarget() instanceof TargetField) {
                final Field field = node.getField();
                matched = field != null
                          && Objects.equals(scope.getTargetClass(), field.getDeclaringClass())
                          && Objects.equals(scope.getField(), field);

            } else if (scope.getTarget() instanceof TargetSetter) {
                final Method setter = node.getSetter();
                final Class<?> parameterType = scope.getParameterType();
                matched = setter != null
                          && Objects.equals(scope.getTargetClass(), setter.getDeclaringClass())
                          && Objects.equals(scope.getMethodName(), setter.getName())
                          && Objects.equals(parameterType, setter.getParameterTypes()[0]);

            } else {
                matched = Objects.equals(node.getRawType(), scope.getTargetClass())
                          || Objects.equals(node.getTargetClass(), scope.getTargetClass());
            }
        }
        return matched;
    }

    @Override
    public String toString() {
        if (selectors.isEmpty() && predicateSelectors.isEmpty()) {
            return "SelectorMap{}";
        }

        final StringBuilder sb = new StringBuilder(1024)
                .append("SelectorMap{")
                .append(NL);
        for (Map.Entry<TargetSelector, V> entry : selectors.entrySet()) {
            sb.append("  [REGULAR] ").append(entry).append(NL);
        }
        for (PredicateSelectorEntry<V> it : predicateSelectors) {
            sb.append("  [PREDICATE] ").append(it.predicateSelector).append('=').append(it.value).append(NL);
        }
        return sb.append('}').toString();
    }

    private static final class PredicateSelectorEntry<V> {
        private final PredicateSelectorImpl predicateSelector;
        private final V value;

        private PredicateSelectorEntry(final PredicateSelectorImpl predicateSelector, final V value) {
            this.predicateSelector = predicateSelector;
            this.value = value;
        }
    }
}
