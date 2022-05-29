/*
 * Copyright 2022 the original author or authors.
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

import org.instancio.Select;
import org.instancio.TargetSelector;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.selectors.Flattener;
import org.instancio.internal.selectors.SelectorImpl;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class SubtypeSelectorMap {

    private final Map<TargetSelector, Class<?>> subtypeSelectors;
    private final SelectorMap<Class<?>> selectorMap = new SelectorMap<>();

    public SubtypeSelectorMap(final Map<TargetSelector, Class<?>> subtypeSelectors) {
        this.subtypeSelectors = Collections.unmodifiableMap(subtypeSelectors);
        putAll(subtypeSelectors);
    }

    Map<TargetSelector, Class<?>> getSubtypeSelectors() {
        return subtypeSelectors;
    }

    SelectorMap<Class<?>> getSelectorMap() {
        return selectorMap;
    }

    public Optional<Class<?>> getSubtype(final Node node) {
        return selectorMap.getValue(node);
    }

    void putAdditional(final Map<Class<?>, Class<?>> additional) {
        additional.forEach((k, v) -> selectorMap.put((SelectorImpl) Select.all(k), v));
    }

    private void putAll(final Map<TargetSelector, Class<?>> subtypes) {
        subtypes.forEach((TargetSelector targetSelector, Class<?> callback) -> {
            for (SelectorImpl selector : ((Flattener) targetSelector).flatten()) {
                selectorMap.put(selector, callback);
            }
        });
    }
}
