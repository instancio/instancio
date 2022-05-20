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

import org.instancio.TargetSelector;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.selectors.Flattener;
import org.instancio.internal.selectors.SelectorImpl;
import org.instancio.internal.selectors.SelectorTargetKind;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.instancio.util.ReflectionUtils.getField;

// TODO subtype selectors with scope
public class SubtypeSelectorMap {

    private final Map<Class<?>, Class<?>> classSubtypeMap = new LinkedHashMap<>();
    private final Map<Field, Class<?>> fieldSubtypeMap = new LinkedHashMap<>();
    private final Map<TargetSelector, Class<?>> subtypeSelectors;

    SubtypeSelectorMap(final Map<TargetSelector, Class<?>> subtypeSelectors) {
        this.subtypeSelectors = Collections.unmodifiableMap(subtypeSelectors);
        putAllSubtypeSelectors(subtypeSelectors);
    }

    Map<TargetSelector, Class<?>> getSubtypeSelectors() {
        return subtypeSelectors;
    }

    Optional<Class<?>> getSubtypeMapping(final Class<?> superType) {
        return Optional.ofNullable(classSubtypeMap.get(superType));
    }

    void putAll(final Map<Class<?>, Class<?>> additional) {
        classSubtypeMap.putAll(additional);
    }

    public Optional<Class<?>> getUserSuppliedSubtype(final Class<?> targetClass, @Nullable final Field field) {
        final Class<?> fieldSubtype = fieldSubtypeMap.get(field);
        return fieldSubtype != null
                ? Optional.of(fieldSubtype)
                : Optional.ofNullable(classSubtypeMap.get(targetClass));
    }

    private void putAllSubtypeSelectors(final Map<TargetSelector, Class<?>> groups) {
        groups.forEach((TargetSelector targetSelector, Class<?> subtype) -> {
            for (SelectorImpl selector : ((Flattener) targetSelector).flatten()) {
                if (selector.selectorType() == SelectorTargetKind.FIELD) {
                    final Field field = getField(selector.getTargetClass(), selector.getFieldName());
                    // TODO validate
                    fieldSubtypeMap.put(field, subtype);
                } else {
                    ApiValidator.validateSubtypeMapping(selector.getTargetClass(), subtype);
                    classSubtypeMap.put(selector.getTargetClass(), subtype);
                }
            }
        });
    }

}
