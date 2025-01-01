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
package org.instancio.internal.context;

import org.instancio.OnCompleteCallback;
import org.instancio.TargetSelector;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Sonar;

import java.util.List;
import java.util.Map;

@SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
class OnCompleteCallbackSelectorMap {

    private final SelectorMap<OnCompleteCallback<?>> selectorMap = new SelectorMapImpl<>();

    void putAll(final Map<TargetSelector, OnCompleteCallback<?>> callbacks) {
        for (Map.Entry<TargetSelector, OnCompleteCallback<?>> entry : callbacks.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    void put(final TargetSelector selector, final OnCompleteCallback<?> value) {
        selectorMap.put(selector, value);
    }

    public SelectorMap<OnCompleteCallback<?>> getSelectorMap() {
        return selectorMap;
    }

    List<OnCompleteCallback<?>> getCallbacks(final InternalNode node) {
        return selectorMap.getValues(node);
    }

}
