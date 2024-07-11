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
package org.instancio.internal.util;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class TypeUtils {
    private static final Logger LOG = LoggerFactory.getLogger(TypeUtils.class);

    private TypeUtils() {
        // non-instantiable
    }

    public static int getTypeParameterCount(final Class<?> klass) {
        return klass.isArray()
                ? klass.getComponentType().getTypeParameters().length
                : klass.getTypeParameters().length;
    }

    public static Class<?> getArrayClass(@Nullable final Type type) {
        if (type instanceof Class) {
            final Class<?> klass = (Class<?>) type;

            if (klass.isArray()) {
                return klass;
            }

            return Array.newInstance(klass, 0).getClass();
        } else if (type instanceof ParameterizedType) {
            final Type rawType = ((ParameterizedType) type).getRawType();
            return getArrayClass(rawType);
        } else if (type instanceof GenericArrayType) {
            final GenericArrayType arrayType = (GenericArrayType) type;
            final Type genericComponent = arrayType.getGenericComponentType();
            return Array.newInstance(TypeUtils.getRawType(genericComponent), 0).getClass();
        }
        throw new IllegalArgumentException("Could not resolve array class for type: " + type);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getRawType(final Type type) {
        Verify.notNull(type, "null type");

        if (type instanceof Class) {
            return (Class<T>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<T>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof GenericArrayType) {
            final Type genericComponentType = ((GenericArrayType) type).getGenericComponentType();
            return getRawType(genericComponentType);
        }

        LOG.warn("Unhandled type: {}", type.getClass().getSimpleName());
        return null;
    }

    @Nullable
    public static Class<?> getGenericSuperclassTypeArgument(final Class<?> klass) {
        final ParameterizedType type = findParameterizedSupertype(klass);
        return type == null ? null : TypeUtils.getRawType(type.getActualTypeArguments()[0]);
    }

    @Nullable
    private static ParameterizedType findParameterizedSupertype(@Nullable final Class<?> klass) {
        if (klass == null) {
            return null;
        }
        List<Type> supertypes = collectSupertypes(klass);

        for (Type supertype : supertypes) {
            if (supertype instanceof ParameterizedType) {
                return (ParameterizedType) supertype;
            }
        }
        return findParameterizedSupertype(supertypes);
    }

    private static List<Type> collectSupertypes(final Class<?> klass) {
        final Type genericSuperclass = klass.getGenericSuperclass();
        final Type[] genericInterfaces = klass.getGenericInterfaces();

        if (genericSuperclass == null && genericInterfaces.length == 0) {
            return Collections.emptyList();
        }

        List<Type> supertypes = new ArrayList<>();
        if (genericSuperclass != null) {
            supertypes.add(genericSuperclass);
        }
        if (genericInterfaces.length > 0) {
            supertypes.addAll(Arrays.asList(genericInterfaces));
        }
        return supertypes;
    }

    private static ParameterizedType findParameterizedSupertype(final List<Type> types) {
        for (Type type : types) {
            ParameterizedType pType = findParameterizedSupertype(getRawType(type));
            if (pType != null) {
                return pType;
            }
        }
        return null;
    }

    public static Type[] getGenericSuperclassTypeArguments(final Class<?> klass) {
        if (klass.getGenericSuperclass() instanceof ParameterizedType) {
            final ParameterizedType genericSuperclass = (ParameterizedType) klass.getGenericSuperclass();
            return genericSuperclass.getActualTypeArguments();
        }
        return new Type[0];
    }

    @SuppressWarnings("unchecked")
    public static <T, C extends Collection<T>> Class<C> cast(final Class<?> collectionClass) {
        return (Class<C>) collectionClass;
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getFirstTypeArg(final Type type) {
        if (type instanceof Class<?>) {
            return (Class<T>) type;
        }

        final Type firstArg = ((ParameterizedType) type).getActualTypeArguments()[0];
        if (firstArg instanceof TypeVariable<?>) {
            return TypeUtils.getRawType(type);
        }
        return getFirstTypeArg(firstArg);
    }
}
