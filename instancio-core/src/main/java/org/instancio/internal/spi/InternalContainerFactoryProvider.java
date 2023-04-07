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
import org.instancio.internal.generator.InternalContainerHint;

import java.util.List;
import java.util.function.Function;

/**
 * An SPI that allows populating data structures using a method.
 *
 * @since 2.0.0
 */
@InternalApi
public interface InternalContainerFactoryProvider {

    /**
     * Returns a function that converts a source object of type
     * {@code S} to the target type {@code T}.
     *
     * <p>The {@code typeArguments} is provided for containers that
     * require the type of element to be specified at construction time,
     * for example {@link java.util.EnumMap#EnumMap(Class)}.
     *
     * @param targetType    the type to be created
     * @param typeArguments type arguments of the source object, or an empty list
     *                      if the source object is not a parameterised type
     * @param <S>           source type
     * @param <T>           target type
     * @return conversion function, or {@code null} if not defined
     */
    <S, T> Function<S, T> getMappingFunction(Class<T> targetType, List<Class<?>> typeArguments);

    /**
     * Returns {@code true} if the given {@code type} represents a container.
     * A container is a data structure that is populated using a method,
     * for example {@code Collection.add()} or {@code Optional.of()}
     * If a class is marked as a container, Instancio will not inspect
     * its fields since the object will not be populated via fields.
     *
     * <p>Container generators should return a {@link InternalContainerHint}
     * that specifies how the data structure should be populated.
     *
     * @param type to check
     * @return {@code true} if the type is a container, {@code false} otherwise
     * @see InternalContainerHint
     */
    boolean isContainer(Class<?> type);
}
