/*
 * Copyright 2022-2023 the original author or authors.
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

import org.instancio.DepthPredicateSelector;
import org.instancio.DepthSelector;
import org.instancio.PredicateSelector;
import org.instancio.TargetSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

abstract class PredicateSelectorBuilderTemplate<T>
        implements SelectorBuilder, DepthSelector, DepthPredicateSelector {

    private final List<Predicate<T>> predicates = new ArrayList<>(3);
    private final StringBuilder description = new StringBuilder(128);

    PredicateSelectorBuilderTemplate() {
        description.append(apiMethod());
    }

    protected abstract PredicateSelectorImpl.Builder createBuilder();

    protected abstract String apiMethod();

    protected final void addPredicate(Predicate<T> predicate) {
        predicates.add(predicate);
    }

    protected final StringBuilder description() {
        return description;
    }

    @Override
    public final TargetSelector atDepth(final int depth) {
        final PredicateSelectorImpl copy = (PredicateSelectorImpl) build();
        return PredicateSelectorImpl.builder(copy)
                .depth(depth)
                .build();
    }

    @Override
    public final TargetSelector atDepth(final Predicate<Integer> predicate) {
        final PredicateSelectorImpl copy = (PredicateSelectorImpl) build();
        return PredicateSelectorImpl.builder(copy)
                .depth(predicate)
                .build();
    }

    protected final Predicate<T> buildPredicate() {
        Predicate<T> predicate = Objects::nonNull;
        for (Predicate<T> p : predicates) {
            predicate = predicate.and(p);
        }
        return predicate;
    }

    @Override
    public final PredicateSelector build() {
        return createBuilder()
                .apiInvocationDescription(description.toString())
                .build();
    }

    @Override
    public final String toString() {
        return description.toString();
    }
}
