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
package org.instancio.internal.nodes;

import org.instancio.internal.RootType;
import org.instancio.internal.util.ObjectUtils;
import org.instancio.internal.util.TypeUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Helper class for mapping type variables to actual type arguments.
 */
public final class NodeTypeMap {

    private final RootType rootType;
    private final Map<Type, Type> typeMap;

    NodeTypeMap(@NotNull final Type genericType,
                @NotNull final RootType rootType,
                @NotNull final Map<Type, Type> subtypeMappingTypeMap) {

        this.rootType = rootType;
        this.typeMap = buildTypeMap(genericType, subtypeMappingTypeMap);
    }

    public RootType getRootType() {
        return rootType;
    }

    public Type get(final Type type) {
        return typeMap.get(type);
    }

    public Type getOrDefault(final Type type, final Type defaultValue) {
        return typeMap.getOrDefault(type, defaultValue);
    }

    public int size() {
        return typeMap.size();
    }

    /**
     * Some possible field declarations and their generic types:
     *
     * <pre>{@code
     *   Type                | Generic type class
     *   --------------------+-------------------
     *   int                 | Class
     *   Integer             | Class
     *   Item<Integer>       | ParameterizedType
     *   Item<T>             | ParameterizedType
     *   T                   | TypeVariable
     *   Item<?>             | WildcardType
     *   Item<? extends Foo> | WildcardType
     * }</pre>
     *
     * @param type                  to build the type map for
     * @param subtypeMappingTypeMap map type parameters of supertype to type parameters of subtype
     * @return type map
     */
    private Map<Type, Type> buildTypeMap(final Type type, final Map<Type, Type> subtypeMappingTypeMap) {
        final Map<Type, Type> map = new HashMap<>(subtypeMappingTypeMap);

        if (type instanceof Class) {
            return map;
        }

        if (type instanceof TypeVariable) {
            final Type mappedType = rootType.getTypeMapping(type);
            if (mappedType != null) {
                map.put(type, mappedType);
                return map;
            }
        }

        if (type instanceof ParameterizedType) {
            final Class<?> rawType = TypeUtils.getRawType(type);
            final Type[] typeArgs = ((ParameterizedType) type).getActualTypeArguments();
            final TypeVariable<?>[] typeVars = rawType.getTypeParameters();

            for (int i = 0; i < typeArgs.length; i++) {
                final Type mappedType = resolveTypeMapping(typeArgs[i]);
                // Mapped type can be null when a type variable isn't in the root type map.
                // In this case with map type variable to type variable
                map.put(typeVars[i], ObjectUtils.defaultIfNull(mappedType, typeArgs[i]));
            }
        }
        return map;
    }

    private Type resolveTypeMapping(final Type type) {
        if (type instanceof Class || type instanceof ParameterizedType || type instanceof GenericArrayType) {
            return type;
        } else if (type instanceof TypeVariable) {
            return rootType.getTypeMapping(type);
        } else if (type instanceof WildcardType) {
            WildcardType wType = (WildcardType) type;
            return resolveTypeMapping(wType.getUpperBounds()[0]); // TODO multiple bounds
        }
        throw new UnsupportedOperationException("Unsupported type: " + type.getClass());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeTypeMap)) return false;
        final NodeTypeMap other = (NodeTypeMap) o;
        return Objects.equals(typeMap, other.typeMap);
    }

    @Override
    public int hashCode() {
        return typeMap == null ? 0 : typeMap.hashCode();
    }
}
