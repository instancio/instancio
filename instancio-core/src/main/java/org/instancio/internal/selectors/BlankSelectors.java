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

import org.instancio.documentation.InternalApi;
import org.instancio.internal.ApiMethodSelector;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeKind;
import org.instancio.internal.util.Constants;

import java.util.Collections;
import java.util.function.Predicate;

@InternalApi
public final class BlankSelectors {

    private static final InternalSelector ARRAY_SELECTOR = new BlankSelector(
            node -> node.is(NodeKind.ARRAY), "arraySelector()");

    private static final InternalSelector COLLECTION_SELECTOR = new BlankSelector(
            node -> node.is(NodeKind.COLLECTION), "collectionSelector()");

    private static final InternalSelector MAP_SELECTOR = new BlankSelector(
            node -> node.is(NodeKind.MAP), "mapSelector()");

    private static final InternalSelector LEAF_SELECTOR = new BlankSelector(
            node -> node.getChildren().isEmpty() && node.getParent() != null
                    && !node.getParent().is(NodeKind.ARRAY)
                    && !node.getParent().is(NodeKind.COLLECTION)
                    && !node.getParent().is(NodeKind.MAP),
            "leafSelector()");

    public static InternalSelector arraySelector() {
        return ARRAY_SELECTOR;
    }

    public static InternalSelector collectionSelector() {
        return COLLECTION_SELECTOR;
    }

    public static InternalSelector mapSelector() {
        return MAP_SELECTOR;
    }

    public static InternalSelector leafSelector() {
        return LEAF_SELECTOR;
    }

    private static final class BlankSelector extends PredicateSelectorImpl {
        private BlankSelector(final Predicate<InternalNode> predicate, final String description) {
            super(ApiMethodSelector.NONE,
                    Constants.BLANK_SELECTOR_PRIORITY,
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
                    BlankSelectors.class.getSimpleName(),
                    getApiInvocationDescription());
        }
    }

    private BlankSelectors() {
        // non-instantiable
    }
}