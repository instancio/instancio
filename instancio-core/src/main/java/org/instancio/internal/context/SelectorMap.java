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

import org.instancio.Scope;
import org.instancio.TargetSelector;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.selectors.ScopeImpl;
import org.instancio.internal.selectors.ScopelessSelector;
import org.instancio.internal.selectors.SelectorImpl;
import org.instancio.internal.selectors.SelectorTargetType;
import org.instancio.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * A map that supports looking up values for a given node, taking into account the node's ancestors.
 * <p>
 * The question this map answers is: given a node, which selector(s) can be applied to it?
 * Since selectors can have scopes that are specified top-down, the lookup will traverse up
 * the node tree and selector scopes to determine if a selector can be applied.
 *
 * @param <V> value type
 */
class SelectorMap<V> {
    private static final boolean FIND_ONE_ONLY = true;

    private final Map<ScopelessSelector, List<SelectorImpl>> scopelessSelectors = new LinkedHashMap<>();
    private final Map<? super TargetSelector, V> selectors = new LinkedHashMap<>();

    void put(final SelectorImpl selector, final V value) {
        final ScopelessSelector scopeless;

        if (selector.selectorType() == SelectorTargetType.FIELD) {
            final Field field = ReflectionUtils.getField(selector.getTargetClass(), selector.getFieldName());
            scopeless = new ScopelessSelector(field.getDeclaringClass(), field);
        } else {
            scopeless = new ScopelessSelector(selector.getTargetClass());
        }

        selectors.put(selector, value);
        scopelessSelectors.computeIfAbsent(scopeless, selectorList -> new ArrayList<>()).add(selector);
    }

    /**
     * Returns last value for given node (in the order values were added).
     *
     * @param node for which to look up the value
     * @return value for given node, if present
     */
    Optional<V> getValue(final Node node) {
        return getSelectorsWithParent(node, getCandidates(node), FIND_ONE_ONLY)
                .stream().findFirst().map(selectors::get);
    }

    /**
     * Returns all values for given node.
     *
     * @param node for which to look up the values
     * @return all values for given node, or an empty list if none found
     */
    List<V> getValues(final Node node) {
        return getSelectorsWithParent(node, getCandidates(node), !FIND_ONE_ONLY)
                .stream()
                .map(selectors::get)
                .collect(toList());
    }

    private List<SelectorImpl> getCandidates(final Node node) {
        List<SelectorImpl> candidates = null;
        if (node.getField() != null) {
            candidates = scopelessSelectors.get(new ScopelessSelector(node.getField().getDeclaringClass(), node.getField()));
        }
        if (candidates == null) {
            candidates = scopelessSelectors.get(new ScopelessSelector(node.getTargetClass()));
        }
        return candidates == null ? Collections.emptyList() : candidates;
    }

    private static List<TargetSelector> getSelectorsWithParent(
            final Node targetNode,
            final List<SelectorImpl> candidates,
            final boolean findOneOnly) {

        if (candidates.isEmpty()) {
            return Collections.emptyList();
        }

        final List<TargetSelector> results = new ArrayList<>();

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
            } else if (node.getTargetClass().equals(scope.getTargetClass())) {
                scope = (ScopeImpl) deq.pollLast();
            }

            if (scope == null) { // All scopes have been matched
                return true;
            }
            node = node.getParent();
        }
        return false;
    }
}
