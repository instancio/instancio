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

import org.instancio.GetMethodSelector;
import org.instancio.GroupableSelector;
import org.instancio.Scope;
import org.instancio.TargetSelector;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.selectors.MethodReferenceHelper;
import org.instancio.internal.selectors.PredicateSelectorImpl;
import org.instancio.internal.selectors.PrimitiveAndWrapperSelectorImpl;
import org.instancio.internal.selectors.ScopeImpl;
import org.instancio.internal.selectors.SelectorBuilder;
import org.instancio.internal.selectors.SelectorGroupImpl;
import org.instancio.internal.selectors.SelectorImpl;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.TypeUtils;
import org.instancio.internal.util.Verify;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class ModelContextHelper {
    private static final Logger LOG = LoggerFactory.getLogger(ModelContextHelper.class);

    /**
     * Pre-processes selectors based on their type.
     *
     * @param selector to pre-process
     * @return a processed selector
     */
    static TargetSelector preProcess(final TargetSelector selector, final Class<?> rootClass) {
        Verify.notNull(selector, "Selector must not be null");

        if (selector instanceof SelectorGroupImpl) {
            final List<TargetSelector> results = flattenSelectorGroup((SelectorGroupImpl) selector, rootClass);
            return new SelectorGroupImpl(results.toArray(new GroupableSelector[0]));
        } else if (selector instanceof GetMethodSelector<?, ?>) {
            return MethodReferenceHelper.resolve((GetMethodSelector<?, ?>) selector);
        } else if (selector instanceof SelectorImpl) {
            return applyRootClass((SelectorImpl) selector, rootClass);
        } else if (selector instanceof PrimitiveAndWrapperSelectorImpl) {
            final PrimitiveAndWrapperSelectorImpl ps = (PrimitiveAndWrapperSelectorImpl) selector;
            if (ps.isScoped()) {
                final List<Scope> scopes = recreateWithRootClass(rootClass, ps.getPrimitive().getScopes());

                return new PrimitiveAndWrapperSelectorImpl(
                        ps.getPrimitive().toBuilder().scopes(scopes).build(),
                        ps.getWrapper().toBuilder().scopes(scopes).build());
            }

            return selector;
        } else if (selector instanceof PredicateSelectorImpl) {
            // No pre-processing of predicate selectors.
            // They can potentially match anything, so don't need to apply root class.
            return selector;
        } else if (selector instanceof SelectorBuilder) {
            return ((SelectorBuilder) selector).build();
        }

        // should not be reachable
        throw Fail.withFataInternalError("Unhandled selector type: %s", selector.getClass());
    }

    /**
     * Handles field selectors created using only the field's name: {@code field("name")}.
     * This means the field belongs to the root class. However, since no class was specified,
     * the selector's target class will be null, so we must set the root class here
     * (creating a new selector from the original and specifying the root class).
     */
    private static SelectorImpl applyRootClass(final SelectorImpl source, final Class<?> rootClass) {
        if (source.isRoot()) {
            return source;
        } else if (source.getTargetClass() == null) {
            return source.toBuilder()
                    .targetClass(rootClass)
                    .scopes(recreateWithRootClass(rootClass, source.getScopes()))
                    .build();
        } else if (!source.getScopes().isEmpty()) {
            return source.toBuilder()
                    .scopes(recreateWithRootClass(rootClass, source.getScopes()))
                    .build();
        }
        return source;
    }

    private static List<Scope> recreateWithRootClass(final Class<?> rootClass, final List<Scope> scopes) {
        if (scopes.isEmpty()) {
            return Collections.emptyList();
        }

        final List<Scope> results = new ArrayList<>(scopes.size());
        for (Scope scope : scopes) {
            ScopeImpl s = (ScopeImpl) scope;
            if (s.getTargetClass() == null) {
                results.add(new ScopeImpl(rootClass, s.getFieldName(), s.getDepth()));
            } else {
                results.add(scope);
            }
        }
        return results;
    }

    private static List<TargetSelector> flattenSelectorGroup(final SelectorGroupImpl selectorGroup, final Class<?> rootClass) {
        final List<GroupableSelector> selectors = selectorGroup.getSelectors();

        if (selectors.isEmpty()) {
            return Collections.emptyList();
        }

        final List<TargetSelector> results = new ArrayList<>();

        for (GroupableSelector groupMember : selectors) {
            if (groupMember instanceof SelectorImpl) {
                final SelectorImpl selector = applyRootClass((SelectorImpl) groupMember, rootClass);
                results.add(selector);
            } else if (groupMember instanceof PrimitiveAndWrapperSelectorImpl) {
                results.addAll(((PrimitiveAndWrapperSelectorImpl) groupMember).flatten());
            } else {
                throw Fail.withFataInternalError("Unhandled selector type: %s", groupMember.getClass());
            }
        }
        return results;
    }

    static Map<TypeVariable<?>, Type> buildRootTypeMap(
            final Type rootType,
            final List<Type> rootTypeParameters) {

        final Class<?> rootClass = TypeUtils.getRawType(rootType);
        ApiValidator.validateTypeParameters(rootClass, rootTypeParameters);

        final Class<?> targetClass = rootClass.isArray()
                ? rootClass.getComponentType()
                : rootClass;

        final TypeVariable<?>[] typeVariables = targetClass.getTypeParameters();
        final Map<TypeVariable<?>, Type> typeMap = new HashMap<>();

        for (int i = 0; i < typeVariables.length; i++) {
            final TypeVariable<?> typeVariable = typeVariables[i];
            final Type actualType = rootTypeParameters.get(i);
            LOG.trace("Mapping type variable '{}' to '{}'", typeVariable, actualType);
            typeMap.put(typeVariable, actualType);
        }

        populateTypeMapFromGenericSuperclass(rootClass, typeMap);
        return typeMap;
    }

    private static void populateTypeMapFromGenericSuperclass(
            @Nullable final Class<?> rootClass,
            final Map<TypeVariable<?>, Type> typeMap) {

        if (rootClass == null) {
            return;
        }

        final Type gsClass = rootClass.getGenericSuperclass();
        if (gsClass instanceof ParameterizedType) {
            final ParameterizedType genericSuperclass = (ParameterizedType) gsClass;
            final Class<?> rawType = TypeUtils.getRawType(genericSuperclass.getRawType());
            final TypeVariable<?>[] typeParameters = rawType.getTypeParameters();

            final Type[] actualTypeArguments = genericSuperclass.getActualTypeArguments();

            for (int i = 0; i < typeParameters.length; i++) {
                if (actualTypeArguments[i] instanceof TypeVariable) {
                    continue; // leave resolving the type to NodeFactory
                }
                final TypeVariable<?> typeVariable = typeParameters[i];
                final Class<?> actualType = TypeUtils.getRawType(actualTypeArguments[i]);
                LOG.trace("Mapping type variable '{}' to '{}'", typeVariable, actualType);
                typeMap.put(typeVariable, actualType);
            }
        }

        if (gsClass != null) {
            populateTypeMapFromGenericSuperclass(TypeUtils.getRawType(gsClass), typeMap);
        }
    }

    private ModelContextHelper() {
        // non-instantiable
    }
}
