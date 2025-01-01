/*
 * Copyright 2022-2025 the original author or authors.
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
import org.instancio.Scope;
import org.instancio.ScopeableSelector;
import org.instancio.TargetSelector;
import org.instancio.internal.ApiMethodSelector;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.Flattener;
import org.instancio.internal.util.ErrorMessageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

abstract class PredicateSelectorBuilderTemplate<T>
        implements SelectorBuilder, DepthSelector, DepthPredicateSelector, Flattener<TargetSelector> {

    private final List<Predicate<T>> predicates = new ArrayList<>(3);
    private final StringBuilder description = new StringBuilder(128);
    private ApiMethodSelector apiMethodSelector;
    private List<Scope> scopes;
    private boolean isLenient;

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
    public final SelectorBuilder apiMethodSelector(final ApiMethodSelector apiMethodSelector) {
        this.apiMethodSelector = apiMethodSelector;
        return this;
    }

    @Override
    public final ScopeableSelector atDepth(final int depth) {
        final PredicateSelectorImpl copy = build();
        return copy.toBuilder()
                .depth(depth)
                .build();
    }

    @Override
    public final ScopeableSelector atDepth(final Predicate<Integer> predicate) {
        final PredicateSelectorImpl copy = build();
        return copy.toBuilder()
                .depth(predicate)
                .build();
    }

    protected final void setLenient() {
        this.isLenient = true;
    }

    protected final void withScopes(final Scope... scopes) {
        ApiValidator.notNull(scopes, () -> ErrorMessageUtils.selectorNotNullErrorMessage(
                "scopes must not be null.",
                "within", description().toString(), new Throwable()));

        ApiValidator.doesNotContainNull(scopes, () -> ErrorMessageUtils.selectorNotNullErrorMessage(
                "scopes vararg must not contain null.",
                "within", description().toString(), new Throwable()));

        this.scopes = Arrays.asList(scopes);

        // do not append scopes to the description field (this is done by toString())
    }

    protected final Predicate<T> buildPredicate() {
        Predicate<T> predicate = Objects::nonNull;
        for (Predicate<T> p : predicates) {
            predicate = predicate.and(p);
        }
        return predicate;
    }

    @Override
    public final PredicateSelectorImpl build() {
        final PredicateSelectorImpl.Builder builder = createBuilder();
        if (scopes != null) {
            builder.scopes(scopes);
        }
        if (isLenient) {
            builder.lenient();
        }
        return builder
                .apiMethodSelector(apiMethodSelector)
                .apiInvocationDescription(description.toString())
                .build();
    }

    @Override
    public List<TargetSelector> flatten() {
        return Collections.singletonList(build());
    }

    @Override
    public final String toString() {
        return description.toString();
    }
}
