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
import org.instancio.internal.ApiMethodSelector;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Constants;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.Objects;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

@InternalApi
public final class FeedSelectors {

    private static final class FeedSelector extends PredicateSelectorImpl {
        private FeedSelector(final Predicate<@Nullable InternalNode> predicate, final String description) {
            super(ApiMethodSelector.NONE,
                    Constants.FEED_SELECTOR_PRIORITY,
                    predicate,
                    Collections.emptyList(),
                    /* selectorDepth = */ null,
                    /* isLenient = */ true,
                    /* isHiddenFromVerboseOutput = */ true,
                    description,
                    new Throwable());
        }

        @Override
        public String toString() {
            return String.format("%s.%s.lenient() [internal]",
                    FeedSelectors.class.getSimpleName(),
                    getApiInvocationDescription());
        }
    }

    public static InternalSelector forProperty(final InternalNode fieldNode) {
        final String fieldName = requireNonNull(fieldNode.getField()).getName();
        final String description = String.format("forProperty(\"%s\")", fieldName);

        return new FeedSelector(candidate ->
                candidate.getField() != null
                && Objects.equals(fieldName, candidate.getField().getName())
                && Objects.equals(fieldNode.getParent(), candidate.getParent()),
                description);
    }

    private FeedSelectors() {
        // non-instantiable
    }
}