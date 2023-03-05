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
import java.util.StringJoiner;

/**
 * Helper class for mapping type variables to actual type arguments.
 */
public final class TypeMap {

    private final Map<TypeVariable<?>, Class<?>> rootTypeMap;
    private final Map<Type, Type> typeMap;

    public TypeMap(final Type genericType, final Map<TypeVariable<?>, Class<?>> rootTypeMap) {
        this(genericType, rootTypeMap, Collections.emptyMap());
    }

    public TypeMap(final Type genericType,
                   final Map<TypeVariable<?>, Class<?>> rootTypeMap,
                   final Map<Type, Type> subtypeMappingTypeMap) {

        this.rootTypeMap = Collections.unmodifiableMap(rootTypeMap);
        this.typeMap = Collections.unmodifiableMap(buildTypeMap(genericType, subtypeMappingTypeMap));
    }

    public Type get(final Type type) {
        return typeMap.get(type);
    }

    public Type getOrDefault(final Type type, final Type defaultValue) {
        return typeMap.getOrDefault(type, defaultValue);
    }

    public Type getActualType(final Type type) {
        return typeMap.get(type);
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
     * @param genericType           to build the type map for
     * @param subtypeMappingTypeMap map type parameters of supertype to type parameters of subtype
     * @return type map
     */
    private Map<Type, Type> buildTypeMap(final Type genericType, final Map<Type, Type> subtypeMappingTypeMap) {
        final Map<Type, Type> map = new HashMap<>(subtypeMappingTypeMap);

        if (genericType instanceof Class) {
            return map;
        }

        if (genericType instanceof TypeVariable && rootTypeMap.containsKey(genericType)) {
            final Class<?> mappedType = rootTypeMap.get(genericType);
            map.put(genericType, mappedType);
            return map;
        }

        if (genericType instanceof ParameterizedType) {
            final Class<?> rawType = TypeUtils.getRawType(genericType);
            final Type[] typeArgs = TypeUtils.getTypeArguments(genericType);
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

    private Type resolveTypeMapping(final Type typeArg) {
        if (typeArg instanceof Class || typeArg instanceof ParameterizedType || typeArg instanceof GenericArrayType) {
            return typeArg;
        } else if (typeArg instanceof TypeVariable) {
            return rootTypeMap.get(typeArg);
        } else if (typeArg instanceof WildcardType) {
            WildcardType wType = (WildcardType) typeArg;
            return resolveTypeMapping(wType.getUpperBounds()[0]); // TODO multiple bounds
        }
        throw new UnsupportedOperationException("Unsupported type: " + typeArg.getClass());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof TypeMap)) return false;
        final TypeMap other = (TypeMap) o;
        return Objects.equals(rootTypeMap, other.rootTypeMap) && Objects.equals(typeMap, other.typeMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rootTypeMap, typeMap);
    }

    @Override
    public String toString() {
        return new StringJoiner("\n - ", TypeMap.class.getSimpleName() + "[", "]")
                .add("typeMap=" + typeMap)
                .add("rootTypeMap=" + rootTypeMap)
                .toString();
    }
}
