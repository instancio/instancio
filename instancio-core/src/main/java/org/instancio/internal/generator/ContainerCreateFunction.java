/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.internal.generator;

import org.instancio.documentation.InternalApi;

/**
 * Creates an instances of a container, or container's builder.
 *
 * @param <T> type of container (or builder)
 * @see ContainerAddFunction
 * @see ContainerBuildFunction
 * @see InternalContainerHint
 * @since 2.0.0
 */
@InternalApi
public interface ContainerCreateFunction<T> {

    /**
     * Returns an instance of a container or its builder.
     *
     * @param arguments if any, required to instantiate an instance
     * @return container (or its builder) instance
     */
    T create(Object... arguments);
}
