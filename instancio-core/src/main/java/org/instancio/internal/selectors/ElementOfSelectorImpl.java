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

import org.instancio.ElementOfSelector;
import org.instancio.GetMethodSelector;
import org.instancio.GroupableSelector;
import org.instancio.IndexedElementSelector;
import org.instancio.TargetSelector;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.Flattener;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class ElementOfSelectorImpl
        implements ElementOfSelector, IndexedElementSelector, Flattener<TargetSelector> {

    private final TargetSelector containerSelector;
    private final @Nullable ElementIndexFilter elementIndexFilter;
    private final @Nullable TargetSelector innerSelector;
    private final boolean isLenient;
    private final Throwable stackTraceHolder;

    public ElementOfSelectorImpl(final TargetSelector containerSelector) {
        this(containerSelector, null, null, false, new Throwable());
    }

    private ElementOfSelectorImpl(
            final TargetSelector containerSelector,
            final @Nullable ElementIndexFilter elementIndexFilter,
            final @Nullable TargetSelector innerSelector,
            final boolean isLenient,
            final Throwable stackTraceHolder) {

        this.containerSelector = containerSelector;
        this.elementIndexFilter = elementIndexFilter;
        this.innerSelector = innerSelector;
        this.isLenient = isLenient;
        this.stackTraceHolder = stackTraceHolder;
    }

    private IndexedElementSelector withIndexFilter(final ElementIndexFilter filter) {
        return new ElementOfSelectorImpl(containerSelector, filter, innerSelector, isLenient, stackTraceHolder);
    }

    @Override
    public IndexedElementSelector at(final int index) {
        return withIndexFilter(new ElementIndexFilter.At(index));
    }

    @Override
    public IndexedElementSelector at(final int... indices) {
        return withIndexFilter(new ElementIndexFilter.MultipleIndices(indices.clone()));
    }

    @Override
    public IndexedElementSelector except(final int... indices) {
        return withIndexFilter(new ElementIndexFilter.Except(indices.clone()));
    }

    @Override
    public IndexedElementSelector range(final int startInclusive, final int endInclusive) {
        return withIndexFilter(new ElementIndexFilter.Range(startInclusive, endInclusive));
    }

    @Override
    public IndexedElementSelector first() {
        return withIndexFilter(new ElementIndexFilter.First());
    }

    @Override
    public IndexedElementSelector last() {
        return withIndexFilter(new ElementIndexFilter.Last());
    }

    @Override
    public <T, R> GroupableSelector field(final GetMethodSelector<T, R> selector) {
        ApiValidator.notNull(selector, "'.field(selector)' selector must not be null");
        return target(selector);
    }

    @Override
    public GroupableSelector target(final TargetSelector selector) {
        ApiValidator.notNull(selector, "'.target(selector)' selector must not be null");
        ApiValidator.isFalse(selector instanceof ElementOfSelectorImpl,
                "'.target(...)' does not support a nested elementOf() selector");

        return new ElementOfSelectorImpl(containerSelector,
                elementIndexFilter, selector, isLenient, stackTraceHolder);
    }

    @Override
    public TargetSelector lenient() {
        return new ElementOfSelectorImpl(
                containerSelector, elementIndexFilter, innerSelector, true, stackTraceHolder);
    }

    public boolean isLenient() {
        return isLenient;
    }

    public Throwable getStackTraceHolder() {
        return stackTraceHolder;
    }

    @Override
    public List<TargetSelector> flatten() {
        return List.of(this);
    }

    public TargetSelector getContainerSelector() {
        return containerSelector;
    }

    @Nullable
    public ElementIndexFilter getElementIndexFilter() {
        return elementIndexFilter;
    }

    @Nullable
    public TargetSelector getInnerSelector() {
        return innerSelector;
    }

    String describeContainer(final String processedDescription) {
        return containerSelector instanceof GetMethodSelector<?, ?> getter
                ? MethodRef.describeGetter(getter)
                : processedDescription;
    }

    String describeSuffix(final @Nullable String innerDescription) {
        final StringBuilder sb = new StringBuilder();

        if (elementIndexFilter != null) {
            sb.append('.').append(elementIndexFilter);
        }
        if (innerSelector != null) {
            if (innerSelector instanceof GetMethodSelector<?, ?> getter) {
                sb.append(".field(").append(MethodRef.describeGetter(getter)).append(')');
            } else {
                sb.append(".target(").append(innerDescription).append(')');
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(128)
                .append("elementOf(")
                .append(describeContainer(String.valueOf(containerSelector)))
                .append(')')
                .append(describeSuffix(String.valueOf(innerSelector)));

        if (isLenient) {
            sb.append(".lenient()");
        }
        return sb.toString();
    }
}
