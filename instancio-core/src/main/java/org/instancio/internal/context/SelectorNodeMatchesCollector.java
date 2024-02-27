/*
 * Copyright 2022-2024 the original author or authors.
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

import org.instancio.TargetSelector;
import org.instancio.internal.nodes.InternalNode;

import java.util.ArrayDeque;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

final class SelectorNodeMatchesCollector {

    private final SelectorMap<?> ignoredSelectorMap;
    private final SelectorMap<?> nullableSelectorMap;
    private final SelectorMap<?> onCompleteCallbackSelectorMap;
    private final SelectorMap<?> subtypeSelectorMap;
    private final SelectorMap<?> generatorSelectorMap;
    private final SelectorMap<?> assignmentOriginSelectorMap;
    private final SelectorMap<?> assignmentDestinationSelectorMap;

    private SelectorNodeMatchesCollector(final Builder builder) {
        ignoredSelectorMap = builder.ignoredSelectorMap;
        nullableSelectorMap = builder.nullableSelectorMap;
        onCompleteCallbackSelectorMap = builder.onCompleteCallbackSelectorMap;
        subtypeSelectorMap = builder.subtypeSelectorMap;
        generatorSelectorMap = builder.generatorSelectorMap;
        assignmentOriginSelectorMap = builder.assignmentOriginSelectorMap;
        assignmentDestinationSelectorMap = builder.assignmentDestinationSelectorMap;
    }

    public Map<ApiMethodSelector, Map<TargetSelector, Set<InternalNode>>> getNodeMatches(final InternalNode rootNode) {
        Map<ApiMethodSelector, Map<TargetSelector, Set<InternalNode>>> map = new EnumMap<>(ApiMethodSelector.class);
        Queue<InternalNode> queue = new ArrayDeque<>();
        queue.offer(rootNode);

        while (!queue.isEmpty()) {
            final InternalNode node = queue.poll();
            collectNodes(map, ApiMethodSelector.IGNORE, node, ignoredSelectorMap);
            collectNodes(map, ApiMethodSelector.WITH_NULLABLE, node, nullableSelectorMap);
            collectNodes(map, ApiMethodSelector.ON_COMPLETE, node, onCompleteCallbackSelectorMap);
            collectNodes(map, ApiMethodSelector.SUBTYPE, node, subtypeSelectorMap);
            collectNodes(map, ApiMethodSelector.GENERATE, node, generatorSelectorMap);
            collectNodes(map, ApiMethodSelector.ASSIGN_ORIGIN, node, assignmentOriginSelectorMap);
            collectNodes(map, ApiMethodSelector.ASSIGN_DESTINATION, node, assignmentDestinationSelectorMap);
            queue.addAll(node.getChildren());
        }
        return map;
    }

    private static void collectNodes(
            final Map<ApiMethodSelector, Map<TargetSelector, Set<InternalNode>>> resultsMap,
            final ApiMethodSelector method,
            final InternalNode node,
            final SelectorMap<?> selectorMap) {

        Map<TargetSelector, Set<InternalNode>> selectorNodeMap = resultsMap
                .computeIfAbsent(method, k -> new LinkedHashMap<>());

        final Set<TargetSelector> selectors = selectorMap.getSelectors(node);

        selectors.forEach(selector -> {
            Set<InternalNode> nodes = selectorNodeMap.computeIfAbsent(selector, k -> new LinkedHashSet<>());
            nodes.add(node);
        });
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private SelectorMap<?> ignoredSelectorMap;
        private SelectorMap<?> nullableSelectorMap;
        private SelectorMap<?> onCompleteCallbackSelectorMap;
        private SelectorMap<?> subtypeSelectorMap;
        private SelectorMap<?> generatorSelectorMap;
        private SelectorMap<?> assignmentOriginSelectorMap;
        private SelectorMap<?> assignmentDestinationSelectorMap;

        private Builder() {
        }

        public Builder ignoredSelectorMap(final SelectorMap<?> ignoredSelectorMap) {
            this.ignoredSelectorMap = ignoredSelectorMap;
            return this;
        }

        public Builder nullableSelectorMap(final SelectorMap<?> nullableSelectorMap) {
            this.nullableSelectorMap = nullableSelectorMap;
            return this;
        }

        public Builder onCompleteCallbackSelectorMap(final SelectorMap<?> onCompleteCallbackSelectorMap) {
            this.onCompleteCallbackSelectorMap = onCompleteCallbackSelectorMap;
            return this;
        }

        public Builder subtypeSelectorMap(final SelectorMap<?> subtypeSelectorMap) {
            this.subtypeSelectorMap = subtypeSelectorMap;
            return this;
        }

        public Builder generatorSelectorMap(final SelectorMap<?> generatorSelectorMap) {
            this.generatorSelectorMap = generatorSelectorMap;
            return this;
        }

        public Builder assignmentOriginSelectorMap(final SelectorMap<?> assignmentOriginSelectorMap) {
            this.assignmentOriginSelectorMap = assignmentOriginSelectorMap;
            return this;
        }

        public Builder assignmentDestinationSelectorMap(final SelectorMap<?> assignmentDestinationSelectorMap) {
            this.assignmentDestinationSelectorMap = assignmentDestinationSelectorMap;
            return this;
        }

        public SelectorNodeMatchesCollector build() {
            return new SelectorNodeMatchesCollector(this);
        }
    }
}
