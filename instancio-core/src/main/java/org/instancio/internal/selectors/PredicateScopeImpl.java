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

import org.instancio.PredicateSelector;
import org.instancio.Scope;
import org.instancio.internal.nodes.InternalNode;

import java.util.function.Predicate;

public final class PredicateScopeImpl implements Scope {

    private final PredicateSelector predicateSelector;
    private final Predicate<InternalNode> nodePredicate;

    public PredicateScopeImpl(final PredicateSelector predicateSelector) {
        this.predicateSelector = predicateSelector;
        this.nodePredicate = ((PredicateSelectorImpl) predicateSelector).getNodePredicate();
    }

    public Predicate<InternalNode> getNodePredicate() {
        return nodePredicate;
    }

    @Override
    public String toString() {
        return String.format("scope(%s)", predicateSelector);
    }
}
