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
package org.instancio.internal.nodes;

import org.instancio.internal.util.ObjectUtils;
import org.instancio.internal.util.TypeUtils;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Helper class for mapping type variables to actual type arguments.
 */
public final class TypeMap {

    private final Map<TypeVariable<?>, Type> rootTypeMap;
    private final Map<Type, Type> typeVariableMap;

    TypeMap(final Type genericType,
            final Map<TypeVariable<?>, Type> rootTypeMap,
            final Map<Type, Type> subtypeMappingTypeMap,
            final TypeMap copyFrom) {

        this.rootTypeMap = new HashMap<>(copyFrom.rootTypeMap);
        this.rootTypeMap.putAll(rootTypeMap);

        this.typeVariableMap = new HashMap<>(copyFrom.typeVariableMap);
        this.typeVariableMap.putAll(buildTypeMap(genericType, subtypeMappingTypeMap));
    }

    TypeMap(final Type genericType,
            final Map<TypeVariable<?>, Type> rootTypeMap,
            final Map<Type, Type> subtypeMappingTypeMap) {

        this.rootTypeMap = Collections.unmodifiableMap(rootTypeMap);
        this.typeVariableMap = Collections.unmodifiableMap(buildTypeMap(genericType, subtypeMappingTypeMap));
    }

    public Type get(final Type type) {
        return typeVariableMap.get(type);
    }

    public Type getOrDefault(final Type type, final Type defaultValue) {
        return typeVariableMap.getOrDefault(type, defaultValue);
    }

    public int size() {
        return typeVariableMap.size();
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

        if (type instanceof TypeVariable && rootTypeMap.containsKey(type)) {
            final Type mappedType = rootTypeMap.get(type);
            map.put(type, mappedType);
            return map;
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
            return rootTypeMap.get(type);
        } else if (type instanceof WildcardType) {
            WildcardType wType = (WildcardType) type;
            return resolveTypeMapping(wType.getUpperBounds()[0]); // TODO multiple bounds
        }
        throw new UnsupportedOperationException("Unsupported type: " + type.getClass());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof TypeMap)) return false;
        final TypeMap other = (TypeMap) o;
        return Objects.equals(rootTypeMap, other.rootTypeMap)
                && Objects.equals(typeVariableMap, other.typeVariableMap);
    }

    @Override
    public int hashCode() {
        int result = rootTypeMap != null ? rootTypeMap.hashCode() : 0;
        result = 31 * result + (typeVariableMap != null ? typeVariableMap.hashCode() : 0);
        return result;
    }
}
