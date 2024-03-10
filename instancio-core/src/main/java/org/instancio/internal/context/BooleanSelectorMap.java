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

import java.util.LinkedHashSet;
import java.util.Set;

public class BooleanSelectorMap {

    private final SelectorMap<Boolean> selectorMap = new SelectorMapImpl<>();
    private final Set<TargetSelector> targetSelectors = new LinkedHashSet<>();

    public void putAll(final Set<TargetSelector> targetSelectors) {
        for (TargetSelector targetSelector : targetSelectors) {
            add(targetSelector);
        }
    }

    void add(final TargetSelector targetSelector) {
        targetSelectors.add(targetSelector);
        selectorMap.put(targetSelector, true);
    }

    public SelectorMap<Boolean> getSelectorMap() {
        return selectorMap;
    }

    public Set<TargetSelector> getTargetSelectors() {
        return targetSelectors;
    }

    public boolean isTrue(final InternalNode node) {
        return Boolean.TRUE.equals(selectorMap.getValue(node).orElse(false));
    }
}
