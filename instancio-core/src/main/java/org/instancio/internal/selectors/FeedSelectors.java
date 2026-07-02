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

import org.instancio.documentation.InternalApi;
import org.instancio.internal.ApiMethod;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Constants;

import java.util.Collections;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

@InternalApi
public final class FeedSelectors {

    // These selectors are internal (lenient and hidden), so their stack trace is never
    // reported; share one holder instead of allocating a Throwable per feed property.
    private static final Throwable STACK_TRACE_HOLDER = new Throwable();

    private static final class FeedSelector extends PredicateSelectorImpl {
        private FeedSelector(final Predicate<InternalNode> predicate, final String description) {
            super(ApiMethod.NONE,
                    Constants.SelectorPriority.FEED,
                    predicate,
                    Collections.emptyList(),
                    /* selectorDepth = */ null,
                    /* isLenient = */ true,
                    /* isHiddenFromVerboseOutput = */ true,
                    description,
                    /* target */ null,
                    STACK_TRACE_HOLDER);
        }

        @Override
        public String toString() {
            return String.format("%s.%s.lenient() [internal]",
                    FeedSelectors.class.getSimpleName(),
                    getApiInvocationDescription());
        }
    }

    public static InternalSelector forProperty(final InternalNode fieldNode) {
        return new FeedSelector(
                propertyPredicate(fieldNode),
                propertyDescription(fieldNode));
    }

    public static InternalSelector forElementProperty(
            final PredicateSelectorImpl feedSelector,
            final InternalNode fieldNode) {

        final ElementOfDescriptor feedDescriptor =
                requireNonNull(feedSelector.getElementOfDescriptor());

        final ElementOfDescriptor rebakedDescriptor = feedDescriptor.rebakedCopy(
                feedDescriptor.containerPredicate(),
                propertyPredicate(fieldNode));

        final String apiInvocationDescription = String.format("%s via %s",
                propertyDescription(fieldNode),
                feedSelector.getApiInvocationDescription());

        return PredicateSelectorImpl.builder()
                .apiMethod(ApiMethod.NONE)
                .priority(Constants.SelectorPriority.FEED)
                .elementOfDescriptor(rebakedDescriptor)
                .scopes(feedSelector.getScopes())
                .lenient()
                .hiddenFromVerboseOutput()
                .apiInvocationDescription(apiInvocationDescription)
                .stackTraceHolder(STACK_TRACE_HOLDER)
                .build();
    }

    private static Predicate<InternalNode> propertyPredicate(final InternalNode fieldNode) {
        final String fieldName = requireNonNull(fieldNode.getField()).getName();
        final InternalNode parent = fieldNode.getParent();

        return candidate -> candidate.getField() != null
                && fieldName.equals(candidate.getField().getName())
                && candidate.getParent() == parent; //NOPMD - reference equality is intended
    }

    private static String propertyDescription(final InternalNode fieldNode) {
        return String.format("forProperty(\"%s\")", requireNonNull(fieldNode.getField()).getName());
    }

    private FeedSelectors() {
        // non-instantiable
    }
}
