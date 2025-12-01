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
package org.instancio.internal.spi;

import org.instancio.internal.util.ReflectionUtils;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class InternalServiceProviderImpl implements InternalServiceProvider {

    @Override
    public InternalContainerFactoryProvider getContainerFactoryProvider() {
        return new InternalContainerFactoryProviderImpl();
    }

    @Override
    public InternalGetterMethodFieldResolver getGetterMethodFieldResolver() {
        return new InternalGetterMethodFieldResolverImpl();
    }

    static final class InternalGetterMethodFieldResolverImpl implements InternalGetterMethodFieldResolver {
        private static final String GET_PREFIX = "get";
        private static final String IS_PREFIX = "is";

        @Override
        public Field resolveField(final Class<?> declaringClass, final String methodName) {
            if (hasPrefix(GET_PREFIX, methodName)) {
                final Field field = getFieldNameByRemovingPrefix(declaringClass, methodName, GET_PREFIX.length());
                if (field != null) return field;
            } else if (hasPrefix(IS_PREFIX, methodName)) {
                final Field field = getFieldNameByRemovingPrefix(declaringClass, methodName, IS_PREFIX.length());
                if (field != null) return field;
            }
            // class could be using property-style getters (e.g. java record)
            return ReflectionUtils.getFieldOrNull(declaringClass, methodName);
        }

        private static @Nullable Field getFieldNameByRemovingPrefix(
                final Class<?> targetClass,
                final String methodName,
                final int getPrefixLength) {

            final char[] ch = methodName.toCharArray();
            ch[getPrefixLength] = Character.toLowerCase(ch[getPrefixLength]);
            final String filedName = new String(ch, getPrefixLength, ch.length - getPrefixLength);
            return ReflectionUtils.getFieldOrNull(targetClass, filedName);
        }

        private static boolean hasPrefix(final String prefix, final String methodName) {
            return methodName.startsWith(prefix) && methodName.length() > prefix.length();
        }
    }

    private static final class InternalContainerFactoryProviderImpl implements InternalContainerFactoryProvider {

        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public <S, T> Function<S, T> getMappingFunction(
                final Class<T> targetType,
                final List<Class<?>> typeArguments) {

            Function<?, ?> result = null;

            if (targetType == EnumMap.class) {
                result = (Function<Map<?, ?>, Map<?, ?>>) other -> {
                    if (other.isEmpty()) {
                        return new EnumMap(typeArguments.iterator().next());
                    }
                    return new EnumMap(other);
                };
            }

            return (Function<S, T>) result;
        }

        @Override
        public boolean isContainer(final Class<?> type) {
            return false;
        }
    }
}
