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
package org.instancio.internal;

import org.instancio.documentation.InternalApi;

import java.util.List;

/**
 * Flattens a list composite objects.
 *
 * @param <T> the type of object
 * @since 1.3.0
 */
@InternalApi
public interface Flattener<T> {

    /**
     * Flattens objects into a list.
     *
     * @return a flattened list
     * @since 1.3.0
     */
    List<T> flatten();
}
