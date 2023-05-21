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
package org.instancio.internal.util;

import org.instancio.exception.InstancioException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class TypeUtils {

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
        }
        if (type instanceof ParameterizedType) {
            final Type rawType = ((ParameterizedType) type).getRawType();
            return getArrayClass(rawType);
        }
        if (type instanceof GenericArrayType) {
            final GenericArrayType arrayType = (GenericArrayType) type;
            final Type genericComponent = arrayType.getGenericComponentType();
            return Array.newInstance(TypeUtils.getRawType(genericComponent), 0).getClass();
        }
        throw new IllegalArgumentException("Not an array: " + type);
    }

    public static Type[] getTypeArguments(final Type parameterizedType) {
        final ParameterizedType pType = (ParameterizedType) parameterizedType;
        return pType.getActualTypeArguments();
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getRawType(final Type type) {
        Verify.notNull(type, "type is null");

        if (type instanceof Class) {
            return (Class<T>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<T>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof GenericArrayType) {
            final Type genericComponentType = ((GenericArrayType) type).getGenericComponentType();
            return getRawType(genericComponentType);
        }
        ExceptionHandler.conditionalFailOnError(() -> {
            throw new InstancioException("Unhandled type: " + type.getClass().getSimpleName());
        });
        return null;
    }

    public static Class<?> getGenericSuperclassTypeArgument(final Class<?> klass) {
        return findParameterizedSupertype(klass)
                .map(t -> t.getActualTypeArguments()[0])
                .map(TypeUtils::getRawType)
                .orElse(null);
    }

    private static Optional<ParameterizedType> findParameterizedSupertype(final Class<?> klass) {
        if (klass == null) {
            return Optional.empty();
        }
        List<Type> supertypes = collectSupertypes(klass);
        Optional<ParameterizedType> parameterizedType = Optional.empty();
        for (Type supertype : supertypes) {
            if (supertype instanceof ParameterizedType) {
                parameterizedType = Optional.of((ParameterizedType) supertype);
                break;
            }
        }
        return parameterizedType.isPresent() ? parameterizedType : findParameterizedSupertype(supertypes);
    }

    private static List<Type> collectSupertypes(final Class<?> klass) {
        List<Type> supertypes = new ArrayList<>();
        if (klass.getGenericSuperclass() != null) {
            supertypes.add(klass.getGenericSuperclass());
        }
        if (klass.getGenericInterfaces().length > 0) {
            supertypes.addAll(Arrays.asList(klass.getGenericInterfaces()));
        }
        return supertypes;
    }

    private static Optional<ParameterizedType> findParameterizedSupertype(final List<Type> types) {
        for (Type type : types) {
            Optional<ParameterizedType> parameterizedSupertype = findParameterizedSupertype(getRawType(type));
            if (parameterizedSupertype.isPresent()) {
                return parameterizedSupertype;
            }
        }
        return Optional.empty();
    }

    public static Type[] getGenericSuperclassTypeArguments(final Class<?> klass) {
        if (klass.getGenericSuperclass() instanceof ParameterizedType) {
            final ParameterizedType genericSuperclass = (ParameterizedType) klass.getGenericSuperclass();
            return genericSuperclass.getActualTypeArguments();
        }
        return new Type[0];
    }
}
