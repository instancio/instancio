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
package org.instancio.generator.specs;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.ValueSpec;

import java.util.Collection;

/**
 * Spec for shuffling elements in a random order,
 * providing the results as a {@code Collection}.
 *
 * @param <T> the type of elements
 * @since 5.0.0
 */
@ExperimentalApi
public interface ShuffleSpec<T> extends ValueSpec<Collection<T>> {

    /**
     * Creates a copy of the specified array and shuffles its elements.
     *
     * @param array containing the elements to shuffle
     * @return spec builder
     * @since 5.0.0
     */
    @ExperimentalApi
    @SuppressWarnings("unchecked")
    ShuffleSpec<T> shuffle(T... array);

    /**
     * Creates a copy of the specified collection and shuffles its elements.
     *
     * @param collection containing the elements to shuffle
     * @return spec builder
     * @since 5.0.0
     */
    @ExperimentalApi
    ShuffleSpec<T> shuffle(Collection<T> collection);
}
