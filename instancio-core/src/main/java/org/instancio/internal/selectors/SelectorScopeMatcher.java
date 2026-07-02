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
package org.instancio.internal.selectors;

import org.instancio.Scope;
import org.instancio.internal.nodes.InternalNode;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Shared scope-matching logic used by regular selector maps and elementOf selectors.
 */
public final class SelectorScopeMatcher {

    private SelectorScopeMatcher() {
        // non-instantiable
    }

    public static boolean matches(
            final List<Scope> candidateScopes,
            final InternalNode targetNode) {

        return matches(candidateScopes, targetNode, null);
    }

    public static boolean matches(
            final List<Scope> candidateScopes,
            final InternalNode targetNode,
            final ElementFrameStack.@Nullable Frame frame) {

        if (candidateScopes.isEmpty()) {
            return true;
        }
        int index = candidateScopes.size() - 1;
        Scope scope = candidateScopes.get(index);
        InternalNode node = targetNode;

        while (node != null) {
            if (scopeMatches((PredicateScopeImpl) scope, node, frame)) {
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

    private static boolean scopeMatches(
            final PredicateScopeImpl scope,
            final InternalNode node,
            final ElementFrameStack.@Nullable Frame frame) {

        final ElementOfDescriptor elementOfDescriptor = scope.getElementOfDescriptor();
        return elementOfDescriptor == null
                ? scope.getNodePredicate().test(node)
                : elementOfDescriptor.matches(node, frame);
    }

}
