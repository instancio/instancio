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
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.selectors.PredicateScopeImpl;
import org.instancio.internal.selectors.PredicateSelectorImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.instancio.internal.util.Constants.NL;

final class SelectorMapImpl<V> implements SelectorMap<V> {

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
        for (PredicateSelectorEntry<V> entry : predicateSelectors) {
            action.accept(entry.predicateSelector, entry.value);
        }
    }

    @Override
    public void put(final TargetSelector targetSelector, final V value) {
        isEmpty = false;

        final PredicateSelectorImpl selector = (PredicateSelectorImpl) targetSelector;
        predicateSelectors.add(new PredicateSelectorEntry<>(selector, value));

        if (!selector.isLenient()) {
            unusedSelectors.add(targetSelector);
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

        V result = null;
        for (PredicateSelectorEntry<V> entry : predicateSelectors) {
            // continue even after finding the first match to mark all matching selectors as "used"
            if (isPredicateMatch(node, entry) && result == null) {
                result = entry.value;
            }
        }
        return Optional.ofNullable(result);
    }

    @Override
    public List<V> getValues(final InternalNode node) {
        if (isEmpty) {
            return emptyList();
        }
        final List<V> values = new ArrayList<>();

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

        final Set<TargetSelector> results = new HashSet<>();
        for (PredicateSelectorEntry<V> entry : predicateSelectors) {
            if (isPredicateMatch(node, entry)) {
                results.add(entry.predicateSelector);
            }
        }
        return results;
    }

    private boolean isPredicateMatch(final InternalNode targetNode, final PredicateSelectorEntry<?> entry) {
        // Verify predicate matches the node, including depth
        final boolean isMatch = entry.predicateSelector.getNodePredicate().test(targetNode)
                && selectorScopesMatchNodeHierarchy(entry.predicateSelector.getScopes(), targetNode);

        if (isMatch) {
            unusedSelectors.remove(entry.predicateSelector);
        }

        return isMatch;
    }

    private static boolean selectorScopesMatchNodeHierarchy(
            final List<Scope> candidateScopes,
            final InternalNode targetNode) {

        if (candidateScopes.isEmpty()) {
            return true;
        }
        int index = candidateScopes.size() - 1;
        Scope scope = candidateScopes.get(index);
        InternalNode node = targetNode;

        while (node != null) {
            final boolean scopeMatched = ((PredicateScopeImpl) scope).getNodePredicate().test(node);

            if (scopeMatched) {
                if (--index < 0) { // All scopes have been matched
                    return true;
                }
                scope = candidateScopes.get(index);

                // allow consecutive scopes to match the same node
                continue;
            }

            node = node.getParent();
        }
        return false;
    }

    @Override
    public String toString() {
        if (predicateSelectors.isEmpty()) {
            return "SelectorMap{}";
        }

        final StringBuilder sb = new StringBuilder(1024)
                .append("SelectorMap{")
                .append(NL);

        for (PredicateSelectorEntry<V> it : predicateSelectors) {
            sb.append("  ").append(it.predicateSelector).append('=').append(it.value).append(NL);
        }
        return sb.append('}').toString();
    }

    private record PredicateSelectorEntry<V>(PredicateSelectorImpl predicateSelector, V value) {}
}
