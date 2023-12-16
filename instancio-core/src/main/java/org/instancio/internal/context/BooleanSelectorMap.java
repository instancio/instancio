/*
 * Copyright 2022-2023 the original author or authors.
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
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;

public class BooleanSelectorMap {

    private final Set<TargetSelector> targetSelectors;
    private final SelectorMap<Boolean> selectorMap;

    public BooleanSelectorMap(@NotNull final Set<TargetSelector> targetSelectors) {
        this.targetSelectors = Collections.unmodifiableSet(targetSelectors);
        this.selectorMap = targetSelectors.isEmpty() ? SelectorMapImpl.emptyMap() : new SelectorMapImpl<>();
        putAll(targetSelectors);
    }

    private void putAll(final Set<TargetSelector> targetSelectors) {
        for (TargetSelector targetSelector : targetSelectors) {
            selectorMap.put(targetSelector, true);
        }
    }

    public SelectorMap<Boolean> getSelectorMap() {
        return selectorMap;
    }

    public Set<TargetSelector> getOriginSelectors(final InternalNode node) {
        return selectorMap.getSelectors(node);
    }

    Set<TargetSelector> getTargetSelectors() {
        return targetSelectors;
    }

    public boolean isTrue(final InternalNode node) {
        return Boolean.TRUE.equals(selectorMap.getValue(node).orElse(false));
    }
}
