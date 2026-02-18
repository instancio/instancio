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
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

/**
 * For predicate selectors, depth can be specified as an {@code int}
 * or a {@code Predicate<Integer>}. This class hides the details
 * of how depth was specified.
 */
final class SelectorDepth {

    private final @Nullable Integer depth;
    private final Predicate<InternalNode> depthPredicate;

    SelectorDepth(final int depth) {
        this.depth = ApiValidator.validateDepth(depth);
        this.depthPredicate = toNodePredicate(d -> d == depth);
    }

    SelectorDepth(final Predicate<Integer> depthPredicate) {
        ApiValidator.notNull(depthPredicate, "Selector depth predicate must not be null");
        this.depth = null;
        this.depthPredicate = toNodePredicate(depthPredicate);
    }

    @SuppressWarnings(Sonar.FUNCTIONAL_INTERFACES_SHOULD_BE_SPECIALISED)
    private static Predicate<InternalNode> toNodePredicate(final Predicate<Integer> p) {
        return n -> p.test(n.getDepth());
    }

    @Nullable Integer getDepth() {
        return depth;
    }

    Predicate<InternalNode> getDepthPredicate() {
        return depthPredicate;
    }
}
