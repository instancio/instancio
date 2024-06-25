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
import org.instancio.schema.Schema;

import java.util.Map;

class SchemaSelectorMap {

    private final SelectorMap<Schema> selectorMap = new SelectorMapImpl<>();

    void putAll(final Map<TargetSelector, Schema> callbacks) {
        for (Map.Entry<TargetSelector, Schema> entry : callbacks.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    void put(final TargetSelector selector, final Schema value) {
        selectorMap.put(selector, value);
    }

    SelectorMap<Schema> getSelectorMap() {
        return selectorMap;
    }

    Schema getSchema(final InternalNode node) {
        return selectorMap.getValue(node).orElse(null);
    }
}
