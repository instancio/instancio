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
import org.instancio.internal.selectors.InternalSelector;
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
    private final Set<SelectorEntry<V>> selectorEntries = new SortedSetWithReverseInsertionOrder<>(
            Comparator.comparingInt(o -> o.selector.getPriority()));

    @Override
    public void forEach(final BiConsumer<TargetSelector, ? super V> action) {
        for (SelectorEntry<V> entry : selectorEntries) {
            action.accept(entry.selector, entry.value);
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
        for (SelectorEntry<V> entry : selectorEntries) {
            // continue even after finding the first match to mark all matching selectors as "used"
            if (isPredicateMatch(node, entry.selector) && result == null) {
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

        for (SelectorEntry<V> entry : selectorEntries) {
            if (isPredicateMatch(node, entry.selector)) {
                values.add(entry.value);
            }
        }

        return values;
    }

    @Override
    public List<V> getValues(final TargetSelector selector) {
        if (isEmpty) {
            return emptyList();
        }
        final List<V> results = new ArrayList<>();

        for (SelectorEntry<V> entry : selectorEntries) {
            if (entry.selector.equals(selector)) {
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
        for (SelectorEntry<V> entry : selectorEntries) {
            if (isPredicateMatch(node, entry.selector)) {
                results.add(entry.selector);
            }
        }
        return results;
    }

    private boolean isPredicateMatch(final InternalNode targetNode, final InternalSelector selector) {
        final PredicateSelectorImpl predicateSelector = (PredicateSelectorImpl) selector;
        // Verify predicate matches the node, including depth
        final boolean isMatch = predicateSelector.getNodePredicate().test(targetNode)
                && selectorScopesMatchNodeHierarchy(selector.getScopes(), targetNode);

        if (isMatch) {
            unusedSelectors.remove(selector);
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
        if (selectorEntries.isEmpty()) {
            return "SelectorMap{}";
        }

        final StringBuilder sb = new StringBuilder(1024)
                .append("SelectorMap{")
                .append(NL);

        for (SelectorEntry<V> it : selectorEntries) {
            sb.append("  ").append(it.selector).append('=').append(it.value).append(NL);
        }
        return sb.append('}').toString();
    }

    private record SelectorEntry<V>(InternalSelector selector, V value) {}
}
