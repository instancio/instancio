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
package org.instancio.internal.spi;

import org.instancio.documentation.InternalApi;

import java.util.List;
import java.util.function.Function;

/**
 * Internal SPI that allows integrating 3rd party collections,
 * such as Guava, with internal collection generators.
 *
 * @since 2.0.0
 */
@InternalApi
public interface InternalContainerFactoryProvider {

    /**
     * Returns a function that converts a source object of type
     * {@code S} to the target type {@code T}.
     *
     * @param targetType    the type to be created
     * @param typeArguments type arguments of the source object, or an empty list
     *                      if the source object is not generic
     * @param <S>           source type
     * @param <T>           target type
     * @return conversion function
     */
    <S, T> Function<S, T> createFromOtherFunction(Class<T> targetType, List<Class<?>> typeArguments);

    boolean isContainerClass(Class<?> type);
}
