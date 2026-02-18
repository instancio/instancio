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
package org.instancio.internal.nodes;

import org.instancio.internal.RootType;
import org.instancio.internal.util.TypeUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper for resolving type variables, building type maps, etc.
 */
class TypeHelper {
    private static final Logger LOG = LoggerFactory.getLogger(TypeHelper.class);

    private final RootType rootType;

    TypeHelper(final RootType rootType) {
        this.rootType = rootType;
    }

    @Nullable Type resolveTypeVariable(
            final TypeVariable<?> typeVar,
            @Nullable final InternalNode parent) {

        Type mappedType = parent == null ? typeVar : parent.getTypeMap().getOrDefault(typeVar, typeVar);
        InternalNode ancestor = parent;

        while ((mappedType == null || mappedType instanceof TypeVariable) && ancestor != null) {
            Type rootTypeMapping = rootType.getTypeMapping(mappedType);
            if (rootTypeMapping != null) {
                return rootTypeMapping;
            }

            mappedType = ancestor.getTypeMap().getOrDefault(mappedType, mappedType);

            if (mappedType instanceof Class || mappedType instanceof ParameterizedType) {
                break;
            }

            ancestor = ancestor.getParent();
        }
        return mappedType == typeVar ? null : mappedType; // NOPMD
    }

    Map<Type, Type> createSuperclassTypeMap(final Class<?> targetClass) {
        Map<Type, Type> resultTypeMap = new HashMap<>();

        traverseHierarchy(targetClass, resultTypeMap);

        if (resultTypeMap.isEmpty()) {
            return Collections.emptyMap();
        }

        LOG.trace("Created superclass type map: {}", resultTypeMap);
        return resultTypeMap;
    }

    private void traverseHierarchy(Class<?> clazz, Map<Type, Type> resultTypeMap) {
        if (clazz == null || Object.class.equals(clazz)) {
            return;
        }

        Type supertype = clazz.getGenericSuperclass();
        if (supertype instanceof ParameterizedType parameterizedType) {
            addTypeParameters(parameterizedType, resultTypeMap);
        }

        if (supertype != null) {
            traverseHierarchy(TypeUtils.getRawType(supertype), resultTypeMap);
        }
    }

    /**
     * A "bridge type map" is required for performing type substitutions of parameterized types.
     * For example, a subtype may declare a type variable that maps to a type variable declared
     * by the super type. This method provides the "bridge" mapping that allows resolving the actual
     * type parameters.
     * <p>
     * For example, given the following classes:
     *
     * <pre>{@code
     *     interface Supertype<A> {}
     *     class Subtype<B> implements Supertype<B>
     * }</pre>
     * <p>
     * the method returns a map of {@code {B -> A}}
     * <p>
     * NOTE: in its current form, this method only handles the most basic use cases.
     *
     * @param source source type
     * @param target target type
     * @return additional type mappings that might help resolve type variables
     */
    Map<Type, Type> createBridgeTypeMap(final Class<?> source, final Class<?> target) {
        if (source.equals(target)) {
            return Collections.emptyMap();
        }

        final Map<Type, Type> typeMap = new HashMap<>();
        final TypeVariable<?>[] subtypeParams = target.getTypeParameters();
        final TypeVariable<?>[] supertypeParams = source.getTypeParameters();

        if (subtypeParams.length == supertypeParams.length) {
            for (int i = 0; i < subtypeParams.length; i++) {
                typeMap.put(subtypeParams[i], supertypeParams[i]);
            }
        }

        // If subtype has a generic superclass, add its type variables and type arguments to the type map
        final Type supertype = target.getGenericSuperclass();
        if (supertype instanceof ParameterizedType parameterizedType) {
            addTypeParameters(parameterizedType, typeMap);
        }

        return typeMap;
    }

    private static void addTypeParameters(final ParameterizedType parameterizedType, final Map<Type, Type> typeMap) {
        final Class<?> rawSuperclassType = TypeUtils.getRawType(parameterizedType);
        final TypeVariable<?>[] typeVars = rawSuperclassType.getTypeParameters();
        final Type[] typeArgs = parameterizedType.getActualTypeArguments();

        if (typeVars.length == typeArgs.length) {
            for (int i = 0; i < typeVars.length; i++) {
                typeMap.put(typeVars[i], typeArgs[i]);
            }
        }
    }
}
