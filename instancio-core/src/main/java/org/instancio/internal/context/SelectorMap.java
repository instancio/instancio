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

/**
 * A map that supports looking up a value for a given node, taking into account the node's parents.
 * <p>
 * The question this map answers is: given a node, which selector can be applied to it?
 * Since selectors can have scopes that are specified top-down, the lookup will traverse
 * the node tree and selector scopes to determine if a selector can be applied.
 *
 * @param <V> value type
 */
class SelectorMap<V> {

    private final Map<ScopelessSelector, List<SelectorImpl>> scopelessSelectors = new LinkedHashMap<>();
    private final Map<SelectorImpl, V> selectors = new LinkedHashMap<>();

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

    Optional<V> getValue(final Node node) {
        ScopelessSelector target = null;
        List<SelectorImpl> candidates = null;

        if (node.getField() != null) {
            target = new ScopelessSelector(node.getField().getDeclaringClass(), node.getField());
            candidates = scopelessSelectors.get(target);
        }

        if (candidates == null) {
            target = new ScopelessSelector(node.getTargetClass());
            candidates = scopelessSelectors.getOrDefault(target, Collections.emptyList());
        }

        if (!candidates.isEmpty()) {
            final Optional<TargetSelector> matchingSelector = getSelectorWithParent(node, candidates);
            final TargetSelector selector = matchingSelector.orElse(target.asSelector());
            return Optional.ofNullable(selectors.get(selector));
        }

        return Optional.empty();
    }

    private static Optional<TargetSelector> getSelectorWithParent(final Node targetNode, final List<SelectorImpl> candidates) {
        // Start from the end so that in case of overlaps last selector wins
        for (int i = candidates.size() - 1; i >= 0; i--) {
            final SelectorImpl candidate = candidates.get(i);
            if (candidate.getScopes().isEmpty()) {
                return Optional.of(candidate);
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

                if (scope == null) { // All scopes have been matched against node parents
                    return Optional.of(candidate);
                }
                node = node.getParent();
            }
        }
        return Optional.empty();
    }
}
