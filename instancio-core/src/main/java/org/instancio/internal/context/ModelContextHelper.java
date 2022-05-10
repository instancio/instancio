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
import org.instancio.Selector;
import org.instancio.TargetSelector;
import org.instancio.exception.InstancioException;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.selectors.PrimitiveAndWrapperSelectorImpl;
import org.instancio.internal.selectors.SelectorGroupImpl;
import org.instancio.internal.selectors.SelectorImpl;
import org.instancio.util.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.instancio.util.ExceptionHandler.conditionalFailOnError;
import static org.instancio.util.ObjectUtils.defaultIfNull;

final class ModelContextHelper {
    private static final Logger LOG = LoggerFactory.getLogger(ModelContextHelper.class);

    /**
     * Add root class to every field selector if class is null.
     *
     * @param selector to preprocess
     * @return a processed selector
     */
    static TargetSelector applyRootClass(final TargetSelector selector, final Class<?> rootClass) {
        conditionalFailOnError(() -> Verify.notNull(selector, "null selector"));

        if (selector instanceof SelectorGroupImpl) {
            final List<Selector> selectors = ((SelectorGroupImpl) selector).getSelectors();
            final List<TargetSelector> results = new ArrayList<>();

            for (Selector groupMember : selectors) {
                if (groupMember instanceof SelectorImpl) {
                    final SelectorImpl source = (SelectorImpl) groupMember;
                    final Class<?> targetClass = defaultIfNull(source.getTargetClass(), rootClass);
                    final SelectorImpl selectorWithClass = new SelectorImpl(
                            source.selectorType(), targetClass, source.getFieldName(), source.getScopes());

                    results.add(selectorWithClass);
                } else if (groupMember instanceof PrimitiveAndWrapperSelectorImpl) {
                    results.addAll(((PrimitiveAndWrapperSelectorImpl) groupMember).flatten());
                } else {
                    conditionalFailOnError(() -> {
                        throw new InstancioException("Unhandled selector type: " + groupMember.getClass());
                    });
                }
            }
            return Select.all(results.toArray(new Selector[0]));
        } else if (selector instanceof SelectorImpl) {
            final SelectorImpl source = (SelectorImpl) selector;
            return source.getTargetClass() == null
                    ? new SelectorImpl(source.selectorType(), rootClass, source.getFieldName(), source.getScopes())
                    : source;

        } else if (selector instanceof PrimitiveAndWrapperSelectorImpl) {
            return selector;
        }

        conditionalFailOnError(() -> {
            throw new InstancioException("Unhandled selector type: " + selector.getClass());
        });

        return selector;
    }

    static Map<TypeVariable<?>, Class<?>> buildRootTypeMap(
            final Class<?> rootClass,
            final List<Class<?>> rootTypeParameters) {

        ApiValidator.validateTypeParameters(rootClass, rootTypeParameters);

        final Class<?> targetClass = rootClass.isArray()
                ? rootClass.getComponentType()
                : rootClass;

        final TypeVariable<?>[] typeVariables = targetClass.getTypeParameters();
        final Map<TypeVariable<?>, Class<?>> typeMap = new HashMap<>();

        for (int i = 0; i < typeVariables.length; i++) {
            final TypeVariable<?> typeVariable = typeVariables[i];
            final Class<?> actualType = rootTypeParameters.get(i);
            LOG.trace("Mapping type variable '{}' to '{}'", typeVariable, actualType);
            typeMap.put(typeVariable, actualType);
        }
        return typeMap;
    }

    private ModelContextHelper() {
        // non-instantiable
    }
}
