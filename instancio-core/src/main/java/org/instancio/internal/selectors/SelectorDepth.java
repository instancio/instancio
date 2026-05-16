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

import org.instancio.internal.ApiValidator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Sonar;

import java.util.function.Predicate;

final class SelectorDepth {

    private final Predicate<InternalNode> depthPredicate;
    private final String description; // used for selector descriptions/reporting

    SelectorDepth(final int depth) {
        ApiValidator.isTrue(depth >= 0, "depth must not be negative: %s", depth);
        this.depthPredicate = toNodePredicate(d -> d == depth);
        this.description = String.valueOf(depth);
    }

    SelectorDepth(final Predicate<Integer> depthPredicate) {
        ApiValidator.notNull(depthPredicate, "Selector depth predicate must not be null");
        this.depthPredicate = toNodePredicate(depthPredicate);
        this.description = "Predicate<Integer>";
    }

    @SuppressWarnings(Sonar.FUNCTIONAL_INTERFACES_SHOULD_BE_SPECIALISED)
    private static Predicate<InternalNode> toNodePredicate(final Predicate<Integer> p) {
        return n -> p.test(n.getDepth());
    }

    Predicate<InternalNode> getDepthPredicate() {
        return depthPredicate;
    }

    String getDescription() {
        return description;
    }
}
