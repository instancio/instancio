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
import org.instancio.internal.nodes.Node;
import org.instancio.internal.selectors.Flattener;

import java.util.Collections;
import java.util.Set;

class BooleanSelectorMap {

    private final Set<TargetSelector> targetSelectors;
    private final SelectorMap<Boolean> selectorMap = new SelectorMap<>();

    BooleanSelectorMap(final Set<TargetSelector> targetSelectors) {
        this.targetSelectors = Collections.unmodifiableSet(targetSelectors);
        putAll(targetSelectors);
    }

    public SelectorMap<Boolean> getSelectorMap() {
        return selectorMap;
    }

    Set<TargetSelector> getTargetSelectors() {
        return targetSelectors;
    }

    boolean isTrue(final Node node) {
        return Boolean.TRUE.equals(selectorMap.getValue(node).orElse(false));
    }

    private void putAll(final Set<TargetSelector> targetSelectors) {
        for (TargetSelector targetSelector : targetSelectors) {
            for (TargetSelector target : ((Flattener) targetSelector).flatten()) {
                selectorMap.put(target, true);
            }
        }
    }
}
