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
import org.instancio.internal.ApiMethod;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.selectors.ElementOfDescriptor;
import org.instancio.internal.selectors.PredicateSelectorImpl;
import org.instancio.internal.selectors.SelectorScopeMatcher;
import org.instancio.internal.selectors.TargetClass;
import org.instancio.internal.util.Constants;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

final class SetModelSelectorHelper {

    private final Class<?> rootClass;

    SetModelSelectorHelper(final Class<?> rootClass) {
        this.rootClass = rootClass;
    }

    /**
     * Creates a new selector by applying scopes from
     * {@code modelTarget} to {@code modelSelector}.
     *
     * <p>When {@code modelSelector} is an elementOf predicate, the closure
     * is rebaked to use the container derived from {@code modelTarget}
     * instead of the model's root.
     *
     * @param modelTarget   the model selector from {@code setModel(TargetSelector, Model)}
     * @param modelSelector a selector within the Model provided to {@code setModel()}
     * @return a copy of {@code modelSelector} with updated scopes / rebaked closure
     */
    TargetSelector applyModelSelectorScopes(
            final TargetSelector modelTarget,
            final TargetSelector modelSelector) {

        return applyModelSelectorScopes(modelTarget, modelSelector, false);
    }

    /**
     * Variant for assignment origins: when {@code isAssignmentOrigin}
     * is set and {@code modelTarget} is an elementOf predicate,
     * the origin is rebaked to fire only within the matching element frames.
     */
    TargetSelector applyModelSelectorScopes(
            final TargetSelector modelTarget,
            final TargetSelector modelSelector,
            final boolean isAssignmentOrigin) {

        final PredicateSelectorImpl internalTarget = (PredicateSelectorImpl) modelTarget;
        final PredicateSelectorImpl internalSelector = (PredicateSelectorImpl) modelSelector;

        if (internalSelector.isElementOfPriority()) {
            return rebakeElementOfSelector(internalTarget, internalSelector);
        }

        if (isAssignmentOrigin) {
            final ElementOfDescriptor eod = internalTarget.getElementOfDescriptor();
            if (eod != null && eod.isUserOriginated()) {
                final Predicate<InternalNode> innerNodePredicate = internalSelector.getNodePredicate();

                return buildRebakedSelector(
                        internalTarget.getApiMethod(),
                        "assignmentOrigin(" + internalTarget.getApiInvocationDescription() + ")",
                        internalTarget.getStackTraceHolder(),
                        eod.rebakedCopy(eod.containerPredicate(), innerNodePredicate),
                        false);
            }
        }

        // Default case: add scopes from modelTarget to modelSelector.
        final List<Scope> scopes = new ArrayList<>(4);
        scopes.addAll(internalTarget.getScopes());
        scopes.add(internalTarget.toScope());
        scopes.addAll(internalSelector.getScopes());

        if (internalSelector.isRootSelector()) {
            // Convert root() to a class selector restricted to the inner model's root type.
            return PredicateSelectorImpl.builder()
                    .target(new TargetClass(rootClass))
                    .priority(Constants.SelectorPriority.SET_MODEL)
                    .typePredicate(cls -> cls == rootClass)
                    .scopes(scopes)
                    .apiMethod(internalTarget.getApiMethod())
                    .build();
        }

        return internalSelector.within(scopes.toArray(new Scope[0]));
    }

    private TargetSelector rebakeElementOfSelector(
            final PredicateSelectorImpl modelTarget,
            final PredicateSelectorImpl ps) {

        final ElementOfDescriptor descriptor = Objects.requireNonNull(ps.getElementOfDescriptor(), "elementOfDescriptor");

        final Predicate<InternalNode> basePredicate = descriptor.containerWasRoot()
                ? modelTarget.getNodePredicate()
                : descriptor.containerPredicate();

        final List<Scope> outerScopes;
        if (!modelTarget.matchesViaElementFrame()) {
            outerScopes = new ArrayList<>(modelTarget.getScopes().size() + 1);
            outerScopes.addAll(modelTarget.getScopes());
            outerScopes.add(modelTarget.toScope());
        } else {
            outerScopes = modelTarget.getScopes();
        }

        final Predicate<InternalNode> newContainerPredicate =
                container -> basePredicate.test(container)
                        && SelectorScopeMatcher.matches(outerScopes, container);

        final String description = descriptor.containerWasRoot()
                ? descriptor.describe(modelTarget.getApiInvocationDescription())
                : ps.getApiInvocationDescription();

        final ElementOfDescriptor rebakedDescriptor = descriptor.rebakedCopy(
                newContainerPredicate, descriptor.innerNodePredicate());

        return buildRebakedSelector(
                modelTarget.getApiMethod(),
                description,
                ps.getStackTraceHolder(),
                rebakedDescriptor,
                ps.isLenient());
    }

    private static TargetSelector buildRebakedSelector(
            final @Nullable ApiMethod apiMethod,
            final String description,
            final Throwable stackTraceHolder,
            final ElementOfDescriptor elementOfDescriptor,
            final boolean lenient) {

        final PredicateSelectorImpl.Builder builder = PredicateSelectorImpl.builder()
                .apiMethod(apiMethod)
                .priority(Constants.SelectorPriority.ELEMENT_OF)
                .scopes(List.of())
                .apiInvocationDescription(description)
                .stackTraceHolder(stackTraceHolder)
                .elementOfDescriptor(elementOfDescriptor);

        if (lenient) {
            builder.lenient();
        }
        return builder.build();
    }
}
