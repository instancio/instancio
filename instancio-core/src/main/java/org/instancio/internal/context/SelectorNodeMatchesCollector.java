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
    private final SelectorMap<?> filterSelectorMap;
    private final SelectorMap<?> subtypeSelectorMap;
    private final SelectorMap<?> generatorSelectorMap;
    private final SelectorMap<?> assignDestinationToAssignmentsMap;
    private final SelectorMap<?> assignOriginToDestinationSelectorsMap;
    private final SelectorMap<?> setModelSelectorMap;
    private final SelectorMap<?> feedSelectorMap;

    SelectorNodeMatchesCollector(final SelectorMaps selectorMaps) {
        this.ignoredSelectorMap = selectorMaps.getIgnoreSelectorMap().getSelectorMap();
        this.nullableSelectorMap = selectorMaps.getWithNullableSelectorMap().getSelectorMap();
        this.onCompleteCallbackSelectorMap = selectorMaps.getOnCompleteSelectorMap().getSelectorMap();
        this.filterSelectorMap = selectorMaps.getFilterSelectorMap().getSelectorMap();
        this.subtypeSelectorMap = selectorMaps.getSubtypeSelectorMap().getSelectorMap();
        this.generatorSelectorMap = selectorMaps.getGeneratorSelectorMap().getSelectorMap();
        this.assignDestinationToAssignmentsMap = selectorMaps.getAssignmentSelectorMap().getDestinationToAssignmentsMap();
        this.assignOriginToDestinationSelectorsMap = selectorMaps.getAssignmentSelectorMap().getOriginToDestinationSelectorsMap();
        this.setModelSelectorMap = selectorMaps.getSetModelSelectorMap().getSelectorMap();
        this.feedSelectorMap = selectorMaps.getFeedSelectorMap().getSelectorMap();
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
            collectNodes(map, ApiMethodSelector.FILTER_WITH_UNIQUE, node, filterSelectorMap);
            collectNodes(map, ApiMethodSelector.SUBTYPE, node, subtypeSelectorMap);
            collectNodes(map, ApiMethodSelector.GENERATE, node, generatorSelectorMap);
            collectNodes(map, ApiMethodSelector.ASSIGN_DESTINATION, node, assignDestinationToAssignmentsMap);
            collectNodes(map, ApiMethodSelector.ASSIGN_ORIGIN, node, assignOriginToDestinationSelectorsMap);
            collectNodes(map, ApiMethodSelector.SET_MODEL, node, setModelSelectorMap);
            collectNodes(map, ApiMethodSelector.APPLY_FEED, node, feedSelectorMap);
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

        for (TargetSelector selector : selectors) {
            Set<InternalNode> nodes = selectorNodeMap.computeIfAbsent(selector, k -> new LinkedHashSet<>());
            nodes.add(node);
        }
    }

    /**
     * {@link SelectorMap} marks keys (selectors) that have an associated value
     * when doing a lookup. Therefore, this method must be called after the root
     * object has been created (all map lookups have been done).
     */
    public Map<ApiMethodSelector, Set<TargetSelector>> getUnusedSelectors() {
        Map<ApiMethodSelector, Set<TargetSelector>> map = new EnumMap<>(ApiMethodSelector.class);
        map.put(ApiMethodSelector.IGNORE, collectSelectors(ignoredSelectorMap));
        map.put(ApiMethodSelector.WITH_NULLABLE, collectSelectors(nullableSelectorMap));
        map.put(ApiMethodSelector.ON_COMPLETE, collectSelectors(onCompleteCallbackSelectorMap));
        map.put(ApiMethodSelector.FILTER_WITH_UNIQUE, collectSelectors(filterSelectorMap));
        map.put(ApiMethodSelector.SUBTYPE, collectSelectors(subtypeSelectorMap));
        map.put(ApiMethodSelector.GENERATE, collectSelectors(generatorSelectorMap));
        map.put(ApiMethodSelector.ASSIGN_DESTINATION, collectSelectors(assignDestinationToAssignmentsMap));
        map.put(ApiMethodSelector.ASSIGN_ORIGIN, collectSelectors(assignOriginToDestinationSelectorsMap));
        map.put(ApiMethodSelector.SET_MODEL, collectSelectors(setModelSelectorMap));
        map.put(ApiMethodSelector.APPLY_FEED, collectSelectors(feedSelectorMap));
        return map;
    }

    private static Set<TargetSelector> collectSelectors(final SelectorMap<?> selectorMap) {
        return selectorMap.getUnusedKeys();
    }
}
