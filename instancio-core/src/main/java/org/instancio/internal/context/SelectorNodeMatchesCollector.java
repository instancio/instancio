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
import org.instancio.internal.ApiMethodSelector;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.selectors.InternalSelector;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

final class SelectorNodeMatchesCollector {

    private final SelectorMap<?> assignDestinationToAssignmentsMap;
    private final SelectorMap<?> assignOriginToDestinationSelectorsMap;
    private final SelectorMap<?> feedSelectorMap;
    private final SelectorMap<?> filterSelectorMap;
    private final SelectorMap<?> generatorSelectorMap;
    private final SelectorMap<?> ignoredSelectorMap;
    private final SelectorMap<?> nullableSelectorMap;
    private final SelectorMap<?> onCompleteCallbackSelectorMap;
    private final SelectorMap<?> setModelSelectorMap;
    private final SelectorMap<?> subtypeSelectorMap;

    SelectorNodeMatchesCollector(final SelectorMaps selectorMaps) {
        this.assignDestinationToAssignmentsMap = selectorMaps.getAssignmentSelectorMap().getDestinationToAssignmentsMap();
        this.assignOriginToDestinationSelectorsMap = selectorMaps.getAssignmentSelectorMap().getOriginToDestinationSelectorsMap();
        this.feedSelectorMap = selectorMaps.getFeedSelectorMap().getSelectorMap();
        this.filterSelectorMap = selectorMaps.getFilterSelectorMap().getSelectorMap();
        this.generatorSelectorMap = selectorMaps.getGeneratorSelectorMap().getSelectorMap();
        this.ignoredSelectorMap = selectorMaps.getIgnoreSelectorMap().getSelectorMap();
        this.nullableSelectorMap = selectorMaps.getWithNullableSelectorMap().getSelectorMap();
        this.onCompleteCallbackSelectorMap = selectorMaps.getOnCompleteSelectorMap().getSelectorMap();
        this.setModelSelectorMap = selectorMaps.getSetModelSelectorMap().getSelectorMap();
        this.subtypeSelectorMap = selectorMaps.getSubtypeSelectorMap().getSelectorMap();
    }

    public Map<ApiMethodSelector, Map<TargetSelector, Set<InternalNode>>> getNodeMatches(final InternalNode rootNode) {
        Map<ApiMethodSelector, Map<TargetSelector, Set<InternalNode>>> map = new EnumMap<>(ApiMethodSelector.class);
        Queue<InternalNode> queue = new ArrayDeque<>();
        queue.offer(rootNode);

        while (!queue.isEmpty()) {
            final InternalNode node = queue.poll();
            collectNodes(map, ApiMethodSelector.APPLY_FEED, node, feedSelectorMap);
            collectNodes(map, ApiMethodSelector.ASSIGN_DESTINATION, node, assignDestinationToAssignmentsMap);
            collectNodes(map, ApiMethodSelector.ASSIGN_ORIGIN, node, assignOriginToDestinationSelectorsMap);
            collectNodes(map, ApiMethodSelector.FILTER, node, filterSelectorMap);
            collectNodes(map, ApiMethodSelector.GENERATE, node, generatorSelectorMap);
            collectNodes(map, ApiMethodSelector.IGNORE, node, ignoredSelectorMap);
            collectNodes(map, ApiMethodSelector.ON_COMPLETE, node, onCompleteCallbackSelectorMap);
            collectNodes(map, ApiMethodSelector.SET_MODEL, node, setModelSelectorMap);
            collectNodes(map, ApiMethodSelector.SET, node, generatorSelectorMap);
            collectNodes(map, ApiMethodSelector.SUBTYPE, node, subtypeSelectorMap);
            collectNodes(map, ApiMethodSelector.SUPPLY, node, generatorSelectorMap);
            collectNodes(map, ApiMethodSelector.WITH_NULLABLE, node, nullableSelectorMap);
            collectNodes(map, ApiMethodSelector.WITH_UNIQUE, node, filterSelectorMap);
            queue.addAll(node.getChildren());
        }
        return map;
    }

    private static void collectNodes(
            final Map<ApiMethodSelector, Map<TargetSelector, Set<InternalNode>>> resultsMap,
            final ApiMethodSelector apiMethodSelector,
            final InternalNode node,
            final SelectorMap<?> selectorMap) {

        Map<TargetSelector, Set<InternalNode>> selectorNodeMap = resultsMap
                .computeIfAbsent(apiMethodSelector, k -> new LinkedHashMap<>());

        final Set<TargetSelector> selectors = selectorMap.getSelectors(node);

        for (TargetSelector selector : selectors) {
            final InternalSelector internalSelector = (InternalSelector) selector;
            if (internalSelector.getApiMethodSelector() == apiMethodSelector) {
                Set<InternalNode> nodes = selectorNodeMap.computeIfAbsent(selector, k -> new LinkedHashSet<>());
                nodes.add(node);
            }
        }
    }

    /**
     * {@link SelectorMap} marks keys (selectors) that have an associated value
     * when doing a lookup. Therefore, this method must be called after the root
     * object has been created (all map lookups have been done).
     */
    public List<TargetSelector> getUnusedSelectors() {
        List<TargetSelector> results = new ArrayList<>();
        results.addAll(assignDestinationToAssignmentsMap.getUnusedKeys());
        results.addAll(assignOriginToDestinationSelectorsMap.getUnusedKeys());
        results.addAll(feedSelectorMap.getUnusedKeys());
        results.addAll(filterSelectorMap.getUnusedKeys());
        results.addAll(generatorSelectorMap.getUnusedKeys());
        results.addAll(ignoredSelectorMap.getUnusedKeys());
        results.addAll(nullableSelectorMap.getUnusedKeys());
        results.addAll(onCompleteCallbackSelectorMap.getUnusedKeys());
        results.addAll(setModelSelectorMap.getUnusedKeys());
        results.addAll(subtypeSelectorMap.getUnusedKeys());
        return results;
    }
}
