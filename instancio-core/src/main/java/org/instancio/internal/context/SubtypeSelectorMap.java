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

/**
 * Subtype mapping precedence, lowest to highest:
 *
 * <ol>
 *   <li>from {@link org.instancio.settings.Settings}</li>
 *   <li>from {@link org.instancio.InstancioApi#subtype(TargetSelector, Class)}</li>
 *   <li>from Generator, e.g. gen.collection().subtype()</li>
 * </ol>
 * <p>
 * Subtypes from #2 and #3 are stored together in a {@link SelectorMap}.
 * If unused, they should trigger "unused selector" error.
 */
public final class SubtypeSelectorMap {

    // Maintain subtypes from Settings in a separate map, instead of SelectorMap.
    // We do not want subtypes from Settings to trigger "unused selectors" error.
    private final Map<Class<?>, Class<?>> subtypeMappingFromSettings;
    private final Map<TargetSelector, Class<?>> subtypeSelectors;
    private final SelectorMap<Class<?>> selectorMap = new SelectorMap<>();

    public SubtypeSelectorMap(
            final Map<Class<?>, Class<?>> subtypeMappingFromSettings,
            final Map<TargetSelector, Class<?>> subtypeSelectors) {

        this.subtypeMappingFromSettings = subtypeMappingFromSettings;
        this.subtypeSelectors = Collections.unmodifiableMap(subtypeSelectors);

        putAdditional(subtypeSelectors);
    }

    Map<TargetSelector, Class<?>> getSubtypeSelectors() {
        return subtypeSelectors;
    }

    SelectorMap<Class<?>> getSelectorMap() {
        return selectorMap;
    }

    public Optional<Class<?>> getSubtype(final Node node) {
        final Optional<Class<?>> selectorSubtype = selectorMap.getValue(node);
        if (selectorSubtype.isPresent()) {
            return selectorSubtype;
        }
        return Optional.ofNullable(subtypeMappingFromSettings.get(node.getRawType()));
    }


    void putAll(final Map<Class<?>, Class<?>> subtypeMapping) {
        subtypeMapping.forEach((k, v) -> selectorMap.put((SelectorImpl) Select.all(k), v));
    }

    private void putAdditional(final Map<TargetSelector, Class<?>> subtypes) {
        subtypes.forEach((TargetSelector targetSelector, Class<?> subtype) -> {
            for (SelectorImpl selector : ((Flattener) targetSelector).flatten()) {
                selectorMap.put(selector, subtype);
            }
        });
    }
}
