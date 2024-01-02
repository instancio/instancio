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
package org.instancio.internal.generator;

import org.instancio.documentation.InternalApi;

/**
 * A function for adding objects to a container.
 *
 * @param <T> container type
 * @see ContainerBuildFunction
 * @see ContainerCreateFunction
 * @see InternalContainerHint
 * @since 2.0.0
 */
@InternalApi
public interface ContainerAddFunction<T> {

    /**
     * Adds given arguments to the specified container.
     *
     * @param container arguments will be added to
     * @param arguments to add
     */
    void addTo(T container, Object... arguments);
}
