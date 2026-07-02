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

import org.instancio.internal.ApiMethod;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Constants;
import org.instancio.internal.util.ErrorMessageUtils;
import org.instancio.internal.util.Fail;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

final class ElementOfSelectorBaker {

    private ElementOfSelectorBaker() {
        // non-instantiable
    }

    /**
     * Assembles already-processed components (container selector, inner selectors, index spec)
     * into baked {@link PredicateSelectorImpl}s: one per inner selector, or a single
     * whole-element selector when there are no inner selectors.
     */
    static List<PredicateSelectorImpl> bake(
            final ElementOfSelectorImpl eos,
            final ApiMethod apiMethod,
            final PredicateSelectorImpl containerPs,
            final List<PredicateSelectorImpl> innerSelectors) {

        if (innerSelectors.isEmpty()) {
            return List.of(buildElementSelector(eos, apiMethod, containerPs, null));
        }

        final List<PredicateSelectorImpl> results = new ArrayList<>(innerSelectors.size());

        for (PredicateSelectorImpl innerSelector : innerSelectors) {
            results.add(buildElementSelector(eos, apiMethod, containerPs, innerSelector));
        }
        return results;
    }

    private static PredicateSelectorImpl buildElementSelector(
            final ElementOfSelectorImpl eos,
            final ApiMethod apiMethod,
            final PredicateSelectorImpl containerPs,
            final @Nullable PredicateSelectorImpl innerSelector) {

        if (innerSelector != null) {
            verifyInnerSelectorWithinElementSubtree(eos, containerPs, innerSelector);
        }

        // assign() restricts elementOf to indexed containers (List/array); Set/Map have no
        // deterministic element ordering, so cross-element assignment on them can't work.
        final boolean requiresIndexedContainer = apiMethod == ApiMethod.ASSIGN_ORIGIN
                || apiMethod == ApiMethod.ASSIGN_DESTINATION;

        final Predicate<InternalNode> innerNodePredicate = innerSelector == null
                ? null // match whole element of list/array
                : innerSelector.getNodePredicate();

        final ElementOfDescriptor elementOfDescriptor = new ElementOfDescriptor(
                containerPs.getNodePredicate(),
                containerPs.getScopes(),
                eos.getElementIndexFilter(),
                innerNodePredicate,
                containerPs.isRootSelector(),
                requiresIndexedContainer, /* isUserOriginated */ true,
                eos.describeSuffix(innerSelector == null ? null : innerSelector.getApiInvocationDescription()));

        final PredicateSelectorImpl.Builder builder = PredicateSelectorImpl.builder()
                .apiMethod(apiMethod)
                .priority(Constants.SelectorPriority.ELEMENT_OF)
                .scopes(innerSelector == null ? Collections.emptyList() : innerSelector.getScopes())
                .apiInvocationDescription(elementOfDescriptor.describe(
                        eos.describeContainer(containerPs.getApiInvocationDescription())))
                .stackTraceHolder(eos.getStackTraceHolder())
                .elementOfDescriptor(elementOfDescriptor);

        if (eos.isLenient()) {
            builder.lenient();
        }
        return builder.build();
    }

    private static void verifyInnerSelectorWithinElementSubtree(
            final ElementOfSelectorImpl eos,
            final PredicateSelectorImpl containerPs,
            final PredicateSelectorImpl innerPs) {

        if (!(containerPs.getTarget() instanceof TargetField containerTf)
                || !(innerPs.getTarget() instanceof TargetField innerTf)) {
            return;
        }

        final Class<?> innerDeclaringClass = innerTf.getField().getDeclaringClass();
        if (innerDeclaringClass == containerTf.getField().getDeclaringClass()) {
            // Element type from the container field, used to name the element class in the error.
            final Class<?> elementType = extractContainerElementType(containerTf.getField());
            throw Fail.withUsageError(ErrorMessageUtils.elementOfInnerSelectorOutsideElementSubtree(
                    eos, innerDeclaringClass, elementType));
        }
    }

    @Nullable
    private static Class<?> extractContainerElementType(final Field field) {
        final Class<?> fieldType = field.getType();
        if (fieldType.isArray()) {
            return fieldType.getComponentType();
        }
        final Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType pt) {
            final Type[] args = pt.getActualTypeArguments();
            if (args.length > 0 && args[0] instanceof Class<?> cls) {
                return cls;
            }
        }
        return null;
    }
}
