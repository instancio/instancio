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

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

public final class SubtypeSelectorMap {

    private final SelectorMap<Type> selectorMap = new SelectorMapImpl<>();

    void putAll(final Map<TargetSelector, ? extends Type> subtypes) {
        for (Map.Entry<TargetSelector, ? extends Type> entry : subtypes.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    void put(final TargetSelector key, final Type value) {
        selectorMap.put(key, value);
    }

    SelectorMap<Type> getSelectorMap() {
        return selectorMap;
    }

    public Optional<Type> getSubtype(final InternalNode node) {
        return selectorMap.getValue(node);
    }
}
