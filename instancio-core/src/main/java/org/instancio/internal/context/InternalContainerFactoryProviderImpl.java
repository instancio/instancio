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

import org.instancio.internal.spi.InternalContainerFactoryProvider;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

class InternalContainerFactoryProviderImpl implements InternalContainerFactoryProvider {

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <S, T> Function<S, T> createFromOtherFunction(
            final Class<T> targetType,
            final List<Class<?>> typeArguments) {

        Function<?, ?> result = null;

        if (targetType == EnumMap.class) {
            result = (Function<Map<?, ?>, EnumMap<?, ?>>) other -> {
                if (other.isEmpty()) {
                    return new EnumMap(typeArguments.iterator().next());
                }
                return new EnumMap(other);
            };
        }

        return (Function<S, T>) result;
    }

    @Override
    public boolean isContainerClass(final Class<?> type) {
        return false;
    }
}
