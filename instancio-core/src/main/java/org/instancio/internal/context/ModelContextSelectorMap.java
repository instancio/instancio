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
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Sonar;

import java.util.Map;

@SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
public class ModelContextSelectorMap {

    private final SelectorMap<ModelContext> selectorMap = new SelectorMapImpl<>();

    void putAll(final Map<TargetSelector, ModelContext> modelContexts) {
        for (Map.Entry<TargetSelector, ModelContext> entry : modelContexts.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    void put(final TargetSelector selector, final ModelContext value) {
        this.selectorMap.put(selector, value);
    }

    public SelectorMap<ModelContext> getSelectorMap() {
        return selectorMap;
    }

    public ModelContext getContext(final InternalNode node) {
        return selectorMap.getValue(node).orElse(null);
    }
}
