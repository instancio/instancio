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

import org.instancio.TargetSelector;
import org.instancio.documentation.InternalApi;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.selectors.InternalSelector;
import org.instancio.internal.selectors.PredicateSelectorImpl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * A map that supports looking up values for a given node, taking into account the node's ancestors.
 * <p>
 * The question this map answers is: given a node, which selector(s) can be applied to it?
 * Since selectors can have scopes that are specified top-down, the lookup will traverse up
 * the node tree and selector scopes to determine if a selector can be applied.
 *
 * @param <V> value type
 */
@InternalApi
public interface SelectorMap<V> extends Iterable<SelectorMap.SelectorEntry<V>> {

    record SelectorEntry<V>(InternalSelector selector, V value) {}

    /**
     * Returns all selectors that match given {@code node}.
     *
     * @param node to match selectors against
     * @return selectors matching the node
     */
    Set<TargetSelector> getSelectors(InternalNode node);

    void forEach(BiConsumer<TargetSelector, ? super V> action);

    void put(TargetSelector targetSelector, V value);

    Set<TargetSelector> getUnusedKeys();

    /**
     * Returns last value for given node (in the order values were added).
     *
     * @param node for which to look up the value
     * @return value for given node, if present
     */
    Optional<V> getValue(InternalNode node);

    /**
     * Returns all matches for the given node in precedence order (the first element
     * is what {@link #getValue(InternalNode)} would return), each paired with the
     * selector that produced it.
     *
     * @param node for which to look up the matches
     * @return the matched selectors and their values
     */
    List<Match<V>> getMatches(InternalNode node);

    /**
     * A matched selector and the value it was registered with.
     */
    record Match<V>(PredicateSelectorImpl selector, V value) {}

    /**
     * Returns all values for given node.
     *
     * @param node for which to look up the values
     * @return all values for given node, or an empty list if none found
     */
    List<V> getValues(InternalNode node);

    List<V> getValues(TargetSelector selector);

    Optional<V> getActiveElementOfValue(InternalNode node);

    boolean isEmpty();

    void markSelectorUsed(TargetSelector selector);
}
