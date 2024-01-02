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

import org.instancio.OnCompleteCallback;
import org.instancio.TargetSelector;
import org.instancio.internal.nodes.InternalNode;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

class OnCompleteCallbackSelectorMap {

    private final Map<TargetSelector, OnCompleteCallback<?>> onCompleteCallbacks;
    private final SelectorMap<OnCompleteCallback<?>> selectorMap;

    OnCompleteCallbackSelectorMap(@NotNull final Map<TargetSelector, OnCompleteCallback<?>> callbacks) {
        this.onCompleteCallbacks = Collections.unmodifiableMap(callbacks);
        this.selectorMap = callbacks.isEmpty() ? SelectorMapImpl.emptyMap() : new SelectorMapImpl<>();
        putAll(callbacks);
    }

    private void putAll(final Map<TargetSelector, OnCompleteCallback<?>> callbacks) {
        for (Map.Entry<TargetSelector, OnCompleteCallback<?>> entry : callbacks.entrySet()) {
            selectorMap.put(entry.getKey(), entry.getValue());
        }
    }

    public SelectorMap<OnCompleteCallback<?>> getSelectorMap() {
        return selectorMap;
    }

    public Map<TargetSelector, OnCompleteCallback<?>> getOnCompleteCallbackSelectors() {
        return onCompleteCallbacks;
    }

    List<OnCompleteCallback<?>> getCallbacks(final InternalNode node) {
        return selectorMap.getValues(node);
    }

}
